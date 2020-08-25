package clients.symphony.api;

import authentication.SymOBOUserRSAAuth;
import clients.ISymClient;
import clients.symphony.api.constants.AgentConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Signal;
import model.SignalList;
import model.SignalSubscriberList;
import model.SignalSubscriptionResult;

public class SignalsClient extends APIClient {
    private ISymClient botClient;
    private boolean isKeyManTokenRequired;

    public SignalsClient(ISymClient client) {
        botClient = client;
        isKeyManTokenRequired = !(botClient.getSymAuth() instanceof SymOBOUserRSAAuth);
    }

    public List<Signal> listSignals(int skip, int limit) throws SymClientException {
        WebTarget webTarget = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.LISTSIGNALS);

        if (skip > 0) {
            webTarget = webTarget.queryParam("skip", skip);
        }
        if (limit > 0) {
            webTarget = webTarget.queryParam("limit", limit);
        }

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());
        if (isKeyManTokenRequired) {
            builder = builder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
        }

        try (Response response = builder.get()) {
            if (response.getStatus() == 204) {
                return new ArrayList<>();
            } else if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listSignals(skip, limit);
                }
                return null;
            } else {
                return response.readEntity(SignalList.class);
            }
        }
    }

    public Signal getSignal(String id) throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.GETSIGNAL.replace("{id}", id))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        if (isKeyManTokenRequired) {
            builder = builder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
        }

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getSignal(id);
                }
                return null;
            } else {
                return response.readEntity(Signal.class);
            }
        }
    }

    public Signal createSignal(Signal signal) throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.CREATESIGNAL)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken());

        if (isKeyManTokenRequired) {
            builder = builder.header("keyManagerToken", botClient.getSymAuth().getKmToken());
        }

        try (Response response = builder.post(Entity.entity(signal, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createSignal(signal);
                }
                return null;
            } else {
                return response.readEntity(Signal.class);
            }
        }
    }

    public Signal updateSignal(Signal signal) throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.UPDATESIGNAL.replace("{id}", signal.getId()))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.post(Entity.entity(signal, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateSignal(signal);
                }
                return null;
            } else {
                return response.readEntity(Signal.class);
            }
        }
    }

    public void deleteSignal(String id) throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.DELETESIGNAL.replace("{id}", id))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    deleteSignal(id);
                }
            }
        }
    }

    public SignalSubscriptionResult subscribeSignal(String id, boolean self, List<Long> uids, boolean pushed)
        throws SymClientException {
        WebTarget webTarget = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.SUBSCRIBESIGNAL.replace("{id}", id));

        Entity entity = null;

        if (self) {
            webTarget = webTarget.queryParam("pushed", pushed);
            entity = Entity.entity(uids, MediaType.APPLICATION_JSON);
        }

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.post(entity)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    subscribeSignal(id, self, uids, pushed);
                }
            }
            return response.readEntity(SignalSubscriptionResult.class);
        }
    }

    public SignalSubscriptionResult unsubscribeSignal(String id, boolean self, List<Long> uids) throws SymClientException {
        Entity entity = self ? Entity.entity(uids, MediaType.APPLICATION_JSON) : null;

        Invocation.Builder builder = botClient.getAgentClient().target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.UNSUBSCRIBESIGNAL.replace("{id}", id))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.post(entity)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                    return null;
                } catch (UnauthorizedException ex) {
                    return unsubscribeSignal(id, self, uids);
                }
            }
            return response.readEntity(SignalSubscriptionResult.class);
        }
    }

    public SignalSubscriberList getSignalSubscribers(String id, int skip, int limit) throws SymClientException {
        WebTarget webTarget = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.GETSUBSCRIBERS.replace("{id}", id));

        if (skip > 0) {
            webTarget = webTarget.queryParam("skip", skip);
        }
        if (limit > 0) {
            webTarget = webTarget.queryParam("limit", limit);
        }

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getSignalSubscribers(id, skip, limit);
                }
                return null;
            } else {
                return response.readEntity(SignalSubscriberList.class);
            }
        }
    }
}
