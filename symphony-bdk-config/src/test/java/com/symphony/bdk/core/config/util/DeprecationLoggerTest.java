package com.symphony.bdk.core.config.util;

import static com.symphony.bdk.core.config.util.DeprecationLogger.LOGGER_NAME;
import static com.symphony.bdk.core.config.util.DeprecationLogger.logDeprecation;
import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

class DeprecationLoggerTest {

  private ListAppender<ILoggingEvent> listAppender;
  private Logger logger;

  @BeforeEach
  void setUp() {
    logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);

    listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);
  }

  @Test
  void testDeprecation() {
    final String message = "message";

    logger.setLevel(Level.WARN);
    logDeprecation(message);

    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size());

    final ILoggingEvent loggingEvent = logsList.get(0);
    assertEquals(message, loggingEvent.getMessage());
    assertEquals(Level.WARN, loggingEvent.getLevel());
  }

  @Test
  void testDeprecationNotLogged() {
    logger.setLevel(Level.ERROR);
    logDeprecation("message");

    assertEquals(0, listAppender.list.size());
  }
}
