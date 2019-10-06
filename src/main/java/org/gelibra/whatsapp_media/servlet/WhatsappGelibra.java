package org.gelibra.whatsapp_media.servlet;

import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Media;
import com.twilio.twiml.messaging.Message;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


@WebServlet(urlPatterns = {"/"})
public class WhatsappGelibra extends HttpServlet {
    private static String donateUrl = "https://villagegreennj.com/wp-content/uploads/2018/01/m27740143_donate_woman_hugging.jpg";
    private static String goodBoyUrl = "https://c8.alamy.com/comp/D36MEN/port-au-prince-haiti-hatian-red-cross-volunteers-at-a-hilfsgueterverteilung-D36MEN.jpg";

    String FileExtensionForMimeType(String mimeType) {
        try {
            return MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
        } catch (MimeTypeException e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        var body = requestParamsToJSON(request);
        System.out.println("body :"  + body);
        int numMedia = body.getInt("NumMedia");

        if (numMedia > 0) {
            while (numMedia > 0) {
                numMedia = numMedia - 1;

                var mediaUrl = body.getString(String.format("MediaUrl%d", numMedia));
                var contentType = body.getString(String.format("MediaContentType%d", numMedia));
                var fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
                var fileExtension = FileExtensionForMimeType(contentType);
                var file = new File(fileName + fileExtension);

                // Download file
                try {
                    downloadFile(mediaUrl, file);
                } catch (URISyntaxException e) {
                    throw new ServletException(e);
                }
            }
        }

        var twimlResponse = new MessagingResponse.Builder();

        if (body.getInt("NumMedia") > 0) {
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

    void downloadFile(String mediaUrl, File file) throws URISyntaxException, IOException {
        var url = new URI(mediaUrl);
        var httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        var get = new HttpGet(url);
        var downloadResp = httpclient.execute(get);
        var source = downloadResp.getEntity().getContent();
        FileUtils.copyInputStreamToFile(source, file);
    }

    public JSONObject requestParamsToJSON(ServletRequest req) {
        JSONObject jsonObj = new JSONObject();
        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String,String[]> entry : params.entrySet()) {
            String v[] = entry.getValue();
            Object o = (v.length == 1) ? v[0] : v;
            jsonObj.put(entry.getKey(), o);
        }
        return jsonObj;
    }
}
