package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.DatafeedEvent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.BaseTest;

import javax.ws.rs.core.NoContentException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatafeedClientTest extends BaseTest {

    private static SymBotClient botClient;

    @BeforeClass
    public static void oneTimeSetUp() {
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
    }

    @Test
    public void datafeedClientCreateTest() {
        DatafeedClient datafeedClient = botClient.getDatafeedClient();
        assertNotNull(datafeedClient);
        try {
            assertNotNull(datafeedClient.createDatafeed());
        } catch (SymClientException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void datafeedClientReadTest(){
        DatafeedClient datafeedClient = botClient.getDatafeedClient();
        try {
            String datafeedId = datafeedClient.createDatafeed();
            List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed(datafeedId);
            assertNotNull(datafeedEvents);
            //assertTrue(datafeedEvents.get(0).getPayload().getMessageSent().getMessage().getMessage().contains("test"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
