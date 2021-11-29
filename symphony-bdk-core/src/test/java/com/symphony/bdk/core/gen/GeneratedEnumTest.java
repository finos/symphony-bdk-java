package com.symphony.bdk.core.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.symphony.bdk.gen.api.model.UserAttributes;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * We modified the templates/modelInnerEnum.mustache in order to not throw an exception when a returned enum value
 * is unknown. Instead, we just print a warning and return null. This specific test class aims to highlight this
 * modification and ensures that removing or modifying the mustache template will break the unit tests.
 */
public class GeneratedEnumTest {

  @Test
  @DisplayName("Generated enum should not fail on unknown value")
  void shouldNotFailOnUnknownEnumValue() {

    final Logger logger = (Logger) LoggerFactory.getLogger("com.symphony.bdk.gen.api.model");
    final ListAppender<ILoggingEvent> testAppender = new ListAppender<>();
    logger.addAppender(testAppender);

    testAppender.start();
    assertNull(UserAttributes.AccountTypeEnum.fromValue("FOOBAR"));
    testAppender.stop();

    assertEquals(1, testAppender.list.size());
    assertEquals(Level.WARN, testAppender.list.get(0).getLevel());
    assertEquals("Unexpected value 'FOOBAR', returning null.", testAppender.list.get(0).getMessage());
  }
}
