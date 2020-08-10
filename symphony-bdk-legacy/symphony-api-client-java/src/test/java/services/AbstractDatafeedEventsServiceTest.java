package services;

import clients.SymBotClient;
import configuration.SymConfig;
import listeners.ConnectionListener;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;
import model.InboundMessage;
import model.Stream;
import model.User;
import model.events.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractDatafeedEventsServiceTest {
    private SymBotClient symBotClient;

    @Before
    public void initClient() {
        symBotClient = mock(SymBotClient.class);
        SymConfig config = mock (SymConfig.class);
        when(config.getDatafeedVersion()).thenReturn("v1");
        when(symBotClient.getConfig()).thenReturn(config);
    }

    @Test
    public void addRoomListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        int initialSize = service.getRoomListeners().size();
        int newSize;
        // Add first item
        service.addListeners(this.createRoomListener());
        newSize = service.getRoomListeners().size();
        assertEquals(initialSize + 1, newSize);
        // Add second item
        service.addListeners(this.createRoomListener());
        newSize = service.getRoomListeners().size();
        assertEquals(initialSize + 2, newSize);
    }

    @Test
    public void addConnectionListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        int initialSize = service.getConnectionListeners().size();
        int newSize;
        // Add first item
        service.addListeners(this.createConnectionListener());
        newSize = service.getConnectionListeners().size();
        assertEquals(initialSize + 1, newSize);
        // Add second item
        service.addListeners(this.createConnectionListener());
        newSize = service.getConnectionListeners().size();
        assertEquals(initialSize + 2, newSize);
    }

    @Test
    public void addElementsListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        int initialSize = service.getElementsListener().size();
        int newSize;
        // Add first item
        service.addListeners(this.createElementsListener());
        newSize = service.getElementsListener().size();
        assertEquals(initialSize + 1, newSize);
        // Add second item
        service.addListeners(this.createElementsListener());
        newSize = service.getElementsListener().size();
        assertEquals(initialSize + 2, newSize);
    }

    @Test
    public void addIMListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        int initialSize = service.getIMListener().size();
        int newSize;
        // Add first item
        service.addListeners(this.createIMListener());
        newSize = service.getIMListener().size();
        assertEquals(initialSize + 1, newSize);
        // Add second item
        service.addListeners(this.createIMListener());
        newSize = service.getIMListener().size();
        assertEquals(initialSize + 2, newSize);
    }

    @Test
    public void removeRoomListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        RoomListener roomListener1 = this.createRoomListener();
        RoomListener roomListener2 = this.createRoomListener();
        RoomListener roomListener3 = this.createRoomListener();
        service.addListeners(roomListener1, roomListener2, roomListener3);
        int initialSize = service.getRoomListeners().size();
        int newSize;

        service.removeListeners(roomListener1);
        newSize = service.getRoomListeners().size();
        assertEquals(initialSize - 1, newSize);

        service.removeListeners(roomListener3);
        newSize = service.getRoomListeners().size();
        assertEquals(initialSize - 2, newSize);
    }

    @Test
    public void removeConnectionListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        ConnectionListener connectionListener1 = this.createConnectionListener();
        ConnectionListener connectionListener2 = this.createConnectionListener();
        ConnectionListener connectionListener3 = this.createConnectionListener();
        service.addListeners(connectionListener1, connectionListener2, connectionListener3);
        int initialSize = service.getConnectionListeners().size();
        int newSize;

        service.removeListeners(connectionListener1);
        newSize = service.getConnectionListeners().size();
        assertEquals(initialSize - 1, newSize);

        service.removeListeners(connectionListener3);
        newSize = service.getConnectionListeners().size();
        assertEquals(initialSize - 2, newSize);
    }

    @Test
    public void removeElementsListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        ElementsListener elementsListener1 = this.createElementsListener();
        ElementsListener elementsListener2 = this.createElementsListener();
        ElementsListener elementsListener3 = this.createElementsListener();
        service.addListeners(elementsListener1, elementsListener2, elementsListener3);
        int initialSize = service.getElementsListener().size();
        int newSize;

        service.removeListeners(elementsListener1);
        newSize = service.getElementsListener().size();
        assertEquals(initialSize - 1, newSize);

        service.removeListeners(elementsListener3);
        newSize = service.getElementsListener().size();
        assertEquals(initialSize - 2, newSize);
    }

    @Test
    public void removeIMListenersTest() {
        MyAbstractDatafeedEventsService service = new MyAbstractDatafeedEventsService(symBotClient);
        IMListener imListener1 = this.createIMListener();
        IMListener imListener2 = this.createIMListener();
        IMListener imListener3 = this.createIMListener();
        service.addListeners(imListener1, imListener2, imListener3);
        int initialSize = service.getIMListener().size();
        int newSize;

        service.removeListeners(imListener1);
        newSize = service.getIMListener().size();
        assertEquals(initialSize - 1, newSize);

        service.removeListeners(imListener3);
        newSize = service.getIMListener().size();
        assertEquals(initialSize - 2, newSize);
    }

    // Anonymous class creation
    private RoomListener createRoomListener() {
        return mock(RoomListener.class);
    }

    private ConnectionListener createConnectionListener() {
        return mock(ConnectionListener.class);
    }

    private ElementsListener createElementsListener() {
        return mock(ElementsListener.class);
    }

    private IMListener createIMListener() {
        return mock(IMListener.class);
    }
}

/**
 * This class is only used to test 'addListeners' and 'removeListeners' from AbstractDatafeedEventsService
 */
class MyAbstractDatafeedEventsService extends AbstractDatafeedEventsService {

    public MyAbstractDatafeedEventsService(SymBotClient client) {
        super(client);
    }

    public List<RoomListener> getRoomListeners() { return this.roomListeners; }
    public List<ConnectionListener> getConnectionListeners() { return this.connectionListeners; }
    public List<ElementsListener> getElementsListener() { return this.elementsListeners; }
    public List<IMListener> getIMListener() { return this.imListeners; }

    @Override
    public void readDatafeed() {}

    @Override
    public void stopDatafeedService() {}

    @Override
    public void restartDatafeedService() {}
}