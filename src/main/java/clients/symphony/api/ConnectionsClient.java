package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.InboundConnectionRequest;
import model.InboundConnectionRequestList;

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
        String userIdString = null;
        if (userIds != null && !userIds.isEmpty()) {
            userIdString = userIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        }

        WebTarget webTarget = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl());
        
        if (status != null) {
            webTarget = webTarget.queryParam("status", status);
        }
        if (userIdString != null) {
            webTarget = webTarget.queryParam("userIds", userIdString);
        }
        Invocation.Builder builder = createInvocationBuilderFromWebTarget(webTarget, PodConstants.GETCONNECTIONS,
        botClient.getSymAuth().getSessionToken());


        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getConnections(status, userIds);
                }
                return null;
            } else {
                return response.readEntity(InboundConnectionRequestList.class);
            }
        }
    }

    public InboundConnectionRequest acceptConnectionRequest(Long userId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.ACCEPTCONNECTION, botClient.getSymAuth().getSessionToken());

        Entity entity = Entity.entity(new UserId(userId), MediaType.APPLICATION_JSON);

        try (Response response = builder.post(entity)) {
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
        }
    }

    public InboundConnectionRequest rejectConnectionRequest(Long userId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.REJECTCONNECTION, botClient.getSymAuth().getSessionToken());

        Entity entity = Entity.entity(new UserId(userId), MediaType.APPLICATION_JSON);

        try (Response response = builder.post(entity)) {
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
        }
    }

    public InboundConnectionRequest sendConnectionRequest(Long userId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.SENDCONNECTIONREQUEST, botClient.getSymAuth().getSessionToken());

        Entity entity = Entity.entity(new UserId(userId), MediaType.APPLICATION_JSON);

        try (Response response = builder.post(entity)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return sendConnectionRequest(userId);
                }
                return null;
            } else {
                return response.readEntity(InboundConnectionRequest.class);
            }
        }
    }

    public InboundConnectionRequest getConnectionRequestStatus(Long userId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.GETCONNECTIONSTATUS.replace("{userId}", Long.toString(userId)), 
            botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getConnectionRequestStatus(userId);
                }
                return null;
            } else {
                return response.readEntity(InboundConnectionRequest.class);
            }
        }
    }

    public void removeConnection(Long userId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.REMOVECONNECTION.replace("{userId}", Long.toString(userId)), 
            botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    removeConnection(userId);
                }
            }
        }
    }

    private class UserId {
        private Long userId;

        public UserId() {}

        public UserId(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}
