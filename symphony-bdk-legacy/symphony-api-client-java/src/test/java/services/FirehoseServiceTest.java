package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import lombok.SneakyThrows;
import model.DatafeedEvent;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FirehoseServiceTest {
  @Mock private SymBotClient symBotClient;
  @Mock private FirehoseClient firehoseClient;

  @Before
  public void initClient() {
    when(firehoseClient.createFirehose()).thenReturn("123");
    when(symBotClient.getFirehoseClient()).thenReturn(firehoseClient);
  }

  @SneakyThrows
  @Test
  public void readFirehoseTest() {
    FirehoseService firehoseService = new FirehoseService(symBotClient);
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    verify(firehoseClient, times(1)).createFirehose();
    verify(firehoseClient, atLeastOnce()).readFirehose("123");
  }

  @SneakyThrows
  @Test
  public void getFirehoseHandleEventFutureTest() {
    IActionFirehose actionFirehose = mock(ActionFirehose.class);
    List<DatafeedEvent> datafeedEventList = new ArrayList<>();
    DatafeedEvent datafeedEvent = mock(DatafeedEvent.class);
    datafeedEventList.add(datafeedEvent);
    when(actionFirehose.actionReadFirehose(firehoseClient, "123")).thenReturn(datafeedEventList);
    ExecutorService pool = Executors.newFixedThreadPool(5);

    FirehoseService firehoseService = new FirehoseService(symBotClient, "123", actionFirehose);
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    CompletableFuture<Object> future = firehoseService.getFirehoseHandleEventFuture(actionFirehose, pool);

    assertNotNull(future);
  }

  @SneakyThrows
  @Test
  public void restartDatafeedTest() {
    FirehoseService firehoseService = new FirehoseService(symBotClient, "123456");
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    verify(firehoseClient, atLeastOnce()).readFirehose("123456");

    firehoseService.restartDatafeedService();
    Thread.sleep(500);
    firehoseService.stopDatafeedService();
    verify(firehoseClient, times(1)).createFirehose();
    verify(firehoseClient, atLeastOnce()).readFirehose("123");
  }
}

