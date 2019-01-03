package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.MemberList;
import model.NumericId;
import model.Room;
import model.RoomInfo;
import model.RoomMember;
import model.RoomSearchQuery;
import model.RoomSearchResult;
import model.StreamInfo;
import model.StreamInfoList;
import model.StreamListItem;
import model.StringId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;

public class StreamsClient extends APIClient {
    private ISymClient botClient;
    public StreamsClient(ISymClient client) {
        botClient=client;
    }

    public String getUserIMStreamId(Long userId) throws SymClientException {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        return getUserListIM(userIdList);
    }

    public String getUserListIM(List<Long> userIdList) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETIM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(userIdList, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserListIM(userIdList);
                }
                return null;
            }
            String streamId = response.readEntity(StringId.class).getId();
            return streamId;

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public RoomInfo createRoom(Room room) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.CREATEROOM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(room, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createRoom(room);
                }
                return null;
            }
            RoomInfo roomInfo = response.readEntity(RoomInfo.class);
            return roomInfo;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void addMemberToRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADDMEMBER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    addMemberToRoom(streamId, userId);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void removeMemberFromRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.REMOVEMEMBER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    removeMemberFromRoom(streamId, userId);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public RoomInfo getRoomInfo(String streamId) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETROOMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getRoomInfo(streamId);
                }
                return null;
            }
            return response.readEntity(RoomInfo.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public RoomInfo updateRoom(String streamId, Room room) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())

                .path(PodConstants.UPDATEROOMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(room, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateRoom(streamId, room);
                }
                return null;
            } else {
                RoomInfo roomInfo = response.readEntity(RoomInfo.class);
                return roomInfo;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    //TODO: CHECK WHY 404
    public StreamInfo getStreamInfo(String streamId) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig()
                    .getPodPort())
                .path(PodConstants.GETSTREAMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken())
                .get();
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
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<RoomMember> getRoomMembers(String streamId) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETROOMMEMBERS.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getRoomMembers(streamId);
                }
                return null;
            }
            return response.readEntity(MemberList.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public void activateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId,true);
    }

    public void deactivateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId,false);
    }

    //TODO: CHECK WHY 403
    private void setActiveRoom(String streamId, boolean active) throws SymClientException {
        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())

                .path(PodConstants.SETACTIVE.replace("{id}", streamId))
                .queryParam("active", active)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( null);
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    setActiveRoom(streamId, active);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void promoteUserToOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.PROMOTEOWNER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    promoteUserToOwner(streamId, userId);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void demoteUserFromOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        Response response = null;

        try {
            response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.DEMOTEOWNER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    demoteUserFromOwner(streamId, userId);
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
//TODO: CHECK WHY 500
    public RoomSearchResult searchRooms(RoomSearchQuery query, int skip, int limit)throws SymClientException , NoContentException {
        RoomSearchResult result = null;
        WebTarget builder
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.SEARCHROOMS);


        if(skip>0){
            builder = builder.queryParam("skip", skip);
        }
        if(limit>0){
            builder = builder.queryParam("limit", limit);
        }
        if(query.getLabels()==null){
            query.setLabels(new ArrayList<>());
        }

        Response response = null;

        try {
            response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(query,MediaType.APPLICATION_JSON));

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
                result = response.readEntity(RoomSearchResult.class);
            }

            return result;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<StreamListItem> getUserStreams(List<String> streamTypes, boolean includeInactiveStreams) throws SymClientException {
        List<Map> inputStreamTypes = new ArrayList();
        if(streamTypes!=null) {
            for (String type : streamTypes) {
                Map<String, String> streamTypesMap = new HashMap<>();
                streamTypesMap.put("type", type);
                inputStreamTypes.add(streamTypesMap);
            }
        }
        Map<String, Object> input = new HashMap<>();
        input.put("streamTypes", inputStreamTypes);
        input.put("includeInactiveStreams", includeInactiveStreams);


        Response response = null;

        try {
            response = botClient.getPodClient()
                .target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig()
                    .getPodPort())
                .path(PodConstants.LISTUSERSTREAMS)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken())
                .post(Entity.entity(input, MediaType.APPLICATION_JSON));

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserStreams(streamTypes, includeInactiveStreams);
                }
                return null;
            }

            return response.readEntity(StreamInfoList.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public StreamListItem getUserWallStream() throws SymClientException {
        List<String> streamTypes = new ArrayList<>();
        streamTypes.add("POST");
        return getUserStreams(streamTypes, false).get(0);
    }



}
