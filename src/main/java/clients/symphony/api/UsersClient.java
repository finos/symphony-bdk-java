package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import model.User;
import model.UserInfo;
import model.UserInfoList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class UsersClient {

    private SymBotClient botClient;

    public UsersClient(SymBotClient client) {
        botClient = client;
    }

    public UserInfo getUserFromUsername(String username) throws NoContentException {
        Client client = ClientBuilder.newClient();
        UserInfo info = null;
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERV2)
                .queryParam("username", username)
                .queryParam("local", true)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .get();
        if(response.getStatus() == 204){
            throw new NoContentException("No user found.");
        } else if (response.getStatus() == 200) {
            info = response.readEntity(UserInfo.class);
        } else {
//            handleErrorStatus(response);

        }
        return info;
    }

    public UserInfo getUserFromEmail(String email, Boolean local) throws NoContentException {
        Client client = ClientBuilder.newClient();
        UserInfo info = null;
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERSV3)
                .queryParam("email", email)
                .queryParam("local", local)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .get();
        if(response.getStatus() == 204){
            throw new NoContentException("No user found.");
        } else if (response.getStatus() == 200) {
            UserInfoList infoList = response.readEntity(UserInfoList.class);
            info = infoList.getUsers().get(0);
        } else {
//            handleErrorStatus(response);

        }
        return info;
    }

    public UserInfo getUserFromId(Long id, Boolean local){
        return null;
    }

    public List<UserInfo> getUsersFromIdList(List<Long> idList, Boolean local){
        return null;
    }

    public List<UserInfo> getUsersFromEmailList(List<Long> emailList, Boolean local){
        return null;
    }



    public List<UserInfo> getUsersV3(String email, List<String> userIds, Boolean local){
        return null;
    }


}
