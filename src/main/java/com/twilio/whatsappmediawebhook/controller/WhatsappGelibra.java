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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;


@RestController
public class WhatsappGelibra {

    Logger logger = LoggerFactory.getLogger(WhatsappGelibra.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${donateUrl}")
    private String donateUrl;

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


        var newMedia = request.getParameter("newMedia");
        var body = request.getParameter("Body");

        var twimlResponse = new MessagingResponse.Builder();

            System.out.println("body :" + body);

            if (body!=null && !body.isEmpty()) {
                if (body.contains("donation")) {
                    twimlResponse.message(new Message.Builder()
                            .body(new Body.Builder("Hello. I m Sam. Great to meet you. I need your help to save people. I m currently saving refugees in Syria and helping victims of Tsunami in Madagascar")
                                    .build())
                            .build());
                    twimlResponse.message(new Message.Builder()
                            .body(new Body.Builder("Refugees in Syria.\nWould you like to help?").build()).build());
                            //.media(new Media.Builder(Phillipines).build()).build());
                    twimlResponse.message(
                            new Message.Builder()
                                    .body(new Body.Builder("Tsunami victoms in the Madagascar\nWould you like to help ?").build()).build());
                               //     .media(new Media.Builder(Phillipines).build()).build());
                }else if (body.contains("yes") || body.contains("Yes"))
                {
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
                } else if (Integer.parseInt(body)>=0)
                {
                    handleDonation(Integer.valueOf(body));
                    twimlResponse.message(
                            new Message.Builder()
                                    .body(new Body.Builder("Thank you for your donation of "+body+ " Libra").build()).build());
                    // payment
                }
            }

        //TODO handle ammount
//        if (body.contains("10")) {
  //          handleDonation(Integer.valueOf(body));
    //    }

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

    private String handleDonation(Integer amount){
       return restTemplate.getForObject("http://localhost:3000/transfer?amount="+amount, String.class);
    }

}
