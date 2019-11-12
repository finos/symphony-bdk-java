package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import model.UserFilter;
import model.UserInfo;
import model.UserInfoList;
import model.UserSearchResult;

public class UsersClient extends APIClient {
    private ISymClient botClient;

    public UsersClient(ISymClient client) {
        botClient = client;
    }

    public UserInfo getUserFromUsername(String username) throws SymClientException, NoContentException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(CommonConstants.HTTPS_PREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig()
                .getPodPort())
            .path(PodConstants.GETUSERV2)
            .queryParam("username", username)
            .queryParam("local", true)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserFromUsername(username);
                }
                return null;
            } else if (response.getStatus() == 204) {
                throw new NoContentException("No user found.");
            } else {
                return response.readEntity(UserInfo.class);
            }
        }
    }

    public UserInfo getUserFromEmail(String email, Boolean local) throws SymClientException, NoContentException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERSV3)
            .queryParam("email", email)
            .queryParam("local", local)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserFromEmail(email, local);
                }
                return null;
            } else if (response.getStatus() == 204) {
                throw new NoContentException("No user found.");
            } else {
                UserInfoList infoList = response.readEntity(UserInfoList.class);
                if (infoList.getUsers().isEmpty()) {
                    throw new NoContentException("No user found.");
                }
                return infoList.getUsers().get(0);
            }
        }
    }

    public UserInfo getUserFromId(Long id, Boolean local) throws SymClientException, NoContentException {
        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERSV3)
            .queryParam("uid", id)
            .queryParam("local", local)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserFromId(id, local);
                }
                return null;
            } else if (response.getStatus() == 204) {
                throw new NoContentException("No user found.");
            } else {
                UserInfoList infoList = response.readEntity(UserInfoList.class);
                return infoList.getUsers().get(0);
            }
        }
    }

    public List<UserInfo> getUsersFromIdList(List<Long> idList, Boolean local)
        throws SymClientException, NoContentException {
        return getUsersV3(null, idList, local);
    }

    public List<UserInfo> getUsersFromEmailList(List<String> emailList, Boolean local)
        throws SymClientException, NoContentException {
        return getUsersV3(emailList, null, local);
    }

    public List<UserInfo> getUsersV3(List<String> emailList, List<Long> idList, Boolean local)
        throws SymClientException, NoContentException {
        List<UserInfo> infoList = new ArrayList<>();
        boolean emailBased = false;
        StringBuilder lookUpListString = new StringBuilder();

        if (emailList != null) {
            if (emailList.isEmpty()) {
                throw new NoContentException("No user sent for lookup");
            }
            emailBased = true;
            lookUpListString.append(emailList.get(0));
            for (int i = 1; i < emailList.size(); i++) {
                lookUpListString.append("," + emailList.get(i));
            }
        } else if (idList != null) {
            if (idList.isEmpty()) {
                throw new NoContentException("No user sent for lookup");
            }
            lookUpListString.append(idList.get(0));
            for (int i = 1; i < idList.size(); i++) {
                lookUpListString.append("," + idList.get(i));
            }
        } else {
            throw new NoContentException("No user sent for lookup");
        }

        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETUSERSV3)
            .queryParam(emailBased ? "email" : "uid", lookUpListString.toString())
            .queryParam("local", local)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUsersV3(emailList, idList, local);
                }
                return null;
            } else if (response.getStatus() == 204) {
                return infoList;
            } else {
                UserInfoList userInfo = response.readEntity(UserInfoList.class);
                infoList = userInfo.getUsers();
            }

            return infoList;
        }
    }

    public UserSearchResult searchUsers(String query, boolean local, int skip, int limit, UserFilter filter)
        throws SymClientException, NoContentException {
        WebTarget webTarget = botClient.getPodClient().target(botClient.getConfig().getPodUrl())
            .path(PodConstants.SEARCHUSERS)
            .queryParam("local", local);

        if (skip > 0) {
            webTarget = webTarget.queryParam("skip", skip);
        }
        if (limit > 0) {
            webTarget = webTarget.queryParam("limit", limit);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        body.put("filters", filter);

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(body, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return searchUsers(query, local, skip, limit, filter);
                }
                return null;
            } else if (response.getStatus() == 204) {
                throw new NoContentException("No user found");
            } else {
                return response.readEntity(UserSearchResult.class);
            }
        }
    }

    public UserInfo getSessionUser() {
        UserInfo info;

        Invocation.Builder builder = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .path(PodConstants.GETSESSIONUSER)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (SymClientException ex) {
                    return getSessionUser();
                }
                return null;
            } else {
                info = response.readEntity(UserInfo.class);
            }
            return info;
        }
    }
}
