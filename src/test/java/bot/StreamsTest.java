package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.Room;
import model.RoomInfo;
import model.RoomMember;
import model.UserInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class StreamsTest {

    private String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    private SymConfig config;
    private SymBotAuth botAuth;
    private SymBotClient botClient;
    private Stream stream;
    private Long userId1;
    private Long userId2;
    private List<Long> userList;
    private String streamId= "yDbrhJBuBDNZaJv6Pm_NGH___px_gz5fdA";

    @Before
    public void oneTimeSetUp() {
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.loadFromFile(configFilePath);
        botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
        try {
           userId1 = botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId();
           userId2 = botClient.getUsersClient().getUserFromEmail("mike.scannell@symphony.com",true).getId();
           userList = new ArrayList<>();
           userList.add(userId1);
           userList.add(userId2);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getIMStreamTest(){
        try {
            String streamId = botClient.getStreamsClient().getUserIMStreamId(userId1);
            Assert.assertNotNull(streamId);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMIMStreamTest(){
        try {
            String streamId = botClient.getStreamsClient().getUserListIM(userList);
            Assert.assertNotNull(streamId);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createRoomTest(){
        try {
            String name = "integration test room "+(Math.random() * 500 + 1);
            Room room = new Room();
            room.setName(name);
            room.setDescription("test");
            room.setDiscoverable(true);
            room.setPublic(true);
            room.setViewHistory(true);
            RoomInfo roomInfo = null;
            roomInfo = botClient.getStreamsClient().createRoom(room);
            Assert.assertEquals(roomInfo.getRoomAttributes().getName(),name);
            Assert.assertNotNull(roomInfo.getRoomSystemInfo().getId());
            //botClient.getStreamsClient().addMemberToRoom(roomInfo.getRoomSystemInfo().getId(),userInfo.getId());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUserToRoomTest(){
        try {

            botClient.getStreamsClient().addMemberToRoom(streamId,userId1);
            List<RoomMember> members =  botClient.getStreamsClient().getRoomMembers(streamId);
            for (RoomMember member: members) {
                if(member.getId().equals(userId1)){
                    Assert.assertEquals(member.getId(),userId1);
                }
            }
            botClient.getStreamsClient().removeMemberFromRoom(streamId,userId1);

        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void promoteToOwnerTest(){
        try {

            botClient.getStreamsClient().addMemberToRoom(streamId,userId2);
            botClient.getStreamsClient().promoteUserToOwner(streamId,userId2);
            List<RoomMember> members =  botClient.getStreamsClient().getRoomMembers(streamId);
            for (RoomMember member: members) {
                if(member.getId().equals(userId2) && member.getOwner()){
                    Assert.assertEquals(member.getId(),userId2);
                }
            }
            botClient.getStreamsClient().demoteUserFromOwner(streamId,userId2);

        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deactivateRoom(){
        try {
            botClient.getStreamsClient().deactivateRoom(streamId);
            Assert.assertEquals(botClient.getStreamsClient().getRoomInfo(streamId).getRoomSystemInfo().isActive(), false);
            botClient.getStreamsClient().activateRoom(streamId);
            Assert.assertEquals(botClient.getStreamsClient().getRoomInfo(streamId).getRoomSystemInfo().isActive(), true);

        } catch (SymClientException e) {
            e.printStackTrace();
        }
    }




}
