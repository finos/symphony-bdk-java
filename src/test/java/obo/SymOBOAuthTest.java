package obo;

import authentication.ISymAuth;
import authentication.SymBotAuth;
import authentication.SymOBOAuth;
import authentication.SymOBOUserAuth;
import clients.SymBotClient;
import clients.SymOBOClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.InboundMessage;
import model.MessageStatus;
import model.OutboundMessage;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;

import static org.junit.Assert.assertNotNull;

public class SymOBOAuthTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/sup-config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);

    @Test
    public void authenticationTest(){
        SymOBOAuth oboAuth = new SymOBOAuth(config);
        oboAuth.sessionAppAuthenticate();
        SymOBOUserAuth auth = oboAuth.getUserAuth("bot.user1");
        SymOBOClient client = SymOBOClient.initOBOClient(config,auth);
        OutboundMessage message = new OutboundMessage();
        message.setMessage("test <emoji shortcode=\"thumbsup\"/>");
        try {
            InboundMessage inboundMessage = client.getMessagesClient().sendMessage(client.getStreamsClient().getUserIMStreamId(client.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId()),message);
            assertNotNull(inboundMessage.getMessage());
            assertNotNull(inboundMessage.getMessageId());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }
}
