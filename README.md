# Symphony Bot Application

This application is managing all bot interactions from handling bot commands to receiving notifications from external
systems and push them as symphony messages.

## Getting Started

These instructions will allow you to set up your bot application.


### Prerequisites
* JDK 1.8
* Maven 3.0.5+
* Service account configured in Symphony Admin portal
* Extension app setup in Symphony Admin portal (optional)


### Setting the service account

In order to register a sevice account in Symphony Admin Console, a RSA key pair is required. The bot application uses
the private key while Symphony needs to know the public one.

In addition to the RSA keys, make sure the property botUsername (and appId) in src/main/resources/bot-config.json
file matches the value configured in Symphony Admin Console.


### POD configuration

In src/main/resources/bot-config.json you will find configuration properties where you can specify the details of your 
POD. Fill out the following properties to make the application to point to your POD.

* sessionAuthHost
* sessionAuthPort
* keyAuthHost
* keyAuthPort
* podHost
* podPort
* agentHost
* agentPort


### Running locally

The Application is built using the Spring Boot Application and uses Maven to manage the dependencies.

1st Step - Install all of the project's dependencies
```
mvn clean install
```
2st Step - Run the application using Maven and Spring Boot
```
mvn clean spring-boot:run
```
To run it in debug mode, provide the following parameters:
```
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```
Bind the IDE of your choice the the specified port (e.g. 5005).


### Verify your setup

Once the application is up and running, you can check if all the setup works properly by navigating to the health check endpoint: http://localhost:8080/your-application-context/monitor/health.

It should return something like:

```
{"status":"UP","details":{"diskSpace":{"status":"UP","details":{"total":267985612800,"free":110894546944,"threshold":10485760}},"symphony":{"status":"UP","details":{"Symphony":{"agentConnection":"UP","podConnection":"UP","agentToPodConnection":"UP","agentToKMConnection":"UP","podVersion":"1.55.3","agentVersion":"2.55.9","agentToPodConnectionError":"N/A","agentToKMConnectionError":"N/A"}}},"internetConnectivity":{"status":"UP","details":{"connectivity":"UP"}}}}
```

 
## Testing commands
Sample commands are shipped with the application as a way to assist developers to understand the mechanics of the application.
If application is properly configured to point to your POD, create an IM or chat room with the bot (search it by the display name you configured in Symphony Admin portal).

All the sample commands require mentioning the bot (e.g. @MyBot), although you can specify any other pattern when creating your own commands.

### Help command

Displays static help message with all available commands

>&#9679; **John Doe**
>
>@MyBot /help

>&#9679; **MyBot**
>
>Bot Commands
>- @MyBot /hello - simple hello command
>- @MyBot /help - displays the list of commands
>- @MyBot /create notification - generates details on how to receive notification in this room
>- @MyBot /login - returns the HTTP authorization header required to talk to external system
>- @MyBot /quote BRL - returns quote for the specified currency (e.g. BRL)
>- @MyBot /register quote - displays the currency quote registration form


### Hello command

Simple hello world command.

>&#9679; **John Doe**
>
>@MyBot /hello

>&#9679; **MyBot**
>
>Hello, **John Doe**


### Create notification command

Returns instructions that you can use to receive notifications from external systems into the given Symphony room. To test it, submit a HTTP POST request to the returned URL.

>&#9679; **John Doe**
>
>@MyBot /create notification

>&#9679; **MyBot**
>
>| Method | Request URL |
>|--|--|
>| POST | http://localhost:8080/myproject/notification/GhaWqOo6jRsHv5adBv4q73___pK2eM94dA |
>
>| Header name | Header value |
>|--|--|
>| Accept | application/json |
>| Content-type | application/json |
>
>**Payload**
>
>Click to expand the sample payload


### Login command

Returns the HTTP header required to perform authenticated requests to external systems. Sample code includes two implementations of the AuthenticationProvider interface representing Basic and OAuth v2 authentication.

>&#9679; **John Doe**
>
>@MyBot /login

>&#9679; **MyBot**
>
>**User authenticated**. Please add the following HTTP header to your requests:
>
>```Authorization: Basic am9obi5kb2VAc3ltcGhvbnkuY29tOnN0cm9uZ3Bhc3M=```


### Quote command

Relies on the RestClient library offered by the application to request quotes for foreigner currencies on a external system.

>&#9679; **John Doe**
>
>@MyBot /quote BRL

>&#9679; **MyBot**
>
>>USD-BRL X-RATE
>>
>>**3.99**<sub>BRL</sub>


### Register quote command

Explores the Symphony Elements visual components to display a form for quote registration in Symphony chat.


### Default response

The application also ships with a mechanism for default responses which sends a default response message in Symphony chat when bot receives an unknown command.

>&#9679; **John Doe**
>
>@MyBot /make coffee

>&#9679; **MyBot**
>
>Sorry, I could not understand


## Testing notifications

The application delivers all support to receive notifications from external systems by exposing an endpoint and offering mechanisms to process incoming requests, the notification interceptors.

