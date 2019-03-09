# Vert.x App on Heroku

This is an example of how to set up a basic Vert.x application which
can be run on [Heroku](https://www.heroku.com).

The example is using the Vertx Config Retriever to load the application
configuration from files, Heroku config vars and environment variables.

## Deployment to Heroku
The compiled jar can be deployed to Heroku by using the
[Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli)

Execute following step to deploy the app to heroku. Exchange the
appName with your current app name.

```sh-session
$ mvn clean package heroku:deploy -Dheroku.appName=<appName>
```

## Local Deployment

For development you can also run the jar on your local computer.

```sh-session
$ mvn clean package
$ java -jar target/vertx-heroku-template-0.0.1-fat.jar
```
