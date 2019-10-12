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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;


@RestController
public class WhatsappGelibra {

    Logger logger = LoggerFactory.getLogger(WhatsappGelibra.class);


    @Value("${donateUrl:https://villagegreennj.com/wp-content/uploads/2018/01/m27740143_donate_woman_hugging.jpg\"}")
    private String donateUrl;

    @Value("${goodBoyUrl:https://c8.alamy.com/comp/D36MEN/port-au-prince-haiti-hatian-red-cross-volunteers-at-a-hilfsgueterverteilung-D36MEN.jpg}")
    private String goodBoyUrl;

    @RequestMapping("/")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @PostMapping(consumes = "application/json", value = "/media")
    public void getMedia(@RequestBody TwilioMessage twilioMessage,
                         @RequestParam(name = "MediaUrl") String mediaUrl,
                         @RequestParam(name = "MediaContentType") String contentType,
                         HttpServletResponse response
    ) throws MimeTypeException, ServletException, IOException, URISyntaxException {

        Integer numMedia = twilioMessage.getNumMedia();

        var fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
        var fileExtension = MimeTypes.getDefaultMimeTypes().forName(contentType).getExtension();
        var file = new File(fileName + fileExtension);

        // Download file
        downloadFile(mediaUrl, file);


        var twimlResponse = new MessagingResponse.Builder();

        if (numMedia > 0) {
            twimlResponse.message(
                    new Message.Builder()
                            .body(new Body.Builder("Thanks for the Libra(s)!").build())
                            .media(new Media.Builder(goodBoyUrl).build())
                            .build()
            );
        } else {
            twimlResponse.message(
                    new Message.Builder()
                            .body(new Body.Builder("Please send us a libra!").build())
                            .media(new Media.Builder(donateUrl).build())
                            .build()
            );
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

}
