package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.*;
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

public class UsersClient extends APIClient{

    private SymBotClient botClient;

    public UsersClient(SymBotClient client) {
        botClient = client;
    }

    public UserInfo getUserFromUsername(String username) throws SymClientException, NoContentException {
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
        } if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
               return getUserFromUsername(username);
            }
            return null;
        }
        return info;
    }

    public UserInfo getUserFromEmail(String email, Boolean local) throws SymClientException, NoContentException {
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
        }
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getUserFromEmail(email,local);
            }
            return null;
        }
        return info;
    }

    public UserInfo getUserFromId(Long id, Boolean local) throws SymClientException, NoContentException {
        Client client = ClientBuilder.newClient();
        UserInfo info = null;
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERSV3)
                .queryParam("uid", id)
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
        }
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getUserFromId(id,local);
            }
            return null;
        }
        return info;
    }

    public List<UserInfo> getUsersFromIdList(List<Long> idList, Boolean local) throws SymClientException, NoContentException {
        return getUsersV3(null, idList, local);
    }

    public List<UserInfo> getUsersFromEmailList(List<String> emailList, Boolean local) throws SymClientException, NoContentException {
        return getUsersV3(emailList, null, local);
    }



    public List<UserInfo> getUsersV3(List<String> emailList, List<Long> idList, Boolean local) throws SymClientException, NoContentException {
        List<UserInfo> infoList = new ArrayList<>();
        boolean emailBased=false;
        StringBuilder lookUpListString = new StringBuilder();
        if(emailList!=null) {
            if (emailList.isEmpty()) {
                throw new NoContentException("No user sent for lookup");
            }
            emailBased = true;
            lookUpListString.append(emailList.get(0));
            for (int i = 1; i < emailList.size(); i++) {
                lookUpListString.append("," + emailList.get(i));
            }
        } else if (idList!=null){
            if (idList.isEmpty()) {
                throw new NoContentException("No user sent for lookup");
            }
            lookUpListString.append(idList.get(0));
            for (int i = 1; i < idList.size(); i++) {
                lookUpListString.append("," + idList.get(i));
            }
        }
        else{
            throw new NoContentException("No user sent for lookup");
        }

        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETUSERSV3)
                .queryParam(emailBased? "email" :"uid", lookUpListString.toString())
                .queryParam("local", local)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .get();
        if(response.getStatus() == 204){
            return infoList;
        } else if (response.getStatus() == 200) {
            UserInfoList userInfo = response.readEntity(UserInfoList.class);
            infoList = userInfo.getUsers();
        }
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getUsersV3(emailList,idList,local);
            }
            return null;
        }
        return infoList;
    }


}