A sample notification interceptor is shipped with the application. It simply forwards any JSON payload received by the notification endpoint to the Symphony chat specified in URL path.

To test it follow the instructions of the create notification command. Once the POST request comes in, the JSON payload is printed in the specified Symphony room:

>&#9679; **MyBot**
>
>**Notification received:**

```
{"alert": false,"title": "Something Interesting occurred!","content": {"header": "This is an example of a notification, expand to see more","body": "The SDK comes with ready-to-use message templates that you can use to render messages with your own data. You can add you own templates using the extension application."},"showStatusBar": true,"comment": {"body": "so interesting!"},"description": "this is a brief description","assignee": {"displayName": "John Doe"},"type": {"name": "sample"},"status": {"name": "Awesome"},"priority": {"name": "normal"},"labels": [{"text": "Example"},{"text": "SDK"},{"text": "MS"}]}
```

## Adding bot commands

Easily add commands to your bot by extending the ```CommandHandler``` class (or its subclasses ```AuthenticatedCommandHandler```, ```DefaultCommandHandler``` more on them later).

To extend ```CommandHandler``` all you have to do is to implement the following methods:

* **Predicate&lt;String&gt; getCommandMatcher()**: use regular expression to specify the pattern to be used by the application to look for commands in Symphony messages.

* **void handle(BotCommand command, SymphonyMessage response)**: where you add your business logic to handle the command. This method is automatically called when a Symphony message matches the specified command pattern. Use the ```BotCommand``` object to retrieve the command details (e.g. user which triggered it, room where the command was triggered, the raw command line, etc). Use the ```SymphonyMessage``` object to format the command response. The application will take care of delivering the response to the correct Symphony room.   

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /hello$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    Map<String, String> variables = new HashMap<>();
    variables.put("user", command.getUserDisplayName());

    response.setTemplateMessage("Hello, <b>${user}</b>", variables);
  }
  
```

### Default responses

Typically bots reply to invalid commands with a default friendly message. Extend the ```DefaultCommandHandler``` class to add that behavior to your bots.

Similarly to its base class (i.e. ```CommandHandler```), in ```DefaultCommandHandler``` you will need to provide implementation for both ```getCommandMatcher``` and ```handle``` methods.

Use simple regular expressions to make sure the message was targeted to the bot.

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName())
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    response.setMessage("Sorry, I could not understand");
  }

```

### Authenticating to external system

When integrating with external systems, bots generally need to consume APIs exposed by such systems which require some sort of authentication. 

Symphony Bot application provides mechanisms to support you on that. Through its ```AuthenticationProvider``` interface and ```AuthenticatedCommandHandler``` class the application offers:

* separation of concerns: isolate the authentication logic from business logic
* code reuse: same authentication method, multiple commands
* rapidly replace the authentication method: ```AuthenticationProvider``` changes, commands remain

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /login$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse,
      AuthenticationContext authenticationContext) {

    commandResponse.setMessage("<b>User authenticated</b>. "
        + "Please add the following HTTP header to your requests:"
        + "Authorization: "
        + authenticationContext.getAuthScheme() + " "
        + authenticationContext.getAuthToken());
  }
 
```


#### AuthenticationProvider

To leverage the authentication support offered by the application, provide an implementation of the ```AuthenticationProvider``` interface.

The ```AuthenticationProvider``` interface defines two methods:

* **AuthenticationContext getAuthenticationContext(String userId)**: returns an ```AuthenticationContext``` object which holds authentication details for the given Symphony user.  

* **void handleUnauthenticated(BotCommand command, SymphonyMessage commandResponse)**: invoked when the corresponding Symphony user is still not authenticated to the external system.


#### AuthenticatedCommandHandler

The ```AuthenticatedCommandHandler``` is a specialization of ```CommandHandler``` which interacts with ```AuthenticationProvider``` to retrieve an ```AuthenticationContext``` before invoking the ```handle``` method.

If the Symphony user issuing the command is still not authenticated to the external system,  ```AuthenticatedCommandHandler``` will defer to the ```handleUnauthenticated``` method in ```AuthenticationProvider``` and the ```handle``` method will not be invoked.

The ```handle``` method in ```AuthenticatedCommandHandler``` child classes receives an extra parameter, the ```AuthenticationContext``` which contains necessary details to make authenticated requests to the external system.

**Notice:** if only one implementation of the ```AuthenticationProvider``` interface is provided, the application will automatically inject it to all ```AuthenticatedCommandHandler``` child classes. Otherwise, you will have to specify which ```AuthenticationProvider``` to use with each ```AuthenticatedCommandHandler``` by annotating the command handlers with the ```CommandAuthenticationProvider``` annotation.

```java
@CommandAuthenticationProvider(name="BasicAuthenticationProvider")
public class LoginCommandHandler extends AuthenticatedCommandHandler {
```


## Handling Symphony events

## Working with Symphony elements

## Processing notifications

## Extending health metrics
