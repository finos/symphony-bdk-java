package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import listeners.IMListener;
import listeners.RoomListener;
import model.DatafeedEvent;
import model.InboundMessage;
import model.Stream;
import model.events.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.DatafeedEventsService;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatafeedEventsServiceTest {

    String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    SymConfigLoader configLoader = new SymConfigLoader();
    SymConfig config = configLoader.loadFromFile(configFilePath);
    SymBotAuth botAuth = new SymBotAuth(config);



    @Test
    public void datafeedClientReadTest() {
        botAuth.authenticate();
        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        DatafeedEventsService datafeedEventsService = botClient.getDatafeedEventsService();
        RoomListener roomListenerTest = new RoomListenerTestImpl();
        datafeedEventsService.addRoomListener(roomListenerTest);
    }

    private class RoomListenerTestImpl implements RoomListener {

        @Override
        public void onRoomMessage(InboundMessage message) {
            assertNotNull(message);
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

}
