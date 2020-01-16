# Symphony Bot Application

This application is managing all bot interactions from handling bot commands to receiving notifications from external
systems and push them as symphony messages.


## Summary

* [Getting Started](#Getting-Started)
  * [Prerequisites](#Prerequisites)
  * [Setting the service account](#Setting-the-service-account)
  * [POD configuration](#POD-configuration)
  * [Running locally](#Running-locally)
  * [Verify your setup](#Verify-your-setup)
* [Testing commands](#Testing-commands)
  * [Help command](#Help-command)
  * [Hello command](#Hello-command)
  * [Create notification command](#Create-notification-command)
  * [Login command](#Login-command)
  * [Quote command](#Quote-command)
  * [Template command](#Template-command)
  * [Register quote command](#Register-quote-command)
  * [Default response](#Default-response)
* [Testing notifications](#Testing-notifications)
* [Adding bot commands](#Adding-bot-commands)
  * [Command initialization](#Command-initialization)
  * [Default responses](#Default-responses)
  * [Authenticating to external system](#Authenticating-to-external-system)
    * [AuthenticationProvider](#AuthenticationProvider)
    * [AuthenticatedCommandHandler](#AuthenticatedCommandHandler)
  * [Command Matcher](#Command-Matcher)
* [Handling Symphony events](#Handling-Symphony-events)
  * [Available Symphony events](#Available-Symphony-events)
* [Working with Symphony Elements](#Working-with-Symphony-Elements)
  * [ElementsActionHandler](#ElementsActionHandler)
* [Receiving notifications](#Receiving-notifications)
  * [Processing incoming requests](#Processing-incoming-requests)
  * [Controlling interceptors order](#Controlling-interceptors-order)
  * [Forwarding notifications to rooms](#Forwarding-notifications-to-rooms)
* [Extension app integration](#Extension-app-integration) TODO
  * [Extension app authentication](#Extension-app-authentication) TODO
  * [Logging extension app](#Logging-extension-app) TODO
  * [Server-sent events](#Server-sent-events) TODO


## Getting Started

These instructions will allow you to set up your bot application.


### Prerequisites
* JDK 1.8
* Maven 3.0.5+
* Service account configured in Symphony Admin portal
* Extension app setup in Symphony Admin portal (optional)


### Setting the service account

In order to register a service account in Symphony Admin Console, a RSA key pair is required. The bot application uses
the private key while Symphony needs to know the public one.

In addition to the RSA keys, make sure the property botUsername (and appId) in src/main/resources/bot-config.json
file matches the value configured in Symphony Admin Console.


### POD configuration

In src/main/resources/bot-config.json you will find configuration properties where you can specify the details of your 
POD. Fill out the following properties to make the application to point to your POD.

|            Property            |                Description                |
|--------------------------------|-------------------------------------------|
| sessionAuthHost                | The session authorization host URL        |
| sessionAuthPort                | The session authorization host port       |
| keyAuthHost                    | The key manager authorization host URL    |
| keyAuthPort                    | The key manager authorization host port   |
| podHost                        | The Pod host URL                          |
| podPort                        | The Pod host port                         |
| agentHost                      | The Agent host URL                        |
| agentPort                      | The Agent host port                       |
| appId                          | The extension app identification          |
| appPrivateKeyPath              | The extension app private key path        |
| appPrivateKeyName              | The extension app private key file        |
| botPrivateKeyPath              | The bot private key path                  |
| botPrivateKeyName              | The bot private key file                  |
| botUsername                    | The bot username                          |
| authTokenRefreshPeriod         | The authentication token refresh period   |
| authenticationFilterUrlPattern | The authentication filter URL pattern     |
| showFirehoseErrors             | Showing Firehose errors flag              |
| connectionTimeout              | The connection timeout                    |

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
>- @MyBot /template alert {\"title\": \"Title\", \"content\": \"Content\"} - displays a customized alert


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


### Template command

Renders messages using Symphony standardized templates. The supported templates are:
* simple
* alert
* information
* notification
* list
* table

>&#9679; **John Doe**
>
>@MyBot /template simple {"title": "Title", "content": "Content"}

![simple template](readme/template_simple.png)

>&#9679; **John Doe**
>
>@MyBot /template alert {"title": "Title", "content": "Content"}

![alert template](readme/template_alert.png)

>&#9679; **John Doe**
>
>@MyBot /template information {"title": "Title", "content": "Content", "description": "Description"}

![information template](readme/template_information.png)

For more information about the standardized templates, take a look on https://github.com/SymphonyPlatformSolutions/sms-sdk-renderer-java. Also, check [Using Symphony standardized templates](#Using-Symphony-standardized-templates) session.


### Register quote command

Explores the Symphony Elements visual components to display a form for quote registration in Symphony chat.

>&#9679; **John Doe**
>
>@MyBot /register quote

>&#9679; **MyBot**
>

![symphony elements form](readme/quote_registration_form.png)


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

To extend ```CommandHandler``` implement the following methods:

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


### Command initialization

To add initialization logic into a ```CommandHandler``` implementation, override the following method:

* **void init()**: Initialize the instance dependencies.

```java
    @Override
      public void init() {
        // Developer initialization logic
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

* separation of concerns: isolate the authentication logic from command business logic
* code reuse: single authentication method, multiple commands
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

The ```AuthenticatedCommandHandler``` is a specialization of ```CommandHandler``` which interacts with ```AuthenticationProvider``` to retrieve an ```AuthenticationContext``` before invoking the ```handle``` method. All the authentication process is abstracted from the command handler.

If the Symphony user issuing the command is still not authenticated to the external system,  ```AuthenticatedCommandHandler``` will defer to the ```handleUnauthenticated``` method in ```AuthenticationProvider``` and the ```handle``` method will not be invoked.

The ```handle``` method in ```AuthenticatedCommandHandler``` child classes receives an extra parameter, the ```AuthenticationContext``` which contains necessary details to make authenticated requests to the external system.

**Notice:** the application supports multiple ```AuthenticationProvider```. When only one implementation of the ```AuthenticationProvider``` interface is provided, the application will automatically inject it to all ```AuthenticatedCommandHandler``` child classes. Otherwise, you will have to specify which ```AuthenticationProvider``` to use with each ```AuthenticatedCommandHandler``` by annotating the command handlers with the ```CommandAuthenticationProvider``` annotation.

```java
@CommandAuthenticationProvider(name="BasicAuthenticationProvider")
public class LoginCommandHandler extends AuthenticatedCommandHandler {
```


### Command Matcher

In order to avoid writing complex regular expression when specifying the command pattern, the developer can also use ```CommandMatcherBuilder```.

For example, the following pattern...

```java
  Pattern
    .compile("^@BotName\\s/template(?:\\s+(?:([^\\s]+)(?:\\s+([\\s\\S]+)?)?)?)?")
    .asPredicate();
```
would be built this way with ```CommandMatcherBuilder```:

```java
  beginsWith("@")
    .followedBy("BotName")
    .followedBy(whiteSpace())
    .followedBy("/template")
    .followedBy(
      optional(
        nonCapturingGroup(
          oneOrMore(whiteSpace()
          ).followedBy(
            optional(
              nonCapturingGroup(
                group(
                  oneOrMore(
                    negatedSet(whiteSpace())
                    )
                ).followedBy(
                  optional(
                    nonCapturingGroup(
                      oneOrMore(whiteSpace()
                      ).followedBy(
                        optional(
                          group(
                            oneOrMore(any())
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    ).pattern();
```


## Handling Symphony events

Bots may need to react to events happening on Symphony rooms they are part of (e.g. sending a greeting message to users who join the room).

Similarly to commands, Symphony Bot application offers straightforward mechanisms for your bots to be notified when something happens on Symphony chats. By extending the ```EventHandler``` class you register your bot to react to a specific Symphony event.

To extend ```EventHandler``` you need to: 

* specify the event type: ```EventHandler``` is a parameterized class so you need to specified which event type you want to handle. *Refer to the following subsection for the list of supported events*.
 
* implement **void handle(&lt;symphony_event&gt; event, final SymphonyMessage eventResponse)**: this is where you add your business logic to handle the specified event. This method is automatically called when the specified event occurs in a room where the bot is. Use the ```event```object to retrieve event details (e.g. target user or target room). Use the ```SymphonyMessage``` object to format the event response. The application will take care of delivering the response to the correct Symphony room.

```java
public class UserJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {

  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage response) {
    response.setMessage("Hey, <mention uid=\"" + event.getUserId() +
        "\"/>. It is good to have you here!");
  }

```

**Notice:** Different ```EventHandler``` child classes can handle the same event. All of them will have their ```handle```method called. There is no way to set the calling order. 
  

### Available Symphony events

* **IMCreatedEvent**: fired when an IM is created with the bot
* **RoomCreatedEvent**: fired when a room is created with the bot
* **RoomDeactivatedEvent**: fired when room is deactivated
* **RoomReactivatedEvent**: fired when room is reactivated
* **RoomUpdatedEvent**: fired when room is updated
* **UserJoinedRoomEvent**: fired when user joins a room with the bot
* **UserLeftRoomEvent**: fired when user lefts a room with the bot
* **RoomMemberDemotedFromOwnerEvent**: fired when a room member is demoted from room ownership
* **RoomMemberPromotedToOwnerEvent**: fired when a room member is promoted to room owner


### Permission for bots in public rooms 

Symphony Bot Application offers an easy way to control whether your bots are allowed to be added to public rooms or not.

By default, bots built with the Symphony Bot Application are able to join public rooms. To change that behavior, the developer just need to set the ```isPublicRoomAllowed``` in application-feature.yaml file.

It is also possible to configure a custom message the bot would send before quitting the room, through the following configurations:

| Property                                 | Description                                                                  |
|------------------------------------------|------------------------------------------------------------------------------|
| features.isPublicRoomAllowed             | Enablement to allow having bot in public rooms                               |
| features.publicRoomNotAllowedMessage     | The message displayed after the bot removal                                  |
| features.publicRoomNotAllowedTemplate    | The file name of the template of the message displayed after the bot removal |
| features.publicRoomNotAllowedTemplateMap | The template parameters of the message displayed after the bot removal       |

The developer can set a message to be displayed before the bot removal, by setting  ```publicRoomNotAllowedMessage```, like the example below:

 ```yaml
features:
  isPublicRoomAllowed: false
  publicRoomNotAllowedMessage: Sorry, I cannot be added to public rooms
```

or, can use templates to generate structured messages, by setting ```publicRoomNotAllowedTemplate``` and ```publicRoomNotAllowedTemplateMap```, like the following example:

 ```yaml
features:
  isPublicRoomAllowed: false
  publicRoomNotAllowedTemplate: alert
  publicRoomNotAllowedTemplateMap:
    message:
      title: Bot not allowed in public rooms
      content: Sorry, I cannot be added to public rooms
```

## Working with Symphony Elements

Symphony Elements allow bots to send messages containing interactive forms with text fields, dropdown menus, person selectors, buttons and more.

Symphony Bot application fully supports Elements. By extending the ```ElementsHandler``` class you get all you need to handle Symphony Elements, from the command to display the Elements in a chat room to the callback triggered when the Symphony Elements form is submitted. All in one single class.

To extend ```ElementsHandler``` you need to implement the following methods:

* **Predicate&lt;String&gt; getCommandMatcher()**: similar to ```CommandHandler```. Use regular expression to specify the pattern to be used by the application to look for commands in Symphony messages.

* **String getElementsFormId()**: returns the Symphony Elements form ID.

* **void displayElements(BotCommand command, SymphonyMessage elementsResponse)**: This is where you add your logic to render the Symphony Elements form. Similarly to ```CommandHandler```, this method is automatically called when a Symphony message matches the specified command pattern. Use the ```BotCommand``` object to retrieve the command details (e.g. user which triggered it, room where the command was triggered, the raw command line, etc). Use the ```SymphonyMessage``` object to format the Symphony Elements form. The application will take care of delivering the response to the correct Symphony room.

* **void handleAction(SymphonyElementsEvent event, SymphonyMessage elementsResponse)**: where you handle interactions with the Elements form. This method is automatically called when users submit the Elements form. The ```SymphonyElementsEvent``` holds details about the action performed on the form (e.g. form payload, action name, etc). Use the ```SymphonyMessage``` object to format a response according to the Elements form action.

```java
  private static final String FORM_ID = "quo-register-form";
  private static final String FROM_CURRENCY = "fromCurrency";
  private static final String TO_CURRENCY = "toCurrency";
  private static final String AMOUNT = "amount";
  private static final String ASSIGNED_TO = "assignedTo";

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /register quote$")
        .asPredicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  @Override
  public void displayElements(BotCommand command,
      SymphonyMessage elementsResponse) {
    Map<String, String> data = new HashMap<>();
    data.put("form_id", getElementsFormId());
    elementsResponse.setTemplateFile("quote-registration", data);
  }

  @Override
  public void handleAction(SymphonyElementsEvent event,
      SymphonyMessage elementsResponse) {
    Map<String, Object> formValues = event.getFormValues();
    
    Map<String, Object> data = new HashMap<String, Object>();
    data.put(FROM_CURRENCY, formValues.get(FROM_CURRENCY));
    data.put(TO_CURRENCY, formValues.get(TO_CURRENCY));
    data.put(AMOUNT, formValues.get(AMOUNT));
    data.put(ASSIGNED_TO, event.getUser().getDisplayName());

    elementsResponse.setTemplateMessage(
        "Quote FX {{fromCurrency}}-{{toCurrency}} {{amount}} sent to dealer {{assignedTo}}", data);
  }

```

Sample [Handlebars](https://github.com/jknack/handlebars.java)-based template for the quote registration form:
```xml
<form id="{{form_id}}">
  <h3>Quote Registration</h3>
  <h6>From currency</h6>
  <text-field minlength="3" maxlength="3" masked="false" name="fromCurrency" required="true"></text-field>
  <h6>To currency</h6>
  <text-field minlength="3" maxlength="3" masked="false" name="toCurrency" required="true"></text-field>
  <h6>Amount</h6>
  <text-field minlength="1" maxlength="9" masked="false" name="amount" required="true"></text-field>
  <h6>Assigned To:</h6>
  <person-selector name="assignedTo" placeholder="Assign to.." required="false" />
  <h6>Quote Status:</h6>
  <radio name="status" checked="true" value="pending">Pending</radio>
  <radio name="status" checked="false" value="confirmed">Confirmed</radio>
  <radio name="status" checked="false" value="settled">Settled</radio>
  <h6>Remarks:</h6>
  <textarea name="remarks" placeholder="Enter your remarks.." required="false"></textarea>
  <button name="confirm" type="action">Confirm</button>
  <button name="reset" type="reset">Reset</button>
</form>
```

**Notice:** The ```event.getFormValues()``` returns a map with the values of all input fields in the Symphony Elements form. The key names of that map match the HTML ```name``` property of the input elements in Elements form. 


### ElementsActionHandler

For scenarios where the Symphony Elements form is not generated through a command targeted to your bot (e.g. a user interacting with an extension app, a notification from external system) but you need to handle the interactions with that form, extend the ```ElementsActionHandler``` class rather than ```ElementsHandler```. 


## Receiving notifications

Receiving notifications from external systems directly into Symphony chats is another common use case for bots and Symphony Bot application delivers all the support you need by:

1. Exposing the ```/notification``` endpoint through which external systems can send their events: https://&lt;hostname&gt;:&lt;port&gt;/&lt;application_context&gt;/notification

2. Offering mechanisms for you to register your own logic to process incoming notification requests through the ```NotificationInterceptor``` class, including discarding requests when applicable 
   
3. Sending notification contents to Symphony rooms

4. Protecting the ```/notification``` endpoint


### Processing incoming requests

Incoming requests can be easily processed by extending the ```NotificationInterceptor``` class. The way Symphony Bot application supports processing notifications mimics the Filter idea of the Java Servlet specification, that is, you can chain multiple ```NotificationInterceptor``` classes together each one tackling a different aspect of the request, but all of them collaborating to either process or discard the request.

Extending ```NotificationInterceptor``` class will automatically register your interceptor to the application internal ```InterceptorChain```.

To create your own ```NotificationInterceptor``` you simply need to implement the following method:

* **boolean process(NotificationRequest notificationRequest, SymphonyMessage notificationMessage)**: where you add your business logic to process incoming requests (e.g. HTTP header verification, JSON payload mapping, etc). Use the ```NotificationRequest``` to retrieve all details of the notification request (e.g. headers, payload, identifier). You can also use its ```getAttribute```/```setAttribute``` methods to exchange data among your interceptors. Use the ```SymphonyMessage``` object to format the notification. The application will take care of delivering the response to the correct Symphony room. This method is automatically called for each notification request. Return false if the request should be discarded, true otherwise. 

```java
  @Override
  public boolean process(NotificationRequest notificationRequest, SymphonyMessage notificationMessage) {

    // For simplicity of this sample code identifier == streamId
    String streamId = notificationRequest.getIdentifier();

    if (streamId != null) {
      notificationRequest.setStreamId(streamId);
      notificationMessage.setMessage(notificationRequest.getPayload());
      notificationMessage.setMessage(
          "<b>Notification received:</b><br />" + notificationRequest.getPayload());
      return true; // true if notification interception chain should continue
    }

    return false; // false if notification intercept chain should be halted and request rejected
  }

```


### Controlling interceptors order

If you need to specify multiple request interceptors and want to control their execution order, extend the ```OrderedNotificationInterceptor``` rather than ```NotificationInterceptor``` and implement the ```getOrder()``` method.

 
### Forwarding notifications to rooms

The notification support offered by the application suggests using an extra path parameter (that is, ```/notification/<some_value>```) to identify which room a particular notification should be sent to. That extra parameter is internally called as 'identifier' and can be retrieved from the ```NotificationRequest``` object.

By default the application assumes the 'identifier' is the stream ID of the room. If for your scenario 'identifier' means something else or you have a completely different mechanism to identify the room, you must set the stream ID in ```NotificationRequest``` manually.  
     

## Formatting bot messages

The ```SymphonyMessage``` object holds the details for a message to be sent to Symphony. It offers the following different ways to specify the message content:

* **void setMessage(String message)**: specifies a static message to be displayed in a Symphony room.

* **void setTemplateMessage(String templateMessage, Object templateData)**: automatically interpolates a string with template wildcards using the given data object. 

* **void setTemplateFile(String templateFile, Object templateData)**: automatically loads the specified template file and interpolates its content using the given data object. Template files must be placed in your resources directory under ```templates```. 

* **void setEnrichedMessage(String message, String entityName, Object entity, String version)**: similar to ```setMessage``` but offers data to for an extension app to create enriched messages replacing what has been specified as ```message```. If no extension app is registered, the ```message``` gets displayed.

* **void setEnrichedTemplateMessage(String templateMessage, Object templateData, String entityName, Object entity, String version)**: similar to ```setTemplateMessage``` but offers data to for an extension app to create enriched messages replacing what has been specified as ```templateMessage```. If no extension app is registered, the interpolated ```templateMessage``` gets displayed.

* **void setEnrichedTemplateFile(String templateFile, Object templateData, String entityName, Object entity, String version)**: similar to ```setTemplateFile``` but offers data to for an extension app to create enriched messages replacing what has been specified as ```templateFile```. If no extension app is registered, the interpolated content of ```templateFile``` gets displayed.
  
Currently Symphony Bot application is shipped with Freemarker template engine and automatically handles the template content preparation for you.


### Using Symphony standardized templates

The template file methods of ```SymphonyMessage``` (```setTemplateFile``` and ```setEnrichedTemplateFile```) can also be used to render predefined templates. For that, it is necessary to specify a native template wildcards from Symphony standardized templates.

Example:

```java
public class TemplateSampleHandler extends CommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    ...
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    Map<String, Object> commandParameter =
        jsonMapper.toObject("{\"message\": {\"title\": \"Title\", \"content\": \"Content\"}}", Map.class);
    commandResponse.setTemplateFile(SmsRenderer.SmsTypes.ALERT.getName(), commandParameter);
  }

}
```

Currently, Symphony supports the following templates:

* Simple
* Alert
* Information
* Notification
* List
* Table

For more information about the standardized templates, take a look on https://github.com/SymphonyPlatformSolutions/sms-sdk-renderer-java. Also, check [Template command](#Template-command) session.


## Extending health metrics

The Symphony Bot application monitoring system is based on Spring Actuators. By default, it exposes the following health metrics:

* **overall system health status**: represented by the top ```status``` field. It shows 'UP' if all other metrics are fine, that is, 'UP'. 
* **diskspace**: disk space metrics. It shows 'DOWN' when the available disk space is below the defined threshold.
* **symphony**: symphony components metrics. It shows 'UP' only if your bot is properly communicating with the POD and agent and all Symphony components (e.g. agent, Key Manager, POD) are accessible. 

```javascript
{
    "status";:"UP",
    "details";:{
        "diskSpace";:{
            "status";:"UP",
            "details";:{
                "total";:267985612800,
                "free";:110891438080,
                "threshold";:10485760
            }            
        },
        "symphony";:{
            "status";:"UP",
            "details";:{
                "Symphony";:{
                    "agentConnection";:"UP",
                    "podConnection";:"UP",
                    "agentToPodConnection";:"UP",
                    "agentToKMConnection";:"UP",
                    "podVersion";:"1.55.3",
                    "agentVersion";:"2.55.9",
                    "agentToPodConnectionError";:"N/A",
                    "agentToKMConnectionError";:"N/A"
                }
                
            }
        }        
    }    
}
```

There are many other built-in metrics in Spring Actuator. Please refer to their documentation for enabling those metrics. 

To create your own custom metric you need to implement the Spring Actuator ```HealthIndicator``` interface and use ```Health``` builder to convey your status. Your metrics are automatically integrated with the system ones in the monitoring endpoint: http(s)://&lt;hostname&gt;:&lt;port&gt;/&lt;application_context&gt;/monitor/health.

```java
public class InternetConnectivityHealthIndicator implements HealthIndicator {

  private RestClient restClient;

  public InternetConnectivityHealthIndicator(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Health health() {
    try {
      restClient.getRequest("https://symphony.com", String.class);
      return Health.up().withDetail("connectivity", "UP").build();
    } catch (Exception e) {
      return Health.down().withDetail("connectivity", "DOWN").build();
    }
  }

}
```


## Extension app integration

TODO: Add some text here


### Extension app authentication

TODO: Add some text here


### Logging extension app

TODO: Add some text here


### Server-sent events

TODO: Add some text here