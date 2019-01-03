package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.Signal;
import model.SignalList;
import model.SignalSubscriberList;
import model.SignalSubscriptionResult;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SignalsClient extends APIClient {

    private ISymClient botClient;

    public SignalsClient(ISymClient client) {
        botClient = client;
    }

    public List<Signal> listSignals(int skip, int limit) throws SymClientException {
        List<Signal> result;
        WebTarget builder
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.LISTSIGNALS);


        if(skip>0){
            builder = builder.queryParam("skip", skip);
        }
        if(limit>0){
            builder = builder.queryParam("limit", limit);
        }

        Response response = null;

        try {
            response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .get();

            if (response.getStatus() == 204) {
                result = new ArrayList<>();
            } else if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listSignals(skip, limit);
                }
                return null;
            } else {
                result = response.readEntity(SignalList.class);
            }
            return result;
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public Signal getSignal(String id) throws SymClientException {

        Response response = null;

        try {
            response = botClient.getAgentClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig()
                    .getAgentPort())
                .path(AgentConstants.GETSIGNAL.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .get();
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
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public Signal createSignal(Signal signal) throws SymClientException {
        Response response = null;

        try {
            response = botClient.getAgentClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig()
                    .getAgentPort())
                .path(AgentConstants.CREATESIGNAL)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .post(Entity.entity(signal, MediaType.APPLICATION_JSON));
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
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public Signal updateSignal(Signal signal) throws SymClientException {
        Response response = null;

        try {
            response = botClient.getAgentClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig()
                    .getAgentPort())
                .path(AgentConstants.UPDATESIGNAL.replace("{id}", signal.getId()))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .post(Entity.entity(signal, MediaType.APPLICATION_JSON));
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
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void deleteSignal (String id) throws SymClientException {
        Response response = null;

        try {
            response = botClient.getAgentClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig()
                    .getAgentPort())
                .path(AgentConstants.DELETESIGNAL.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .post(null);
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    deleteSignal(id);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public SignalSubscriptionResult subscribeSignal (String id, boolean self, List<Long> uids, boolean pushed) throws SymClientException {

        Response response = null;

        try {
            if (self) {
                response
                    = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                    .path(AgentConstants.SUBSCRIBESIGNAL.replace("{id}", id))
                    .request(MediaType.APPLICATION_JSON)
                    .header("sessionToken", botClient.getSymAuth().getSessionToken())
                    .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                    .post(null);

            } else {
                response
                    = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                    .path(AgentConstants.SUBSCRIBESIGNAL.replace("{id}", id))
                    .queryParam("pushed", pushed)
                    .request(MediaType.APPLICATION_JSON)
                    .header("sessionToken", botClient.getSymAuth().getSessionToken())
                    .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                    .post(Entity.entity(uids, MediaType.APPLICATION_JSON));
            }
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    subscribeSignal(id, self, uids, pushed);
                }
            }
            return response.readEntity(SignalSubscriptionResult.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public SignalSubscriptionResult unsubscribeSignal (String id, boolean self, List<Long> uids) throws SymClientException {
        Response response = null;

        try {
            if (self) {
                response
                    = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                    .path(AgentConstants.UNSUBSCRIBESIGNAL.replace("{id}", id))
                    .request(MediaType.APPLICATION_JSON)
                    .header("sessionToken", botClient.getSymAuth().getSessionToken())
                    .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                    .post(null);

            } else {
                response
                    = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                    .path(AgentConstants.UNSUBSCRIBESIGNAL.replace("{id}", id))
                    .request(MediaType.APPLICATION_JSON)
                    .header("sessionToken", botClient.getSymAuth().getSessionToken())
                    .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                    .post(Entity.entity(uids, MediaType.APPLICATION_JSON));

            }
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                    return null;
                } catch (UnauthorizedException ex) {
                    return unsubscribeSignal(id, self, uids);
                }
            }
            return response.readEntity(SignalSubscriptionResult.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public SignalSubscriberList getSignalSubscribers(String id, int skip, int limit) throws SymClientException {
        WebTarget builder
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.GETSUBSCRIBERS.replace("{id}", id));

        if(skip>0){
            builder = builder.queryParam("skip", skip);
        }
        if(limit>0){
            builder = builder.queryParam("limit", limit);
        }

        Response response = null;

        try {
            response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .get();

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
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


}
