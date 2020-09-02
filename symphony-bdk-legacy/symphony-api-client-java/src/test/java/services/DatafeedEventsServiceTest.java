package services;

import static org.mockito.Mockito.*;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import exceptions.APIClientErrorException;
import model.datafeed.DatafeedV2;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class DatafeedEventsServiceTest {

    private SymBotClient symBotClient;
    private DatafeedEventsService datafeedEventsService;
    private DatafeedEventsServiceV2 datafeedEventsServiceV2;
    private DatafeedClient datafeedClient;

    @Before
    public void initClient() {
        symBotClient = mock(SymBotClient.class);
        SymConfig config = mock (SymConfig.class);
        when(config.getDatafeedVersion()).thenReturn("v2");
        when(symBotClient.getConfig()).thenReturn(config);

        datafeedClient = mock(DatafeedClient.class);
        when(symBotClient.getDatafeedClient()).thenReturn(datafeedClient);
        when(datafeedClient.getAckId()).thenReturn("ack_id_string");

    }

    @Test
    public void readDatafeedTest() {
        DatafeedV2 id = new DatafeedV2();
        id.setId("123");
        when(datafeedClient.listDatafeedId()).thenReturn(Collections.singletonList(id));
        datafeedEventsService = new DatafeedEventsService(symBotClient);
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(1)).listDatafeedId();
        verify(datafeedClient, atLeastOnce()).readDatafeed("123", "ack_id_string");

    }

    @Test
    public void createAndReadDatafeed() {
        when(datafeedClient.listDatafeedId()).thenReturn(Collections.emptyList());
        when(datafeedClient.createDatafeed()).thenReturn("123");
        datafeedEventsService = new DatafeedEventsService(symBotClient);
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(1)).listDatafeedId();
        verify(datafeedClient, times(1)).createDatafeed();
        verify(datafeedClient, atLeastOnce()).readDatafeed("123", "ack_id_string");
    }

    @Test
    public void restartDatafeed() {
        DatafeedV2 id = new DatafeedV2();
        id.setId("123");
        when(datafeedClient.listDatafeedId()).thenReturn(Collections.singletonList(id));
        datafeedEventsService = new DatafeedEventsService(symBotClient);
        datafeedEventsService.restartDatafeedService();
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(2)).listDatafeedId();
        verify(datafeedClient, atLeast(1)).readDatafeed("123", "ack_id_string");
    }

    @Test
    public void stopAndReadAgain() {
        DatafeedV2 id = new DatafeedV2();
        id.setId("123");
        when(datafeedClient.listDatafeedId()).thenReturn(Collections.singletonList(id));
        datafeedEventsService = new DatafeedEventsService(symBotClient);
        datafeedEventsService.stopDatafeedService();
        datafeedEventsService.readDatafeed();
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(2)).listDatafeedId();
        verify(datafeedClient, atLeast(2)).readDatafeed("123", "ack_id_string");
    }

    @Test
    public void readStaleDatafeed() throws Exception {
        CountDownLatch doneSignal = new CountDownLatch(1);
        DatafeedV2 id = new DatafeedV2();
        Sleeper sleeper = mock(Sleeper.class);
        id.setId("123");

        when(datafeedClient.listDatafeedId()).thenReturn(Collections.singletonList(id))
                .thenReturn(Collections.emptyList());
        when(datafeedClient.createDatafeed()).thenReturn("1234");
        when(datafeedClient.readDatafeed("123", "ack_id_string"))
                .thenThrow(new APIClientErrorException("Feed was deleted because it was a stale feed"));
        when(datafeedClient.readDatafeed("1234", "ack_id_string"))
                .thenAnswer(i -> {
                    datafeedEventsServiceV2.stopDatafeedService();
                    doneSignal.countDown();
                    return Collections.emptyList();
                });

        datafeedEventsServiceV2 = new DatafeedEventsServiceV2(symBotClient, sleeper);
        doneSignal.await();

        verify(datafeedClient, times(2)).listDatafeedId();
        verify(datafeedClient, times(1)).createDatafeed();
        verify(datafeedClient, times(1)).deleteDatafeed("123");
        verify(datafeedClient, times(1)).readDatafeed("123", "ack_id_string");
        verify(datafeedClient, atLeast(1)).readDatafeed("1234", "ack_id_string");
        verify(sleeper, times(0)).sleep(anyInt());
    }
}
