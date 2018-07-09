package bot;

import authentication.ISymAuth;
import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.*;
import model.events.AdminStreamInfoList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AdminClientTest {

    private SymBotClient botClient;
    private Stream stream;
    private Long userId1;
    private Long userId2;
    private List<Long> userList;
    private String streamId= "1_vv_nyaIQ2OQniAKfkmgX___pu9TLmzdA";

    @Before
    public void oneTimeSetUp() {
        String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/dev-config.json";
        SymConfigLoader configLoader = new SymConfigLoader();
        SymConfig config = configLoader.loadFromFile(configFilePath);
        ISymAuth botAuth = new SymBotRSAAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
        try {
            userId1 = botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId();
            userId2 = botClient.getUsersClient().getUserFromEmail("mike.scannell@symphony.com",true).getId();
            userList = new ArrayList<>();
            userList.add(userId1);
            userList.add(userId2);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void adminCreateIMTest(){
        try {
            String id = botClient.getAdminClient().createIM(userList);
            Assert.assertNotNull(id);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void enterpriseStreamsTest(){
        try {
            AdminStreamFilter filter = new AdminStreamFilter();
            List<String> streamTypes = new ArrayList<>();
            streamTypes.add("ROOM");
            filter.setStreamTypes(streamTypes);
            AdminStreamInfoList list = botClient.getAdminClient().listEnterpriseStreams(filter, 0, 100);
            for (AdminStreamInfo item: list.getStreams()) {
                Assert.assertNotNull(item);
            }
            Assert.assertTrue(!list.getStreams().isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importMessageTest(){
        OutboundImportMessageList importMessages = new OutboundImportMessageList();
        OutboundImportMessage message = new OutboundImportMessage();
        message.setIntendedMessageFromUserId(botClient.getBotUserInfo().getId());
        message.setIntendedMessageTimestamp(1530197282);
        message.setMessage("<messageML>import message test</messageML>");
        message.setOriginatingSystemId("api client");
        message.setOriginalMessageId("1");
        importMessages.add(message);
        try {
            message.setStreamId(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com", true).getId()));
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        InboundImportMessageList result = null;
        try {
            result = botClient.getAdminClient().importMessages(importMessages);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(!result.isEmpty());
    }

    @Test
    public void suppressMessageTest(){
        OutboundMessage message = new OutboundMessage();
        message.setMessage("test <hash tag=\"tobesuppressedt\"/>");
        InboundMessage inboundMessage=null;
        try {
            inboundMessage = botClient.getMessagesClient().sendMessage(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com", true).getId()), message);

        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        SuppressionResult result = null;
        try {
            result = botClient.getAdminClient().suppressMessage(inboundMessage.getMessageId());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result.isSuppressed());

    }
}
