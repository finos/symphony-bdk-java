package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.StreamsClient;
import configuration.SymConfig;
import java.util.ArrayList;
import java.util.List;
import model.Room;
import model.RoomInfo;
import model.RoomMember;
import model.RoomSearchQuery;
import model.RoomSearchResult;
import model.StreamInfo;
import model.StreamListItem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StreamsClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void StreamsClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new StreamsClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void activateRoomTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.activateRoom(streamId);
  }

  @Test
  public void addMemberToRoomTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    Long userId = new Long(655361L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.addMemberToRoom(streamId, userId);
  }

  @Test
  public void createRoomTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    Room room = new Room();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.createRoom(room);
  }

  @Test
  public void deactivateRoomTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.deactivateRoom(streamId);
  }

  @Test
  public void demoteUserFromOwnerTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    Long userId = new Long(655361L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.demoteUserFromOwner(streamId, userId);
  }

  @Test
  public void getRoomInfoTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getRoomInfo(streamId);
  }

  @Test
  public void getRoomMembersTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getRoomMembers(streamId);
  }

  @Test
  public void getStreamInfoTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getStreamInfo(streamId);
  }

  @Test
  public void getUserIMStreamIdTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getUserIMStreamId(userId);
  }

  @Test
  public void getUserListIMTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getUserListIM(arrayList);
  }

  @Test
  public void getUserStreamsTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");
    boolean includeInactiveStreams = true;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getUserStreams(arrayList, includeInactiveStreams);
  }

  @Test
  public void getUserWallStreamTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.getUserWallStream();
  }

  @Test
  public void promoteUserToOwnerTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    Long userId = new Long(655361L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.promoteUserToOwner(streamId, userId);
  }

  @Test
  public void removeMemberFromRoomTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    Long userId = new Long(655361L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.removeMemberFromRoom(streamId, userId);
  }

  @Test
  public void searchRoomsTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    RoomSearchQuery query = new RoomSearchQuery();
    int skip = 1;
    int limit = 1;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.searchRooms(query, skip, limit);
  }

  @Test
  public void updateRoomTest() throws Exception {
    // Arrange
    StreamsClient streamsClient = new StreamsClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    Room room = new Room();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamsClient.updateRoom(streamId, room);
  }
}
