package clients.symphony.api;

import clients.ISymClient;
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
import model.*;

public class StreamsClient extends APIClient {
    private ISymClient botClient;

    public StreamsClient(ISymClient client) {
        botClient = client;
    }

    public String getUserIMStreamId(Long userId) throws SymClientException {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        return getUserListIM(userIdList);
    }

    public String getUserListIM(List<Long> userIdList) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.GETIM, botClient.getSymAuth().getSessionToken());
        
        try (Response response = builder.post(Entity.entity(userIdList, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserListIM(userIdList);
                }
                return null;
            }
            return response.readEntity(StringId.class).getId();
        }
    }

    public RoomInfo createRoom(Room room) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.CREATEROOM, botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(room, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createRoom(room);
                }
                return null;
            }
            return response.readEntity(RoomInfo.class);
        }
    }

    public void addMemberToRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.ADDMEMBER.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    addMemberToRoom(streamId, userId);
                }
            }
        }
    }

    public void removeMemberFromRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.REMOVEMEMBER.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    removeMemberFromRoom(streamId, userId);
                }
            }
        }
    }

    public RoomInfo getRoomInfo(String streamId) throws SymClientException {

        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.GETROOMINFO.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getRoomInfo(streamId);
                }
                return null;
            }
            return response.readEntity(RoomInfo.class);
        }
    }

    public RoomInfo updateRoom(String streamId, Room room) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.UPDATEROOMINFO.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(room, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateRoom(streamId, room);
                }
                return null;
            } else {
                return response.readEntity(RoomInfo.class);
            }
        }
    }

    public StreamInfo getStreamInfo(String streamId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.GETSTREAMINFO.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getStreamInfo(streamId);
                }
                return null;
            } else {
                return response.readEntity(StreamInfo.class);
            }
        }
    }

    public List<RoomMember> getRoomMembers(String streamId) throws SymClientException {
        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.GETROOMMEMBERS.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getRoomMembers(streamId);
                }
                return null;
            }
            return response.readEntity(MemberList.class);
        }

    }

    public void activateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId, true);
    }

    public void deactivateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId, false);
    }

    private void setActiveRoom(String streamId, boolean active) throws SymClientException {
        WebTarget webTarget = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .queryParam("active", active);

        Invocation.Builder builder = createInvocationBuilderFromWebTarget(webTarget,
            PodConstants.SETACTIVE.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    setActiveRoom(streamId, active);
                }
            }
        }
    }

    public void promoteUserToOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.PROMOTEOWNER.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    promoteUserToOwner(streamId, userId);
                }
            }
        }
    }

    public void demoteUserFromOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Invocation.Builder builder = createInvocationBuilder(botClient.getPodClient(), botClient.getConfig().getPodUrl(),
            PodConstants.DEMOTEOWNER.replace("{id}", streamId), botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    demoteUserFromOwner(streamId, userId);
                }
            }
        }
    }

    public RoomSearchResult searchRooms(RoomSearchQuery query, int skip, int limit)
        throws SymClientException, NoContentException {
        WebTarget webTarget = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl());

        if (skip > 0) {
            webTarget = webTarget.queryParam("skip", skip);
        }
        if (limit > 0) {
            webTarget = webTarget.queryParam("limit", limit);
        }
        if (query.getLabels() == null) {
            query.setLabels(new ArrayList<>());
        }

        Invocation.Builder builder = createInvocationBuilderFromWebTarget(webTarget,
            PodConstants.SEARCHROOMS, botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(query, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return searchRooms(query, skip, limit);
                }
                return null;
            } else if (response.getStatus() == 204) {
                throw new NoContentException("No messages found");
            } else {
                return response.readEntity(RoomSearchResult.class);
            }
        }
    }

    /**
     * Returns a list of all the streams of which the requesting user is a member,
     * sorted by creation date (ascending - oldest to newest).
     *
     * <p>
     *   skip and limit parameters are set to default : skip=0 and limit=50.
     * </p>
     *
     * @param streamTypes A list of stream types that will be returned.
     * @param includeInactiveStreams Whether to include inactive conversations.
     * @return a list of all the streams
     * @throws SymClientException the generic client exception
     * @throws IllegalArgumentException on illegal skip or limit parameter
     */
    public List<StreamListItem> getUserStreams(List<String> streamTypes, boolean includeInactiveStreams)
        throws SymClientException {
        return this.getUserStreams(streamTypes, includeInactiveStreams, 0, 50);
    }

    /**
     * Returns a list of all the streams of which the requesting user is a member,
     * sorted by creation date (ascending - oldest to newest).
     *
     * @param streamTypes A list of stream types that will be returned.
     * @param includeInactiveStreams Whether to include inactive conversations.
     * @param skip Number of stream results to skip.
     * @param limit Maximum number of streams to return. If 0, all user streams will be returned.
     * @return a list of all the streams
     * @throws SymClientException the generic client exception
     * @throws IllegalArgumentException on illegal skip or limit parameter
     */
    public List<StreamListItem> getUserStreams(List<String> streamTypes, boolean includeInactiveStreams, int skip, int limit)
        throws SymClientException {

        if (skip < 0) {
            throw new IllegalArgumentException("skip must be equal or greater than 0.");
        }

        if (limit < 0) {
            throw new IllegalArgumentException("limit must be equal or greater than 0.");
        }

        List<Map> inputStreamTypes = new ArrayList<>();
        if (streamTypes != null) {
            for (String type : streamTypes) {
                Map<String, String> streamTypesMap = new HashMap<>();
                streamTypesMap.put("type", type);
                inputStreamTypes.add(streamTypesMap);
            }
        }
        Map<String, Object> input = new HashMap<>();
        input.put("streamTypes", inputStreamTypes);
        input.put("includeInactiveStreams", includeInactiveStreams);

        WebTarget webTarget = botClient.getPodClient()
            .target(botClient.getConfig().getPodUrl())
            .queryParam("skip", skip)
            .queryParam("limit", limit);
        
        Invocation.Builder builder = createInvocationBuilderFromWebTarget(webTarget, 
            PodConstants.LISTUSERSTREAMS, botClient.getSymAuth().getSessionToken());

        try (Response response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserStreams(streamTypes, includeInactiveStreams);
                }
                return null;
            }
            return response.readEntity(StreamInfoList.class);
        }
    }

    public StreamListItem getUserWallStream() throws SymClientException {
        List<String> streamTypes = new ArrayList<>();
        streamTypes.add("POST");
        return getUserStreams(streamTypes, false).get(0);
    }
}
