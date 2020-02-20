package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.InformationBarrierGroup;
import model.InformationBarrierGroupStatus;
import model.Policy;

public class InformationBarriersClient extends APIClient {
    private ISymClient botClient;

    public InformationBarriersClient(ISymClient client) {
        botClient = client;
    }

    public List<InformationBarrierGroup> listGroups() throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.LISTIBGROUPS, botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listGroups();
                }
                return null;
            } else {
                return response.readEntity(new GenericType<List<InformationBarrierGroup>>() {});
            }
        }
    }

    public List<Long> listGroupMembers(String groupId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.LISTIBGROUPMEMBERS.replace("{gid}", groupId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listGroupMembers(groupId);
                }
                return null;
            } else {
                return response.readEntity(new GenericType<List<Long>>() {});
            }
        }
    }

    public InformationBarrierGroupStatus addGroupMembers(String groupId, List<Long> members) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.ADDIBGROUPMEMBERS.replace("{gid}", groupId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(members, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return addGroupMembers(groupId, members);
                }
                return null;
            } else {
                return response.readEntity(InformationBarrierGroupStatus.class);
            }
        }
    }

    public InformationBarrierGroupStatus removeGroupMembers(String groupId, List<Long> members) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.REMOVEIBGROUPMEMBERS.replace("{gid}", groupId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(members, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return removeGroupMembers(groupId, members);
                }
                return null;
            } else {
                return response.readEntity(InformationBarrierGroupStatus.class);
            }
        }
    }

    public List<Policy> listPolicies() throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.LISTPOLICIES, botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return listPolicies();
                }
                return null;
            } else {
                return response.readEntity(new GenericType<List<Policy>>() {});
            }
        }
    }
}
