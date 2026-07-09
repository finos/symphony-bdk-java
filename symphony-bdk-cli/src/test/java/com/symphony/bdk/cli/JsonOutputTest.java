package com.symphony.bdk.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.model.UserV2;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Iterator;

/** Verifies that command results are valid JSON on stdout and that logging is routed to stderr. */
class JsonOutputTest extends CliTestBase {

  @Test
  void stdoutIsValidJsonDocument() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final SessionService sessions = mock(SessionService.class);
    when(bdk.sessions()).thenReturn(sessions);
    when(sessions.getSession()).thenReturn(new UserV2().id(12345L).displayName("Test Bot"));

    final int code = execute(bdk, "whoami");

    assertThat(code).isZero();
    final JsonNode node = new ObjectMapper().readTree(stdout());
    assertThat(node.get("id").asLong()).isEqualTo(12345L);
    assertThat(node.get("displayName").asText()).isEqualTo("Test Bot");
    // nothing but the JSON document on stdout
    assertThat(stdout().trim()).startsWith("{").endsWith("}");
    assertThat(stderr()).isEmpty();
  }

  @Test
  void bundledLogbackRoutesAllAppendersToStderr() throws Exception {
    // load the bundled config in isolation so the assertion is about the artifact, not ambient state
    final LoggerContext context = new LoggerContext();
    final JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    try (InputStream config = getClass().getResourceAsStream("/logback.xml")) {
      assertThat(config).as("bundled logback.xml is on the classpath").isNotNull();
      configurator.doConfigure(config);
    }

    final Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
    final Iterator<Appender<ILoggingEvent>> appenders = root.iteratorForAppenders();
    assertThat(appenders.hasNext()).as("root logger has at least one appender").isTrue();
    while (appenders.hasNext()) {
      final Appender<ILoggingEvent> appender = appenders.next();
      assertThat(appender).isInstanceOf(ConsoleAppender.class);
      assertThat(((ConsoleAppender<ILoggingEvent>) appender).getTarget()).isEqualTo("System.err");
    }
  }
}
