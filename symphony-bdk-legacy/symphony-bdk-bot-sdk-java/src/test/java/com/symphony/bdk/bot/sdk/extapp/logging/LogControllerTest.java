package com.symphony.bdk.bot.sdk.extapp.logging;

import com.symphony.bdk.bot.sdk.symphony.ConfigClient;

import lombok.SneakyThrows;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.junit.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LogControllerTest {
  private final String FILE_DIR = "src/test/resources/logs/";
  private ConfigClient configClient;
  private Path logFilePath;

  @SneakyThrows
  @Before
  public void init() {
    Files.createDirectories(Paths.get(FILE_DIR));
    logFilePath = Files.createTempFile(Paths.get(FILE_DIR), "test", ".log");
    FileAppender fileAppender = new FileAppender();
    fileAppender.setName("LogControllerFileAppender");
    fileAppender.setLayout(new PatternLayout());
    fileAppender.setFile(logFilePath.toString());
    fileAppender.setAppend(true);
    fileAppender.activateOptions();

    BasicConfigurator.configure(fileAppender);
    configClient = mock(ConfigClient.class);
    when(configClient.getExtAppAuthPath()).thenReturn("test");
  }

  @SneakyThrows
  @After
  public void clear() {
    BasicConfigurator.resetConfiguration();
    logFilePath.toFile().deleteOnExit();
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
    try(Stream<String> lines = Files.lines(Paths.get(logFilePath.toString()))) {
      result = lines.collect(Collectors.toList());
    }
    return result;
  }
}
