package com.symphony.bdk.bot.sdk.extapp.logging;

import com.symphony.bdk.bot.sdk.symphony.ConfigClient;

import lombok.SneakyThrows;
import org.junit.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LogControllerTest {
  private final String FILE_NAME = "src/test/resources/logs/test.log";
  private ConfigClient configClient;


  @SneakyThrows
  @Before
  public void init() {
    try {
      Files.createFile(Paths.get(FILE_NAME));
      new PrintWriter(FILE_NAME).close();
    } catch (Exception ignored) {}
    configClient = mock(ConfigClient.class);
    when(configClient.getExtAppAuthPath()).thenReturn("test");
  }

  @SneakyThrows
  @After
  public void clear() {
    // Clear Content of test log file
    try {
      new PrintWriter(FILE_NAME).close();
    } catch (Exception ignored) {}
  }

  @Test
  public void logMessageDebugTest() throws IOException {
    LogController logController = new LogController(configClient);
    ResponseEntity<String> resp = logController.logMessage(Optional.of(LogLevelEnum.DEBUG), "Debug Test Message");
    List<String> logsList = readLogContent();

    testResponse(resp);
    testLogMessage(logsList, "[CLIENT LOG] Debug Test Message");
  }

  @Test
  public void logMessageInfoTest() throws IOException {
    LogController logController = new LogController(configClient);
    ResponseEntity<String> resp = logController.logMessage(Optional.of(LogLevelEnum.INFO), "Info Test Message");
    List<String> logsList = readLogContent();

    testResponse(resp);
    testLogMessage(logsList, "[CLIENT LOG] Info Test Message");
  }

  @Test
  public void logMessageWarnTest() throws IOException {
    LogController logController = new LogController(configClient);
    ResponseEntity<String> resp = logController.logMessage(Optional.of(LogLevelEnum.WARN), "Warn Test Message");
    List<String> logsList = readLogContent();

    testResponse(resp);
    testLogMessage(logsList, "[CLIENT LOG] Warn Test Message");
  }

  @Test
  public void logMessageErrorTest() throws IOException {
    LogController logController = new LogController(configClient);
    ResponseEntity<String> resp = logController.logMessage(Optional.of(LogLevelEnum.ERROR), "Error Test Message");
    List<String> logsList = readLogContent();

    testResponse(resp);
    testLogMessage(logsList, "[CLIENT LOG] Error Test Message");
  }

  @Test
  public void logMessageDefaultLevelTest() throws IOException {
    LogController logController = new LogController(configClient);
    ResponseEntity<String> resp = logController.logMessage(Optional.empty(), "Default Test Message");
    List<String> logsList = readLogContent();

    testResponse(resp);
    testLogMessage(logsList, "[CLIENT LOG] Default Test Message");
  }

  private void testLogMessage(List<String> logsList, String msg) {
    assertEquals(msg, logsList.get(0));
  }

  private void testResponse(ResponseEntity<String> resp) {
    assertNull(resp.getBody());
    assertEquals(HttpStatus.OK, resp.getStatusCode());
  }

  private List<String> readLogContent() throws IOException {
    List<String> result;
    try(Stream<String> lines = Files.lines(Paths.get(FILE_NAME))) {
      result = lines.collect(Collectors.toList());
    }
    return result;
  }
}
