package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.DatafeedEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatafeedClientTest {
    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);
    SymBotAuth botAuth = new SymBotAuth(config);


    @Test
    public void datafeedClientCreateTest() {
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        DatafeedClient datafeedClient = botClient.getDatafeedClient();
        assertNotNull(datafeedClient);
        try {
            assertNotNull(datafeedClient.createDatafeed());
        } catch (SymClientException e) {
            e.printStackTrace();
        }

    }

//    @Test
//    public void datafeedClientReadTest(){
//        botAuth.authenticate();
//        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
//        DatafeedClient datafeedClient = botClient.getDatafeedClient();
//        String datafeedId = datafeedClient.createDatafeed();
//        try {
//            List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed(datafeedId);
//            assertNotNull(datafeedEvents);
//            assertTrue(datafeedEvents.get(0).getPayload().getMessageSent().getMessage().getMessage().contains("test"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
