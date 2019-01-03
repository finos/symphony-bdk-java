package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.UserPresence;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PresenceClient extends APIClient{

    ISymClient botClient;
    public PresenceClient(ISymClient client) {
        botClient = client;
    }

    public UserPresence getUserPresence(Long userId, boolean local) throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig()
                    .getPodPort())
                .path(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(userId)))
                .queryParam("local", local)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserPresence(userId, local);
                }
                return null;
            }
            return response.readEntity(UserPresence.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public UserPresence setPresence(String status) throws SymClientException {
        Category category = new Category();
        category.setCategory(status);
        Response response = null;

        try {
            response = botClient.getPodClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig()
                    .getPodPort())
                .path(PodConstants.SETPRESENCE)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(category, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return setPresence(status);
                }
                return null;
            }
            return response.readEntity(UserPresence.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void registerInterestExtUser(List<Long> userIds) throws SymClientException {
        Response response = null;

        try {
            response = botClient.getPodClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig()
                    .getPodPort())
                .path(PodConstants.REGISTERPRESENCEINTEREST)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(userIds, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    registerInterestExtUser(userIds);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private class Category{
        private String category;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
