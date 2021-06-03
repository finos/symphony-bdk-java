# Migration guide to Symphony BDK 2.0

This guide provides information about how to migrate from Symphony SDK to BDK 2.0. Migration for the following topics will be detailed here:
- Dependencies
- Bot's configuration
- Symphony BDK entry point
- BDK services
- Event listeners

## Dependencies
In Java SDK, the bot had to have dependencies on `symphony-api-client-java` in addition to the application framework (SpringBoot for e.g). With BDK 2.0, we can replace both of them with `symphony-bdk-core-spring-boot-starter`.
### Spring Boot based project

<table>
<tr>
<th>Java SDK</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.3.5.RELEASE</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>com.symphony.platformsolutions</groupId>
        <artifactId>symphony-api-client-java</artifactId>
        <version>1.3.3</version>
    </dependency>      
</dependencies>
```
</td>
<td>

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.finos.symphony.bdk</groupId>
            <artifactId>symphony-bdk-bom</artifactId>
            <version>2.1.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
<dependency>
    <groupId>org.finos.symphony.bdk</groupId>
    <artifactId>symphony-bdk-core-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
</dependencies>
```
</td>
</tr>
</table>

### No framework based project

<table>
<tr>
<th>Java SDK</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

```xml
<dependencies>
    <dependency>
        <groupId>com.symphony.platformsolutions</groupId>
        <artifactId>symphony-api-client-java</artifactId>
        <version>1.3.3</version>
    </dependency>
</dependencies>
```
</td>
<td>

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.finos.symphony.bdk</groupId>
            <artifactId>symphony-bdk-bom</artifactId>
            <version>2.1.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
<dependency>
    <groupId>org.finos.symphony.bdk</groupId>
    <artifactId>symphony-bdk-core</artifactId>
</dependency>
</dependencies>
```
</td>
</tr>
</table>

## Bot's configuration
In Java SDK, two configuration files were required : `application.yaml` (or `application.config`) and `bot-config.json`. Java BDK 2.0 lightened the configuration. Therefore, only `src/main/resources/config.yaml` file is required with a minimum of configuration.

Bot’s configuration in Java BDK 2.0 should have the following properties:
- `host`: pod’s host name

- `bot.username`: bot’s (or service account) username

- `bot.privatekey.path`: bot’s private key path

If your bot is deployed on premise, the following properties are required as well:

- `agent`: on premise agent configuration

- `keyManager`: on premise Key manager configuration

- `proxy`: proxy configuration to reach the pod

- `ssl.trustStore`: trust store path and password

> Click [here](./configuration.md) for more detailed documentation about BDK configuration

### Minimal configuration example
#### Spring Boot based project
<table>
<tr>
<th>Java SDK</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

#### **`application.yaml`:**
```yaml
server:
    port: 8080
    servlet:
        context-path: "/botapp"

certs: /path/to/private/key
bot-config: /path/to/bot-config.json
```

#### **`bot-config.json`:**
```json
{
  "sessionAuthHost": "session.symphony.com",
  "sessionAuthPort": 443,
  "keyAuthHost": "km.symphony.com",
  "keyAuthPort": 443,
  "podHost": "pod.symphony.com",
  "podPort": 443,
  "agentHost": "agent.symphony.com",
  "agentPort": 443,
  "appId": "myapp",
  "appPrivateKeyPath": "certs/",
  "appPrivateKeyName": "app_private.pkcs8",
  "botPrivateKeyPath": "certs/",
  "botPrivateKeyName": "bot_private.pkcs8",
  "botUsername": "mybot",
  "authTokenRefreshPeriod": "30",
  "authenticationFilterUrlPattern": "/secure/",
  "showFirehoseErrors": false,
  "connectionTimeout": 45000, 
  "proxyURL": "proxy.symphony.com",
  "proxyUsername": "proxy.username",
  "proxyPassword": "proxy.password",
  "keyManagerProxyURL": "km.proxy.symphony.com",
  "keyManagerProxyUsername": "km.proxy.username",
  "keyManagerProxyPassword": "km.proxy.password"
}
```
</td>
<td>

Only `config.yaml`  file is required. It can be in *JSON* 


```json
{
    "host": "acme.symphony.com",
    "bot": {
        "username": "bot-username",
        "privateKey": {
            "path": "/path/to/bot/rsa-privatekey.pem"
        }
    }
}
```

or *YAML* format.

```yaml
host: acme.symphony.com

bot:
  username: bot-username
  privateKey:
    path: /path/to/bot/rsa-privatekey.pem
