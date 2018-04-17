package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Test;

import static org.junit.Assert.*;

public class SymBotClientTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);
    SymBotAuth botAuth = new SymBotAuth(config);


    @Test
    public void botClientInitTest(){
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config,botAuth);
        assertNotNull(botClient);
        assertNotNull(botClient.getSymBotAuth());
        SymBotClient botClient1 = SymBotClient.initBot(config,botAuth);
        assertEquals(botClient,botClient1);
        botClient.clearBotClient();
        SymBotClient botClient2 = SymBotClient.initBot(config,botAuth);
        assertNotEquals(botClient,botClient2);
    }
}
