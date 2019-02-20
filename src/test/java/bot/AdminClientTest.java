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
import org.junit.BeforeClass;
import org.junit.Test;
import util.BaseTest;

import javax.ws.rs.core.NoContentException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AdminClientTest extends BaseTest {

    private static SymBotClient botClient;
    private Stream stream;
    private static Long userId1;
    private static Long userId2;
    private static List<Long> userList;
    private String streamId= "1_vv_nyaIQ2OQniAKfkmgX___pu9TLmzdA";

    @BeforeClass
    public static void oneTimeSetUp() {
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

//    @Test
//    public void listUsersTest(){
//        try {
//            List<AdminUserInfo> list = botClient.getAdminClient().listUsers(0, 50);
//            Assert.assertTrue(list.size()==50);
//        } catch (SymClientException e) {
//            e.printStackTrace();
//        }
//    }

    //Works- excluded from tests
//    @Test
//    public void createUserTest(){
//        AdminNewUser newUser = new AdminNewUser();
//
//        AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
//        adminUserAttributes.setAccountType("SYSTEM");
//        adminUserAttributes.setEmailAddress("test-java@example.com");
//        adminUserAttributes.setUserName("testapijavanew");
//        adminUserAttributes.setDisplayName("Java Test User");
//        newUser.setUserAttributes(adminUserAttributes);
//
//        List<String> roles = new ArrayList<>();
//        roles.add("INDIVIDUAL");
//        newUser.setRoles(roles);
//
//        try {
//            AdminUserInfo result = botClient.getAdminClient().createUser(newUser);
//            Assert.assertTrue(result.getUserSystemInfo().getUserId()!=null);
//        } catch (SymClientException e) {
//            e.printStackTrace();
//        }
//
//    }


    @Test
    public void updateUserTest(){
        AdminUserAttributes userAttributes = new AdminUserAttributes();
        userAttributes.setDisplayName("Java Test User Edited");
        AdminUserInfo info = new AdminUserInfo();
        info.setUserAttributes(userAttributes);


        try {
            AdminUserInfo result = botClient.getAdminClient().updateUser(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId(),userAttributes);
            Assert.assertEquals(result.getUserAttributes().getDisplayName(), "Java Test User Edited");
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getAvatarTest(){
        try {
            List<Avatar> avatarList = botClient.getAdminClient().getAvatar(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId());
            Assert.assertTrue(avatarList!=null);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateAvatarTest(){
        try {
            botClient.getAdminClient().updateAvatar(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId(), "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/innovatebot-img.png");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserStatusTest(){
        try {
            String status = botClient.getAdminClient().getUserStatus(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId());
            Assert.assertEquals("ENABLED", status);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateUserStatusTest(){
        try {
            botClient.getAdminClient().updateUserStatus(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId(), "DISABLED");
            String status = botClient.getAdminClient().getUserStatus(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId());
            Assert.assertEquals("DISABLED", status);
            botClient.getAdminClient().updateUserStatus(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId(), "ENABLED");
            status = botClient.getAdminClient().getUserStatus(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId());
            Assert.assertEquals("ENABLED", status);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPodFeatures(){
        try {
            List<String> features = botClient.getAdminClient().listPodFeatures();
            Assert.assertTrue(!features.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getUserFeatures(){
        try {
            List<FeatureEntitlement> features = botClient.getAdminClient().getUserFeatures(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId());
            Assert.assertTrue(!features.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateUserFeatures(){
        List<FeatureEntitlement> entitlements = new ArrayList<>();
        FeatureEntitlement entitlement = new FeatureEntitlement();
        entitlement.setEntitlment("postWriteEnabled");
        entitlement.setEnabled(false);
        entitlements.add(entitlement);
        try {
            botClient.getAdminClient().updateUserFeatures(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId(), entitlements);
            List<FeatureEntitlement> features = botClient.getAdminClient().getUserFeatures(botClient.getUsersClient().getUserFromEmail("test-java@example.com",true).getId());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserAppEntitlements(){
        try {
            List<ApplicationEntitlement> entitlements = botClient.getAdminClient().getUserApplicationEntitlements(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId());
            Assert.assertTrue(!entitlements.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateUserAppEntitlements(){
        try {
            List<ApplicationEntitlement> entitlements = new ArrayList<>();
            ApplicationEntitlement entitlement = new ApplicationEntitlement();
            entitlement.setAppId("mifidRenderer");
            entitlement.setInstall(true);
            entitlement.setListed(true);
            entitlements.add(entitlement);
            List<ApplicationEntitlement> entitlementsupdated =botClient.getAdminClient().updateUserApplicationEntitlements(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId(), entitlements);

        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }
}
