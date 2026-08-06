package com.symphony.bdk.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Event;

import org.junit.jupiter.api.Test;

/** Verifies the {@code datafeed read} JSON Lines stream and its bounded lifecycle. */
class DatafeedReadTest extends CliTestBase {

  @Test
  void emitsEventsAsJsonLinesAndStopsOnCount() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final DatafeedLoop loop = mock(DatafeedLoop.class);
    when(bdk.datafeed()).thenReturn(loop);

    final RealTimeEventListener[] subscribed = new RealTimeEventListener[1];
    doAnswer(invocation -> {
      subscribed[0] = invocation.getArgument(0);
      return null;
    }).when(loop).subscribe(any());

    // when the loop "starts", deliver an event to the registered listener
    doAnswer(invocation -> {
      subscribed[0].isAcceptingEvent(new V4Event().id("E1").type("MESSAGESENT"), null);
      return null;
    }).when(loop).start();

    final int code = execute(bdk, "datafeed", "read", "--count", "1");

    assertThat(code).isZero();
    final String firstLine = stdout().trim().split("\\R")[0];
    assertThat(new ObjectMapper().readTree(firstLine).get("id").asText()).isEqualTo("E1");
    // count bound reached -> loop stopped cleanly
    verify(loop).start();
    verify(loop, atLeastOnce()).stop();
  }
}
