package services;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class DatafeedEventsServiceV1Test {
    private SymBotClient symBotClient;
    private DatafeedEventsService datafeedEventsService;
    private DatafeedClient datafeedClient;

    @Before
    public void initClient() {
        symBotClient = mock(SymBotClient.class);
        SymConfig config = mock (SymConfig.class);
        when(config.getDatafeedVersion()).thenReturn("v1");
        when(symBotClient.getConfig()).thenReturn(config);

        datafeedClient = mock(DatafeedClient.class);
        when(symBotClient.getDatafeedClient()).thenReturn(datafeedClient);
        when(datafeedClient.createDatafeed()).thenReturn("123");
        when(datafeedClient.getAckId()).thenReturn("ack_id_string");
    }

    @SneakyThrows
    @Test
    public void readDatafeedTest() {
        datafeedEventsService = new DatafeedEventsService(symBotClient);
        Thread.sleep(1500);
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(1)).createDatafeed();
        verify(datafeedClient, atLeastOnce()).readDatafeed("123");
    }

    @SneakyThrows
    @Test
    public void restartDatafeedTest() {
        datafeedEventsService = new DatafeedEventsService(symBotClient);
        Thread.sleep(1500);
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(1)).createDatafeed();
        verify(datafeedClient, atLeastOnce()).readDatafeed("123");

        when(datafeedClient.createDatafeed()).thenReturn("123456");
        datafeedEventsService.restartDatafeedService();
        Thread.sleep(1500);
        datafeedEventsService.stopDatafeedService();
        verify(datafeedClient, times(2)).createDatafeed();
        verify(datafeedClient, atLeastOnce()).readDatafeed("123456");
    }
}
