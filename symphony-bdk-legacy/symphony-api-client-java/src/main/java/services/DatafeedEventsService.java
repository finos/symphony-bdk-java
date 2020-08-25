package services;

import clients.SymBotClient;
import clients.symphony.api.constants.DatafeedVersion;
import configuration.SymConfig;
import listeners.*;

public class DatafeedEventsService implements IDatafeedEventsService {

    private final AbstractDatafeedEventsService datafeedEventsService;

    public DatafeedEventsService(SymBotClient client) {
        SymConfig config = client.getConfig();
        String datafeedVersion = config.getDatafeedVersion();
        if (DatafeedVersion.V2 == DatafeedVersion.of(datafeedVersion)) {
            this.datafeedEventsService = new DatafeedEventsServiceV2(client);
        } else {
            this.datafeedEventsService = new DatafeedEventsServiceV1(client);
        }
    }

    @Override
    public void readDatafeed() {
        this.datafeedEventsService.readDatafeed();
    }

    @Override
    public void stopDatafeedService() {
        this.datafeedEventsService.stopDatafeedService();
    }

    @Override
    public void restartDatafeedService() {
        this.datafeedEventsService.restartDatafeedService();
    }

    @Override
    public void addListeners(DatafeedListener... listeners) {
        this.datafeedEventsService.addListeners(listeners);
    }

    @Override
    public void removeListeners(DatafeedListener... listeners) {
        this.datafeedEventsService.removeListeners(listeners);
    }

    @Override
    public void addRoomListener(RoomListener listener) {
        this.datafeedEventsService.addRoomListener(listener);
    }

    @Override
    public void removeRoomListener(RoomListener listener) {
        this.datafeedEventsService.removeRoomListener(listener);
    }

    @Override
    public void addIMListener(IMListener listener) {
        this.datafeedEventsService.addIMListener(listener);
    }

    @Override
    public void removeIMListener(IMListener listener) {
        this.datafeedEventsService.removeIMListener(listener);
    }

    @Override
    public void addConnectionsListener(ConnectionListener listener) {
        this.datafeedEventsService.addConnectionsListener(listener);
    }

    @Override
    public void removeConnectionsListener(ConnectionListener listener) {
        this.datafeedEventsService.removeConnectionsListener(listener);
    }

    @Override
    public void addElementsListener(ElementsListener listener) {
        this.datafeedEventsService.addElementsListener(listener);
    }

    @Override
    public void removeElementsListener(ElementsListener listener) {
        this.datafeedEventsService.removeElementsListener(listener);
    }

}
