## Note to Developers
* Run `npm install` to install the dependencies

* `npm install express`
* `npm install kulap-libra`
* Launch with `npm start`

Use Postman to try out the GET api requests :

## Change the Mnemonic in each api
* Use only strings with number of words divisible by 6
* A list of allowed mnemonic words can be found under npm_modules -> kulab-libra -> lib -> constants -> MnemonicsWords.ts


## Try Out the API
* /main : Top Up address with 20 libras
* /transfer?amount=XXX : Transfer X amount of libras to the address2 in code
* /balance : Check wallet's balance
