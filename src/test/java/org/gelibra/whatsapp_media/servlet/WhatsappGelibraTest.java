package org.gelibra.whatsapp_media.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WhatsappGelibraTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleWebhookRequestWithImages() throws ServletException, IOException, URISyntaxException {
        // arrange
        String json =  "{\n" +
            "  \"MediaContentType0\": \"image/jpeg\",\n" +
            "  \"SmsMessageSid\": \"MM19df5a6293470c5e309890648740986a\",\n" +
            "  \"NumMedia\": \"1\",\n" +
            "  \"SmsSid\": \"MM19df5a6293470c5e309890648740986a\",\n" +
            "  \"SmsStatus\": \"received\",\n" +
            "  \"Body\": \"\",\n" +
            "  \"To\": \"whatsapp:+14155238886\",\n" +
            "  \"NumSegments\": \"1\",\n" +
            "  \"MessageSid\": \"MM19df5a6293470c5e309890648740986a\",\n" +
            "  \"AccountSid\": \"AC4ee8a4bf66c95837fc46316395718baa\",\n" +
            "  \"From\": \"whatsapp:+5213321678083\",\n" +
            "  \"MediaUrl0\": \"https://api.twilio.com/2010-04-01/Accounts/AC4ee8a4bf66c95837fc46316395718baa/Messages/MM19df5a6293470c5e309890648740986a/Media/ME456a12de2891e2a69bc11a23aab6b9c5\",\n" +
            "  \"ApiVersion\": \"2010-04-01\"\n" +
            "}";

        var writer = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        when(request.getReader()).thenReturn(
            new BufferedReader(new StringReader(json)));
        when(request.getContentType()).thenReturn("*/*");
        when(request.getCharacterEncoding()).thenReturn("UTF-8");

        var servlet = Mockito.spy(new WhatsappGelibra());
        Mockito.doNothing().when(servlet)
                .downloadFile(Mockito.any(), Mockito.any());

        servlet.doPost(request, response);

        verify(response).setContentType("text/xml");
        var body = writer.toString();

        // assert
        assertEquals(body,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<Response>" +
                "<Message>" +
                    "<Body>Thanks for the image(s)!</Body>" +
                    "<Media>https://images.unsplash.com/photo-1518717758536-85ae29035b6d?ixlib=rb-1.2.1&amp;ixid=eyJhcHBfaWQiOjEyMDd9&amp;auto=format&amp;fit=crop&amp;w=1350&amp;q=80</Media>" +
                "</Message>" +
            "</Response>");
    }

    @Test
    public void handleWebhookRequestWithoutImages() throws ServletException, IOException, URISyntaxException {
        // arrange
        String json =  "{\n" +
            "  \"SmsMessageSid\": \"MM19df5a6293470c5e309890648740986a\",\n" +
            "  \"NumMedia\": \"0\",\n" +
            "  \"SmsSid\": \"MM19df5a6293470c5e309890648740986a\",\n" +
            "  \"SmsStatus\": \"received\",\n" +
            "  \"Body\": \"\",\n" +
            "  \"To\": \"whatsapp:+14155238886\",\n" +
            "  \"NumSegments\": \"1\",\n" +
            "  \"MessageSid\": \"MM19df5a6293470c5e309890648740986a\",\n" +
            "  \"AccountSid\": \"AC4ee8a4bf66c95837fc46316395718baa\",\n" +
            "  \"From\": \"whatsapp:+5213321678083\",\n" +
            "  \"ApiVersion\": \"2010-04-01\"\n" +
            "}";

        var writer = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        when(request.getReader()).thenReturn(
            new BufferedReader(new StringReader(json)));
        when(request.getContentType()).thenReturn("*/*");
        when(request.getCharacterEncoding()).thenReturn("UTF-8");

        var servlet = Mockito.spy(new WhatsappGelibra());
        Mockito.doNothing().when(servlet)
                .downloadFile(Mockito.any(), Mockito.any());

        servlet.doPost(request, response);

        verify(response).setContentType("text/xml");
        var body = writer.toString();

        // assert
        assertEquals(body,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<Response>" +
                "<Message>" +
                    "<Body>Send us an image!</Body>" +
                    "<Media>https://images.unsplash.com/photo-1518717758536-85ae29035b6d?ixlib=rb-1.2.1&amp;ixid=eyJhcHBfaWQiOjEyMDd9&amp;auto=format&amp;fit=crop&amp;w=1350&amp;q=80</Media>" +
                "</Message>" +
            "</Response>");
    }
}