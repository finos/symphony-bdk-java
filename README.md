# symphony-api-client-java
The Java client is built in an event handler architecture. If you are building a bot that listens to conversations, you will only have to implement an interface of a listener with the functions to handle all events that will come through the Data Feed. 

### Install using maven
```xml
    <dependency>
        <groupId>com.symphony.platformsolutions</groupId>
        <artifactId>symphony-api-client-java</artifactId>
        <version>1.0.17</version>
    </dependency>
```
        

## Configuration
Create a config.json file in your project which includes the following properties.
You can exclude sections irrelevant to your project. For example, in a bot project
authenticating via RSA and connecting directly to the pod, agent and key manager, you
can exclude the bot certificate section, all extension app sections and all optional sections.

```json5
{
    // Mandatory section
    "sessionAuthHost": "my-company-name-api.symphony.com",
    "sessionAuthPort": 8444,
    "keyAuthHost": "my-company-name-api.symphony.com",
    "keyAuthPort": 8444,
    "podHost": "my-company-name.symphony.com",
    "podPort": 443,
    "agentHost": "my-company-name.symphony.com",
    "agentPort": 443,
    
    // For bots only
    "botUsername": "my-bot-name",
    "botEmailAddress": "bot@company.com",
    // For bots using RSA authentication
    "botPrivateKeyPath": "/path/to/rsa/private-key/",
    "botPrivateKeyName": "bot-private-key.pem",
    // For bots using certificate authentication
    "botCertPath": "/path/to/bot-cert/",
    "botCertName": "bot-cert.p12",
    "botCertPassword": "bot-cert-password",
    
    // For extension apps only
    "appId": "",
    // For extension apps using RSA authentication
    "appPrivateKeyPath": "",
    "appPrivateKeyName": "",
    // For extension apps using certificate authentication
    "appCertPath": "/path/to/app-cert/",
    "appCertName": "app-cert.p12",
    "appCertPassword": "app-cert-password",
    
    // Optional: If the connection to the pod (but not the agent) needs to run through a proxy
    "podProxyURL": "http://localhost:3128",
    "podProxyUsername": "proxy-username",
    "podProxyPassword": "proxy-password",
    
    // Optional: If the connection to both the pod and the agent needs to run through a proxy
    //           Do not include the podProxy properties if using this
    "proxyURL": "http://localhost:3128",
    "proxyUsername": "proxy-username",
    "proxyPassword": "proxy-password",
    
    // Optional: If the connection to the key manager needs to run through a proxy
    "keyManagerProxyURL": "http://localhost:3128",
    "keyManagerProxyUsername": "proxy-username",
    "keyManagerProxyPassword": "proxy-password",
    
    // Optional: If the SSL connection to any endpoint uses private or self-signed certificates
    "truststorePath": "/path/to/truststore/",
    "truststorePassword": "truststore.pks",
    
    // Optional: To modify the default authentication token refresh period
    "authTokenRefreshPeriod": 30,
    
    // Optional: To modify the default datafeed handling properties
    "datafeedEventsThreadpoolSize": 5,
    "datafeedEventsErrorTimeout": 30
}
```

### Loading configuration
To load the configuration

```
URL url = getClass().getResource("config.json");
SymConfigLoader configLoader = new SymConfigLoader();
SymConfig config = configLoader.loadFromFile(url.getPath());
```
or
```
InputStream configFileStream = getClass().getResourceAsStream("/config.json");
SymConfigLoader configLoader = new SymConfigLoader();
SymConfig config = configLoader.load(configFileStream);
```

## Authentication
To authenticate against the pod and key manager
```
URL url = getClass().getResource("config.json");
SymConfigLoader configLoader = new SymConfigLoader();
SymConfig config = configLoader.loadFromFile(url.getPath());
SymBotAuth botAuth = new SymBotAuth(config);
botAuth.authenticate();
```
or
```
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
