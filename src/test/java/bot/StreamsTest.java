package bot;

import authentication.ISymAuth;
import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.NoContentException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamsTest {
    private SymBotClient botClient;
    private Long userId1;
    private Long userId2;
    private List<Long> userList;
    private static final Logger logger = LoggerFactory.getLogger(StreamsTest.class);
    private String streamId = "Q-ywILH29kMzAudH4i4CWX___pZcqKS7dA";

    @Before
    public void oneTimeSetUp() {
        String configFile = "config.json";
        SymConfig config = SymConfigLoader.load(StreamsTest.class.getResourceAsStream(File.separator + configFile));
        ISymAuth botAuth = new SymBotRSAAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
        try {
            userId1 = botClient.getUsersClient().getUserFromEmail("yong.tan@symphony.com", true).getId();
            userId2 = botClient.getUsersClient().getUserFromEmail("pizza@bots.symphony.com", true).getId();
            userList = new ArrayList<>();
            userList.add(userId1);
            userList.add(userId2);
        } catch (SymClientException | NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getIMStreamTest() {
        try {
            String streamId = botClient.getStreamsClient().getUserIMStreamId(userId1);
            logger.info("IM Stream ID = {}", streamId);
            Assert.assertNotNull(streamId);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMIMStreamTest() {
        try {
            String streamId = botClient.getStreamsClient().getUserListIM(userList);
            logger.info("MIM Stream ID = {}", streamId);
            Assert.assertNotNull(streamId);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createRoomAndAddUsersTest() {
        try {
            String name = "integration test room " + (Math.random() * 500 + 1);
            Room room = new Room();
            room.setName(name);
            room.setDescription("test " + (Math.random() * 500 + 1));
            room.setDiscoverable(true);
            room.setPublic(true);
            room.setViewHistory(true);
            RoomInfo roomInfo;
            roomInfo = botClient.getStreamsClient().createRoom(room);

            String actualRoomName = roomInfo.getRoomAttributes().getName();
            logger.info("Room Created = {}", actualRoomName);
            Assert.assertEquals(name, actualRoomName);

            String actualRoomId = roomInfo.getRoomSystemInfo().getId();
            logger.info("Room ID = {}", actualRoomId);
            Assert.assertNotNull(actualRoomId);

            userList.forEach(u -> botClient.getStreamsClient().addMemberToRoom(actualRoomId, u));

            Assert.assertTrue(botClient.getStreamsClient()
                .getRoomMembers(actualRoomId).stream()
                .map(RoomMember::getId).anyMatch(u -> userList.contains(u))
            );
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void promoteDemoteOwnerTest() {
        try {
            botClient.getStreamsClient().addMemberToRoom(streamId, userId2);
            botClient.getStreamsClient().promoteUserToOwner(streamId, userId2);
            List<RoomMember> members = botClient.getStreamsClient().getRoomMembers(streamId);

            Assert.assertTrue(members.stream().anyMatch(m -> m.getId().equals(userId2) && m.getOwner()));

            botClient.getStreamsClient().demoteUserFromOwner(streamId, userId2);
            Assert.assertTrue(members.stream().anyMatch(m -> m.getId().equals(userId2) && !m.getOwner()));
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deactivateRoom() {
        try {
            botClient.getStreamsClient().deactivateRoom(streamId);
            Assert.assertFalse(botClient.getStreamsClient().getRoomInfo(streamId).getRoomSystemInfo().isActive());
            botClient.getStreamsClient().activateRoom(streamId);
            Assert.assertTrue(botClient.getStreamsClient().getRoomInfo(streamId).getRoomSystemInfo().isActive());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchRoomTest() {
        RoomSearchQuery query = new RoomSearchQuery();
        query.setQuery("test room");
        try {
            RoomSearchResult result = botClient.getStreamsClient().searchRooms(query, 0, 0);
            Assert.assertTrue(!result.getRooms().isEmpty());
        } catch (SymClientException | NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserStreamsTest() {
        List<String> streamTypesRoom = new ArrayList<>();
        streamTypesRoom.add("ROOM");
        try {
            List<StreamListItem> list = botClient.getStreamsClient().getUserStreams(streamTypesRoom, true);
            Assert.assertTrue(!list.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        List<String> streamTypes = new ArrayList<>();
        streamTypes.add("IM");
        try {
            List<StreamListItem> list = botClient.getStreamsClient().getUserStreams(streamTypes, true);
            Assert.assertTrue(!list.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        try {
            List<StreamListItem> list = botClient.getStreamsClient().getUserStreams(null, true);
            Assert.assertTrue(!list.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        try {
            Assert.assertNotNull(botClient.getStreamsClient().getUserWallStream());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getStreamInfoTypeTest() {
        String imStreamId = botClient.getStreamsClient().getUserIMStreamId(userId1);
        Assert.assertEquals(StreamTypes.IM, botClient.getStreamsClient().getStreamInfo(imStreamId).getStreamType().getType());
        logger.info("StreamInfo Type Test for IM Passed");

        String mimStreamId = botClient.getStreamsClient().getUserListIM(userList);
        Assert.assertEquals(StreamTypes.MIM, botClient.getStreamsClient().getStreamInfo(mimStreamId).getStreamType().getType());
        logger.info("StreamInfo Type Test for MIM Passed");

        Room room = new Room();
        room.setName("integration test room " + (Math.random() * 500 + 1));
        room.setDescription("test " + (Math.random() * 500 + 1));
        RoomInfo roomInfo = botClient.getStreamsClient().createRoom(room);
        String roomId = roomInfo.getRoomSystemInfo().getId();
        Assert.assertEquals(StreamTypes.ROOM, botClient.getStreamsClient().getStreamInfo(roomId).getStreamType().getType());
        logger.info("StreamInfo Type Test for Room Passed");

        List<StreamListItem> wallList = botClient.getStreamsClient().getUserStreams(Collections.singletonList("POST"), true);
        Assert.assertEquals(1, wallList.size());
        Assert.assertEquals(StreamTypes.POST, botClient.getStreamsClient().getStreamInfo(wallList.get(0).getId()).getStreamType().getType());
        logger.info("StreamInfo Type Test for Post Passed");
    }
}
