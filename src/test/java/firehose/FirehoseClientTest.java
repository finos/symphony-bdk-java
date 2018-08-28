package firehose;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.DatafeedEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class FirehoseClientTest {
    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/sup-firehose-config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);
    SymBotAuth botAuth = new SymBotAuth(config);


    @Test
    public void firehoseClientCreateTest() {
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        FirehoseClient firehoseClient = botClient.getFirehoseClient();
        assertNotNull(firehoseClient);
        try {
            assertNotNull(firehoseClient.createFirehose());
        } catch (SymClientException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void firehoseClientReadTest(){
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        FirehoseClient firehoseClient = botClient.getFirehoseClient();
        try {
            String firehoseId = firehoseClient.createFirehose();
            List<DatafeedEvent> firehoseEvents = firehoseClient.readFirehose(firehoseId);
            assertNotNull(firehoseEvents);
            //assertTrue(datafeedEvents.get(0).getPayload().getMessageSent().getMessage().getMessage().contains("test"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
