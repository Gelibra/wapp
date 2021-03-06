package com.twilio.whatsappmediawebhook.controller;


import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Media;
import com.twilio.twiml.messaging.Message;
import com.twilio.whatsappmediawebhook.json.TwilioMessage;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import java.net.URISyntaxException;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.HttpEntity;


@RestController
public class WhatsappGelibra {

    Logger logger = LoggerFactory.getLogger(WhatsappGelibra.class);
    private static String Syria = "https://github.com/Gelibra/wapp/raw/feature/springboot/src/main/resources/Refugees.jpg";
    private static String Phillipines = "https://github.com/Gelibra/wapp/raw/feature/springboot/src/main/resources/Tsunami2.jpg";



    @Autowired
    RestTemplate restTemplate;

    @Value("${donateUrl}")
    private String donateUrl;

    @Value("${libraAPI}")
    private String libraAPI;

    @Value("${association}")
    private String association;

    @Value("${goodBoyUrl}")
    private String goodBoyUrl;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @PostMapping(consumes = "application/x-www-form-urlencoded;charset=UTF-8", value = "/")
    public void getMedia(

            HttpServletResponse response,
            HttpServletRequest request
    ) throws MimeTypeException, ServletException, IOException, URISyntaxException {
        var sender = request.getParameter("From");
        var body = request.getParameter("Body");

        var twimlResponse = new MessagingResponse.Builder();

        System.out.println("body :" + body);


        if (body != null && !body.isEmpty()) {
            if(body.contains("create")) {
                handleCreation(sender);
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("Your account has been created!").build()).build());
            }
            else if (body.contains("donation")) {
                twimlResponse.message(new Message.Builder()
                        .body(new Body.Builder("Hello. I m Sam. Great to meet you. I need your help to save people. I m currently saving refugees in Syria and helping victims of Tsunami in Madagascar")
                                .build())
                        .build());
                twimlResponse.message(new Message.Builder()
                        .body(new Body.Builder("Refugees in Syria.\nWould you like to help?").build())
                .media(new Media.Builder(Syria).build()).build());
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("Tsunami victims in the Madagascar\nWould you like to help ?").build())
                     .media(new Media.Builder(Phillipines).build()).build());
            } else if (body.contains("yes") || body.contains("Yes")) {
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("Great! Happy to hear. Which cause would you like to support?\nA. Syriam refugees.\nB. Tsunami victims in Magadascar").build())
                                .build());
            } else if (body.contains("A")) {
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("How much would you like to donate to Syrian refugees").build()).build());
            } else if (body.contains("B")) {
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("How much would you like to donate to Tsunami victims in Madagascar").build()).build());
            } else if (Integer.parseInt(body) >= 0) {
                handleDonation(sender, Integer.valueOf(body));
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("Thank you for your donation of " + body + " Libra").build()).build());
                // payment
            } else {
                twimlResponse.message(
                        new Message.Builder()
                                .body(new Body.Builder("I didn't understand, sorry!").build()).build());
            }
        }

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write(twimlResponse.build().toXml());

        out.flush();
        out.close();


    }

    private void downloadFile(String mediaUrl, File file) throws URISyntaxException, IOException {
        var url = new URI(mediaUrl);
        var httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        var get = new HttpGet(url);
        var downloadResp = httpclient.execute(get);
        var source = downloadResp.getEntity().getContent();
        FileUtils.copyInputStreamToFile(source, file);
    }

    private String handleDonation(String sender, Integer amount) {

        System.out.println(sender);
        System.out.println(amount);
        System.out.println(association);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("amount", String.valueOf(amount));
        personJsonObject.put("receiver", association);
        personJsonObject.put("number", sender.split(":")[1]);
        HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);

        return restTemplate.postForObject(libraAPI + "/transaction", request, String.class);
    }

    private String handleCreation(String number) {
        System.out.println(number.split(":")[1]);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("number", String.valueOf(number.split(":")[1]));
        HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
        return restTemplate.postForObject(libraAPI + "/account/create", request, String.class);
    }

}
