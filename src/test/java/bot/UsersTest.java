package bot;

import clients.SymBotClient;
import exceptions.SymClientException;
import model.UserFilter;
import model.UserInfo;
import model.UserSearchResult;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import util.BaseTest;

import javax.ws.rs.core.NoContentException;
import java.util.ArrayList;
import java.util.List;

public class UsersTest extends BaseTest {

    private static SymBotClient botClient;
    private static UserInfo user;

    @BeforeClass
    public static void oneTimeSetUp() {
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
        try {
            user = botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true);
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

    @Test
    public void searchForUser(){
        try {
            UserFilter filter = new UserFilter();
            filter.setLocation("NYC");
            UserSearchResult result = botClient.getUsersClient().searchUsers("scannell",false,0,0,filter);
            Assert.assertTrue(!result.getUsers().isEmpty());
            Assert.assertTrue(result.getUsers().get(0).getDisplayName().toLowerCase().contains("scannell"));
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }
}
