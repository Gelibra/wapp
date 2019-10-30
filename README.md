# wapp
<a href="https://github.com/Gelibra">
  <img src="https://avatars0.githubusercontent.com/u/56204628?s=200&v=4" alt="Gelibra" width="250" />
</a>
# Receive, Send Libra in WhatsApp Messages. 

## Local development

To run the app locally:

1. Clone this repository and open the solution in your preferred IDE.

   ```bash
   git clone git@github.com:Gelibra/wapp.git
   cd wapp
   ```

1. Run the web app.
use java 11
   ```bash
   mvn clean package
   java -jar  whatsappmediawebhook-0.0.1-SNAPSHOT.jar```

1. Launch ngrok http -host-header=localhost 8080
this should give you something like http://01183580.ngrok.io

1. GO to https://www.twilio.com/console/sms/whatsapp/sandbox
and change the url to be http://01183580.ngrok.io
