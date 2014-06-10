### Spring Batch Webinar
---
This repository goes along with the Spring Batch Webinar given June 10th, 2014.  The presentation deck is provided as well as the code from all demos.

#### JSR-352 Hello World!

From the root of the project:

1. `$ ./gradlew clean build` This may be skipped if done previously
2. `$ java -jar JSR-352/build/libs/JSR-352.jar helloWorldJob name=Michael`

#### JSR-352 File to Database

From the root of the project:

1. `$ ./gradlew clean build` This may be skipped if done previously
2. `java -jar JSR-352/build/libs/JSR-352.jar fileToDatabase fileName=<PATH_TO_WORKSPACE>/SpringBatchWebinar/JSR-352/src/main/resources/data/customer.csv` where <PATH_TO_WORKSPACE> is the absolute path to the location you cloned this repository from.


#### Spring Based JSR-352 Job

From the root of the project:

1. `$ ./gradlew clean build` This may be skipped if done previously
2. `$ java -jar SpringBasedJSR-352/build/libs/SpringBasedJSR-352.jar fileToDatabase fileName=<PATH_TO_WORKSPACE/SpringBatchWebinar/JSR-352/src/main/resources/data/customer.csv` where <PATH_TO_WORKSPACE> is the absolute path to the location you cloned this repository from.

#### Spring Batch Integraiton

This example requires a small bit of additional configuration.  In order to poll and post to Twitter, you'll need to supply your Twitter credentials.  To do that:

1. In the directory `<PATH_TO_WORKSPACE/SpringBatchWebinar/SpringBatchIntegration/src/main/resources` create a file named `application.properties`.
2. Add the following to it replacing the values for the first four accordingly:

```
twitter.oauth.consumerKey=foo_consumer_key
twitter.oauth.consumerSecret=foo_consumer_secret
twitter.oauth.accessToken=foo_access_token
twitter.oauth.accessTokenSecret=foo_access_token_secret
spring.batch.job.enabled=false
```

3. `$ ./gradlew clean build` This may be skipped if done previously
4. `$ java -jar SpringBatchIntegration/build/libs/SpringBatchIntegraiton.jar`
5. Login to Twitter.
6. Sent the following tweet where <PATH_TO_INPUT> points to the input file: `fileToDatabase fileName=<PATH_TO_INPUT>`
7. Verify the results on Twitter and in the console.  The job polls once a minute so there will be a bit of a delay.