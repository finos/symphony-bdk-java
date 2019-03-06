package ${package}.bot.listeners;

import listeners.IMListener;
import listeners.RoomListener;
import model.InboundMessage;
import model.Stream;
import model.events.RoomCreated;
import model.events.RoomDeactivated;
import model.events.RoomMemberDemotedFromOwner;
import model.events.RoomMemberPromotedToOwner;
import model.events.RoomUpdated;
import model.events.UserJoinedRoom;
import model.events.UserLeftRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatListener implements IMListener, RoomListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChatListener.class);


  public void onIMMessage(InboundMessage inboundMessage) {
    LOGGER.info(inboundMessage.getUser().getDisplayName() + " has sent a message");
  }

  public void onIMCreated(Stream stream) {
  }

  @Override
  public void onRoomMessage(InboundMessage inboundMessage) {
    LOGGER.info(inboundMessage.getUser().getDisplayName() + " has sent a message");
  }

  @Override
  public void onRoomCreated(RoomCreated roomCreated) {

  }

  @Override
  public void onRoomDeactivated(RoomDeactivated roomDeactivated) {

  }

  @Override
  public void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {

  }

  @Override
  public void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner) {

  }

  @Override
  public void onRoomReactivated(Stream stream) {

  }

  @Override
  public void onRoomUpdated(RoomUpdated roomUpdated) {

  }

  @Override
  public void onUserJoinedRoom(UserJoinedRoom userJoinedRoom) {

  }

  @Override
  public void onUserLeftRoom(UserLeftRoom userLeftRoom) {

  }
}
