# symphony-api-client-java
The Java client is built in an event handler architecture. If you are building a bot that listens to conversations, you will only have to implement an interface of a listener with the functions to handle all events that will come through the Data Feed. 

### Install using maven
```xml
    <dependency>
        <groupId>com.symphony.platformsolutions</groupId>
        <artifactId>symphony-api-client-java</artifactId>
        <version>1.0.14</version>
    </dependency>
```
        

## Configuration
Create a config.json file in your project which includes the following properties
```json
{
  "sessionAuthHost": "COMPANYNAME-api.symphony.com",
  "sessionAuthPort": 8444,
  "keyAuthHost": "COMPANYNAME-api.symphony.com",
  "keyAuthPort": 8444,
  "podHost": "COMPANYNAME.symphony.com",
  "podPort": 443,
  "agentHost": "COMPANYNAME.symphony.com",
  "agentPort": 443,
  "botCertPath": "PATH",
  "botCertName": "BOT-CERT-NAME",
  "botCertPassword": "BOT-PASSWORD",
  "botEmailAddress": "BOT-EMAIL-ADDRESS",
  "appCertPath": "",
  "appCertName": "",
  "appCertPassword": "",
  "proxyURL": "",
  "proxyUsername": "",
  "proxyPassword": "",
  "datafeedEventsThreadpoolSize": 5,
  "datafeedEventsErrorTimeout": 30
}
```
### Loading configuration
To load the configuration

```java
URL url = getClass().getResource("config.json");
SymConfigLoader configLoader = new SymConfigLoader();
SymConfig config = configLoader.loadFromFile(url.getPath());
```
or
```java
InputStream configFileStream = getClass().getResourceAsStream("/config.json");
SymConfigLoader configLoader = new SymConfigLoader();
SymConfig config = configLoader.load(configFileStream);
```

## Authentication
To authenticate against the pod and keymanager
```java
URL url = getClass().getResource("config.json");
SymConfigLoader configLoader = new SymConfigLoader();
SymConfig config = configLoader.loadFromFile(url.getPath());
SymBotAuth botAuth = new SymBotAuth(config);
botAuth.authenticate();
```
or
```java
ClientConfig sessionAuthClientConfig = new ClientConfig();
......
ClientConfig kmAuthClientConfig = new ClientConfig();
......
SymBotAuth botAuth = new SymBotAuth(config, sessionAuthClientConfig, kmAuthClientConfig);

```
        
## Example main class

```java
public class BotExample {

    public static void main(String [] args) {
        BotExample app = new BotExample();
    }
    
    public BotExample() {
        URL url = getClass().getResource("config.json");
        SymConfigLoader configLoader = new SymConfigLoader();
        SymConfig config = configLoader.loadFromFile(url.getPath());
        SymBotAuth botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        DatafeedEventsService datafeedEventsService = botClient.getDatafeedEventsService();
        RoomListener roomListenerTest = new RoomListenerTestImpl(botClient);
        datafeedEventsService.addRoomListener(roomListenerTest);
        IMListener imListener = new IMListenerImpl(botClient);
        datafeedEventsService.addIMListener(imListener);
    }
}
```    
    
## Example RoomListener implementation

```java
public class RoomListenerTestImpl implements RoomListener {

    private SymBotClient botClient;

    public RoomListenerTestImpl(SymBotClient botClient) {
        this.botClient = botClient;
    }

    @Override
    public void onRoomMessage(InboundMessage message) {
        OutboundMessage messageOut = new OutboundMessage();
        messageOut.setMessage("<messageML>Hi "+message.getUser().getFirstName()+"!</messageML>");
        try {
            this.botClient.getMessagesClient().sendMessage(message.getStream().getStreamId(), messageOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomCreated(RoomCreated roomCreated) {

    }

    @Override
    public void onRoomDeactivated(RoomDeactivated roomDeactivated) {

    }

    @Override
    public void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {

    }

    @Override
    public void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner) {

    }

    @Override
    public void onRoomReactivated(Stream stream) {

    }

    @Override
    public void onRoomUpdated(RoomUpdated roomUpdated) {

    }

    @Override
    public void onUserJoinedRoom(UserJoinedRoom userJoinedRoom) {
        OutboundMessage messageOut = new OutboundMessage();
        messageOut.setMessage("<messageML>Welcome "+userJoinedRoom.getAffectedUser().getFirstName()+"!</messageML>");
        try {
            this.botClient.getMessagesClient().sendMessage(userJoinedRoom.getStream().getStreamId(), messageOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserLeftRoom(UserLeftRoom userLeftRoom) {

    }
}
```
