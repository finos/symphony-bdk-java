package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import listeners.FirehoseListener;
import model.DatafeedEvent;
import model.events.MessageSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActionFirehose implements IActionFirehose {
  private final Logger logger = LoggerFactory.getLogger(ActionFirehose.class);
  private SymBotClient botClient;

  public ActionFirehose(SymBotClient botClient) {
    this.botClient = botClient;
  }

  @Override
  public List<DatafeedEvent> actionReadFirehose(FirehoseClient firehoseClient, String firehoseId) {
    try {
      return firehoseClient.readFirehose(firehoseId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CompletableFuture<List<DatafeedEvent>> actionHandleEvents(List<DatafeedEvent> events, List<FirehoseListener> listeners) {
    if (events != null || !events.isEmpty()) {
      handleEvents(events, listeners);
    }
    return null;
  }

  private void handleEvents(List<DatafeedEvent> firehoseEvents, List<FirehoseListener> listeners) {
    for (DatafeedEvent event : firehoseEvents) {
      if (!event.getInitiator().getUser().getUserId().equals(botClient.getBotUserId())) {
        switch (event.getType()) {
          case "MESSAGESENT":

            MessageSent messageSent = event.getPayload().getMessageSent();

            if (messageSent.getMessage().getStream().getStreamType().equals("ROOM")) {
              for (FirehoseListener listener : listeners) {
                listener.onRoomMessage(messageSent.getMessage());
              }
            } else {
              for (FirehoseListener listener : listeners) {
                listener.onIMMessage(messageSent.getMessage());
              }
            }
            break;
          case "INSTANTMESSAGECREATED":

            for (FirehoseListener listener : listeners) {
              listener.onIMCreated(event.getPayload().getInstantMessageCreated().getStream());
            }
            break;

          case "ROOMCREATED":

            for (FirehoseListener listener : listeners) {
              listener.onRoomCreated(event.getPayload().getRoomCreated());
            }
            break;

          case "ROOMUPDATED":

            for (FirehoseListener listener : listeners) {
              listener.onRoomUpdated(event.getPayload().getRoomUpdated());
            }
            break;

          case "ROOMDEACTIVATED":

            for (FirehoseListener listener : listeners) {
              listener.onRoomDeactivated(event.getPayload().getRoomDeactivated());
            }
            break;

          case "ROOMREACTIVATED":

            for (FirehoseListener listener : listeners) {
              listener.onRoomReactivated(event.getPayload().getRoomReactivated().getStream());
            }
            break;

          case "USERJOINEDROOM":

            for (FirehoseListener listener : listeners) {
              listener.onUserJoinedRoom(event.getPayload().getUserJoinedRoom());
            }
            break;

          case "USERLEFTROOM":

            for (FirehoseListener listener : listeners) {
              listener.onUserLeftRoom(event.getPayload().getUserLeftRoom());
            }
            break;

          case "ROOMMEMBERPROMOTEDTOOWNER":

            for (FirehoseListener listener : listeners) {
              listener.onRoomMemberPromotedToOwner(event.getPayload().getRoomMemberPromotedToOwner());
            }
            break;

          case "ROOMMEMBERDEMOTEDFROMOWNER":

            for (FirehoseListener listener : listeners) {
              listener.onRoomMemberDemotedFromOwner(event.getPayload().getRoomMemberDemotedFromOwner());
            }
            break;

          case "CONNECTIONACCEPTED":

            for (FirehoseListener listener : listeners) {
              listener.onConnectionAccepted(event.getPayload().getConnectionAccepted().getFromUser());
            }
            break;

          case "CONNECTIONREQUESTED":

            for (FirehoseListener listener : listeners) {
              listener.onConnectionRequested(event.getPayload().getConnectionRequested().getToUser());
            }
            break;

          default:
            break;
        }
      }
    }
  }
}
