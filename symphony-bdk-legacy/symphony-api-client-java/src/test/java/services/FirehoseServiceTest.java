package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class FirehoseServiceTest {
    private SymBotClient symBotClient;
    private FirehoseClient firehoseClient;

    @Before
    public void initClient() {
        symBotClient = mock(SymBotClient.class);
        firehoseClient = mock(FirehoseClient.class);
        when(symBotClient.getFirehoseClient()).thenReturn(firehoseClient);
        when(firehoseClient.createFirehose()).thenReturn("123");
    }

    @SneakyThrows
    @Test
    public void readFirehoseTest() {
        FirehoseService firehoseService = new FirehoseService(symBotClient);
        firehoseService.readFirehose();
        Thread.sleep(1500);
        firehoseService.stopDatafeedService();
        verify(firehoseClient, times(1)).createFirehose();
        verify(firehoseClient, atLeastOnce()).readFirehose("123");
    }

    @SneakyThrows
    @Test
    public void restartDatafeedTest() {
        FirehoseService firehoseService = new FirehoseService(symBotClient, "123456");
        Thread.sleep(1500);
        firehoseService.stopDatafeedService();
        verify(firehoseClient, atLeastOnce()).readFirehose("123456");

        firehoseService.restartDatafeedService();
        Thread.sleep(1500);
        firehoseService.stopDatafeedService();
        verify(firehoseClient, times(1)).createFirehose();
        verify(firehoseClient, atLeastOnce()).readFirehose("123");
    }
}
