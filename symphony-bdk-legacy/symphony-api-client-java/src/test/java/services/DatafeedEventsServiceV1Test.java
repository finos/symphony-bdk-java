package services;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.LoadBalancing;
import configuration.LoadBalancingMethod;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;
import exceptions.SymClientException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

public class DatafeedEventsServiceV1Test {
  private final String testDatafeedFile = "src/test/resources/testdatafeed.id";
  @Mock private SymBotClient symBotClient;
  private SymConfig config;
  private DatafeedEventsService datafeedEventsService;
  private DatafeedClient datafeedClient;

  @Before
  public void initClient() {
    config = mock(SymConfig.class);
    when(config.getDatafeedVersion()).thenReturn("v1");
    when(config.getDatafeedEventsErrorTimeout()).thenReturn(1);
    symBotClient = mock(SymBotClient.class);
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
    // Wait for async operation to finish
    Thread.sleep(500);
    datafeedEventsService.stopDatafeedService();
    verify(datafeedClient, times(1)).createDatafeed();
    verify(datafeedClient, atLeastOnce()).readDatafeed("123");
  }

  @SneakyThrows
  @Test
  public void readDatafeedReuseDatafeedIDTest() {
    LoadBalancing loadBalancing = mock(LoadBalancing.class);
    when(loadBalancing.getMethod()).thenReturn(LoadBalancingMethod.external);
    SymLoadBalancedConfig lbConfig = mock(SymLoadBalancedConfig.class);
    when(lbConfig.getDatafeedVersion()).thenReturn("v1");
    when(lbConfig.getDatafeedEventsErrorTimeout()).thenReturn(1);
    when(lbConfig.getReuseDatafeedID()).thenReturn(true);
    when(lbConfig.getLoadBalancing()).thenReturn(loadBalancing);
    when(symBotClient.getConfig()).thenReturn(lbConfig);
    when(symBotClient.getDatafeedIdFile()).thenReturn(new File(testDatafeedFile));
    datafeedEventsService = new DatafeedEventsService(symBotClient);
    // Wait for async operation to finish
    Thread.sleep(500);
    datafeedEventsService.stopDatafeedService();
    verify(datafeedClient, times(0)).createDatafeed();
    verify(datafeedClient, atLeastOnce()).readDatafeed("12345-test");
  }

  @SneakyThrows
  @Test
  public void createDatafeedErrorTest() {
    when(datafeedClient.createDatafeed()).thenThrow(new SymClientException("Test Exception")).thenReturn("123");
    datafeedEventsService = new DatafeedEventsService(symBotClient);
    // Wait for async operation to finish
    Thread.sleep(500);
    datafeedEventsService.stopDatafeedService();
    verify(datafeedClient, times(2)).createDatafeed();
    verify(datafeedClient, atLeastOnce()).readDatafeed("123");
  }

  @SneakyThrows
  @Test
  public void restartDatafeedTest() {
    datafeedEventsService = new DatafeedEventsService(symBotClient);
    Thread.sleep(500);
    datafeedEventsService.stopDatafeedService();
    verify(datafeedClient, times(1)).createDatafeed();
    verify(datafeedClient, atLeastOnce()).readDatafeed("123");

    when(datafeedClient.createDatafeed()).thenReturn("123456");
    datafeedEventsService.restartDatafeedService();
    Thread.sleep(500);
    datafeedEventsService.stopDatafeedService();
    verify(datafeedClient, times(2)).createDatafeed();
    verify(datafeedClient, atLeastOnce()).readDatafeed("123456");
  }
}
