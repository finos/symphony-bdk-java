package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.InboundConnectionRequest;
import model.InboundConnectionRequestList;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public final class ConnectionsClient extends APIClient {
  private ISymClient botClient;

  public ConnectionsClient(ISymClient client) {
    botClient = client;
  }

  public List<InboundConnectionRequest> getPendingConnections()
      throws SymClientException {
    return getConnections(null, null);
  }

  public List<InboundConnectionRequest> getInboundPendingConnections()
      throws SymClientException {
    return getConnections("PENDING_INCOMING", null);
  }

  public List<InboundConnectionRequest> getAllConnections()
      throws SymClientException {
    return getConnections("ALL", null);
  }

  public List<InboundConnectionRequest> getAcceptedConnections()
      throws SymClientException {
    return getConnections("ACCEPTED", null);
  }

  public List<InboundConnectionRequest> getRejectedConnections()
      throws SymClientException {
    return getConnections("REJECTED", null);
  }

  public List<InboundConnectionRequest> getConnections(String status,
      List<Long> userIds)
      throws SymClientException {
    boolean userList = false;
    StringBuilder userIdList = new StringBuilder();
    if (userIds != null) {
      if (!userIds.isEmpty()) {
        userList = true;
        userIdList.append(userIds.get(0));
        for (int i = 1; i < userIds.size(); i++) {
          userIdList.append("," + userIds.get(i));
        }
      }
    }
    WebTarget builder
        = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
        + botClient.getConfig().getPodHost()
        + ":" + botClient.getConfig().getPodPort())
        .path(PodConstants.GETCONNECTIONS);

    if (status != null) {
      builder = builder.queryParam("status", status);
    }
    if (userList) {
      builder = builder.queryParam("userIds", userIdList.toString());
    }

    Response response = null;

    try {
      response = builder.request(MediaType.APPLICATION_JSON)
          .header("sessionToken",
              botClient.getSymAuth().getSessionToken())
          .get();
      if (response.getStatusInfo().getFamily()
          != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, botClient);
        } catch (UnauthorizedException ex) {
          return getConnections(status, userIds);
        }
        return null;
      } else {
        return response.readEntity(InboundConnectionRequestList.class);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public InboundConnectionRequest acceptConnectionRequest(Long userId)
      throws SymClientException {
    UserId userIdObject = new UserId();
    userIdObject.setUserId(userId);
    Response response = null;

    try {
        response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
          + botClient.getConfig().getPodHost()
          + ":" + botClient.getConfig().getPodPort())
          .path(PodConstants.ACCEPTCONNECTION)
          .request(MediaType.APPLICATION_JSON)
          .header("sessionToken",
              botClient.getSymAuth().getSessionToken())
          .post(Entity.entity(userIdObject, MediaType.APPLICATION_JSON));
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, botClient);
        } catch (UnauthorizedException ex) {
          return acceptConnectionRequest(userId);
        }
        return null;
      } else {
        return response.readEntity(InboundConnectionRequest.class);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public InboundConnectionRequest rejectConnectionRequest(Long userId)
      throws SymClientException {
    UserId userIdObject = new UserId();
    userIdObject.setUserId(userId);
    Response response = null;

    try {
        response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
          + botClient.getConfig().getPodHost()
          + ":" + botClient.getConfig().getPodPort())
          .path(PodConstants.REJECTCONNECTION)
          .request(MediaType.APPLICATION_JSON)
          .header("sessionToken",
              botClient.getSymAuth().getSessionToken())
          .post(Entity.entity(userIdObject, MediaType.APPLICATION_JSON));
      if (response.getStatusInfo().getFamily()
          != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, botClient);
        } catch (UnauthorizedException ex) {
          return rejectConnectionRequest(userId);
        }
        return null;
      } else {
        return response.readEntity(InboundConnectionRequest.class);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public InboundConnectionRequest sendConnectionRequest(Long userId)
      throws SymClientException {
    UserId userIdObject = new UserId();
    userIdObject.setUserId(userId);
    Response response = null;

    try {
        response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
          + botClient.getConfig().getPodHost()
          + ":" + botClient.getConfig().getPodPort())
          .path(PodConstants.SENDCONNECTIONREQUEST)
          .request(MediaType.APPLICATION_JSON)
          .header("sessionToken",
              botClient.getSymAuth().getSessionToken())
          .post(Entity.entity(userIdObject, MediaType.APPLICATION_JSON));
      if (response.getStatusInfo().getFamily()
          != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, botClient);
        } catch (UnauthorizedException ex) {
          return sendConnectionRequest(userId);
        }
        return null;
      } else {
        return response.readEntity(InboundConnectionRequest.class);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public InboundConnectionRequest getConnectionRequestStatus(Long userId)
      throws SymClientException {
    Response response = null;

    try {
        response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
          + botClient.getConfig().getPodHost()
          + ":" + botClient.getConfig().getPodPort())
          .path(PodConstants.GETCONNECTIONSTATUS.replace("{userId}",
              Long.toString(userId)))
          .request(MediaType.APPLICATION_JSON)
          .header("sessionToken",
              botClient.getSymAuth().getSessionToken())
          .get();
      if (response.getStatusInfo().getFamily()
          != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, botClient);
        } catch (UnauthorizedException ex) {
          return getConnectionRequestStatus(userId);
        }
        return null;
      } else {
        return response.readEntity(InboundConnectionRequest.class);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public void removeConnection(Long userId) throws SymClientException {
    Response response = null;

    try {
        response = botClient.getPodClient().target(CommonConstants.HTTPS_PREFIX
          + botClient.getConfig().getPodHost()
          + ":" + botClient.getConfig().getPodPort())
          .path(PodConstants.REMOVECONNECTION.replace("{userId}",
              Long.toString(userId)))
          .request(MediaType.APPLICATION_JSON)
          .header("sessionToken",
              botClient.getSymAuth().getSessionToken())
          .post(null);
      if (response.getStatusInfo().getFamily()
          != Response.Status.Family.SUCCESSFUL) {
        try {
          handleError(response, botClient);
        } catch (UnauthorizedException ex) {
          removeConnection(userId);
        }
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  private class UserId {
    private Long userId;

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }
  }

}