```
</td>
</tr>
</table>


## Symphony BDK entry point
The `SymphonyBdk` class acts as an entry point into the library and provides a [fluent API](./fluent-api.md) to access to the main BDK features such as [Datafeed](./datafeed.md), services or [Activities](./activity-api.md).
With this class, all BDK services are auto-configured and can be directly accessed without any bot client. Examples of this class usage will be provided in next parts.
> Click [here](./fluent-api.md) for more detailed documentation about Symphony BDK fluent api

## BDK services
If you use a Spring Boot based project, BDK services can be directly injected in your bot service. If it is not a framework based project, BDK services can be retrieved with Symphony BDK entry point.
To illustrate this, let's take an example of a bot reacting to *ping pong* messages.
<table>
<tr>
<th>Java SDK</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

In Java SDK, the main class should have *SymBotClient* object that the bot service can use to call `sendMessage()` method.

```java
@Slf4j
@Service
public class PingPongBotService {
  public handleIncomingMessage(InboundMessage message, StreamTypes streamType) {
      String streamId = message.getStream().getStreamId();
      String messageText. message.getMessageText();

      switch (messageText) {
          case "/ping":
              PingPongBot.sendMessage(streamId, "pong");
              break;
          case "/pong":
              PingPongBot.sendMessage(streamId, "ping");
              break;
      }
  }
}

@Slf4j
public class PingPongBot {
  private static SymBotClient botClient;
  
  public PingPongBot(IMListenerImpl imListener, RoomListenerImpl roomListener, 
                     ElementsListenerImpl elementsListener) {
      try {
          // Bot init
          botClient = SymBotClient.initBotRsa("config.json");
      
          // Bot listeners
          botClient.getDatafeedEventsService().addListeners(imListener, roomListener, 
                  elementsListener);
      } catch (Exception e) {
        log.error("Error: {}", e.getMessage());
      }
  }
  
  public static void sendMessage(String streamId, String message) {
      botClient.getMessageClient.sendMessage(streamId, new OutboundMessage(message));
  }
}
```
</td>
<td>

In Java BDK 2.0, `MessageService` can be injected in the Bot service class to directly call `send()` without needing `SymBotClient`. *(The example below uses a Spring Boot based project)*  

```java
@Slf4j
@Service
public class PingPongBotService {

    private final MessageService messageService;

    public PingPongBotService(MessageService messageService) {
        this.messageService = messageService;
    }

    public handleIncomingMessage(V4Message message, StreamType.TypeEnum streamType) {
        String streamId = message.getStream().getStreamId();
        String messageText = message.getMessage();

        switch (messageText) {
            case "/ping":
                this.messageService.send(streamId, "pong");
                break;
            case "/pong":
                this.messageService.send(streamId, "ping");
                break;
        }
    }
}


@Component
public class RealTimeEventComponent {

    private final PollService pollService;

    public RealTimeEventComponent(PollService pollService) {
        this.pollService = pollService;
    }

    @EventListener
    public void onMessageSent(RealTimeEvent<V4MessageSent> event) {
        this.pollService.handleIncomingMessage(event.getSource().getMessage,
                StreamType.TypeEnum.formValue(event.getSource().getMessage.getStream.getStreamType()));
    }
}
```
</td>
</tr>
</table>

An example of no framework based project using `SymphonyBdk` to retrieve BDK services:
````java
@Slf4j
public class GreetingsAllRoomsBot {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    // list all rooms
    Stream<StreamAttributes> rooms = bdk.streams().listAllStreams(new StreamFilter());
    
    rooms.forEach(streamAttributes -> {
          // send message to room
          bdk.messages().send(streamAttributes.getId(), "Hello world!");
          log.info("Message sent to room with: id:{}, name:{}", streamAttributes.getId(),
              streamAttributes.getRoomAttributes().getName());
    });
    
    bdk.datafeed().start();
  }
}
````
> A list of BDL available services can be found [here](./fluent-api.md)

## Event listeners
Java BDK 2.0 comes with a simplified way to handle event listeners.
<table>
<tr>
<th>Java SDK</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

In Java SDK, the bot had to implement 3 listeners classes: 
- one for IM (1 to 1 conversation)
- one for MIM (room)
- one for Symphony elements
```java
@Slf4j
@Service
public class ElementsListenerImpl implements ElementsListener {
    public void onElementsAction() {...}
}

@Slf4j
@Service
public class ElementsListenerImpl implements ElementsListener {
    public void onIMMessage() {...}
}

@Slf4j
@Service
public class RoomListenerImpl implements RoomListener {
    public void onRoomMessage() {...}
}
```
</td>
<td>

In Java BDK 2.0, only one component `RealTimeEventComponent` has to be implemented with two methods having `@EventListener` annotation. The 3 classes can be factored in one single component. *(The example below uses a Spring Boot based project)* 
```java
@Component
public class RealTimeEventComponent {
    @Autowired
    private MessageService messageService;

    @EventListener
    public void onMessageSent() {...}

    @EventListener
    public onElementsAction() {...}
}
```
</td>
</tr>
</table>

## Models
Some models names have been changed in Java BDK 2.0. This requires to change some variables types names in your legacy bots.
For example : *(non exhaustive list)*
- `SymphonyElementsAction` → `V4SymphonyElementsAction`
- `User` → `V4User`
- `InboundMessage` → `V4Message`
- `StreamTypes` → `StreamType.TypeEnum`
- `RoomInfo` → `V3RoomDetail`
