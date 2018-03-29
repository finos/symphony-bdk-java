package example;

import authentication.SymBotAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import listeners.RoomListener;
import services.DatafeedEventsService;

public class BotExample {

    String configFilePath = "/Users/manuela.caicedo/Documents/Bots/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);
    SymBotAuth botAuth = new SymBotAuth(config);

    public static void main(String [] args) {
        BotExample app = new BotExample();
    }


    public BotExample() {
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        DatafeedEventsService datafeedEventsService = botClient.getDatafeedEventsService();
        RoomListener roomListenerTest = new RoomListenerTestImpl(botClient);
        datafeedEventsService.addRoomListener(roomListenerTest);

    }
}
