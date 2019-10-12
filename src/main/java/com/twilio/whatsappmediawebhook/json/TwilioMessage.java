package com.twilio.whatsappmediawebhook.json;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwilioMessage {

    private String ApiVersion;
    private String SmsSid;
    private String SmsStatus;
    private String SmsMessageSid;
    private String NumSegments;
    private String To;
    private String From;
    private String MessageSid;
    private String Body;
    private String AccountSid;
    private Integer NumMedia;

    public String getApiVersion() {
        return ApiVersion;
    }

    public void setApiVersion(String apiVersion) {
        ApiVersion = apiVersion;
    }

    public String getSmsSid() {
        return SmsSid;
    }

    public void setSmsSid(String smsSid) {
        SmsSid = smsSid;
    }

    public String getSmsStatus() {
        return SmsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        SmsStatus = smsStatus;
    }

    public String getSmsMessageSid() {
        return SmsMessageSid;
    }

    public void setSmsMessageSid(String smsMessageSid) {
        SmsMessageSid = smsMessageSid;
    }

    public String getNumSegments() {
        return NumSegments;
    }

    public void setNumSegments(String numSegments) {
        NumSegments = numSegments;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessageSid() {
        return MessageSid;
    }

    public void setMessageSid(String messageSid) {
        MessageSid = messageSid;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getAccountSid() {
        return AccountSid;
    }

    public void setAccountSid(String accountSid) {
        AccountSid = accountSid;
    }

    public Integer getNumMedia() {
        return NumMedia;
    }

    public void setNumMedia(Integer numMedia) {
        NumMedia = numMedia;
    }
}
