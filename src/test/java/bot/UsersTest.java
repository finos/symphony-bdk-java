package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.User;
import model.UserInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;
import java.util.ArrayList;
import java.util.List;

public class UsersTest {

    private String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    private SymConfig config;
    private SymBotAuth botAuth;
    private SymBotClient botClient;
    private UserInfo user;

    @Before
    public void oneTimeSetUp() {
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.loadFromFile(configFilePath);
        botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
        try {
            user = botClient.getUsersClient().getUserFromEmail("manuela.caicedo@example.com",true);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserFromIdTest(){
        UserInfo info = null;
        try {
            info = botClient.getUsersClient().getUserFromId(user.getId(),true);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(info.getId(),user.getId());
    }

    @Test
    public void getUserFromEmailTest(){
        UserInfo info = null;
        try {
            info = botClient.getUsersClient().getUserFromEmail(user.getEmailAddress(),true);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(info.getId(),user.getId());
    }

    @Test
    public void getUserFromUsernameTest(){
        UserInfo info = null;
        try {
            info = botClient.getUsersClient().getUserFromUsername(user.getUsername());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(info.getId(),user.getId());
    }

    @Test
    public void getUsersFromEmailListTest(){
        List<String> emailList = new ArrayList<>();
        emailList.add(user.getEmailAddress());
        List<UserInfo> info = null;
        try {
            info = botClient.getUsersClient().getUsersFromEmailList(emailList,true);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(info.get(0).getId(),user.getId());
    }

    @Test
    public void getUsersFromIdListTest(){
        List<Long> idList = new ArrayList<>();
        idList.add(user.getId());
        List<UserInfo> info = null;
        try {
            info = botClient.getUsersClient().getUsersFromIdList(idList,true);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(info.get(0).getId(),user.getId());
    }
}
