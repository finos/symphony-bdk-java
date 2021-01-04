package com.symphony.bdk.core.activity.room;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4RoomCreated;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoomCreatedActivityTest {

  private TestRoomCreatedActivity activity;

  @Mock DatafeedLoop datafeedLoop;

  @BeforeEach
  void setUp() {
    activity = new TestRoomCreatedActivity();
    activity.bindToRealTimeEventsSource(datafeedLoop::subscribe);
  }

  @Test
  void testMatcher() {
    activity.setMatcher(c -> c.getRoom().getStreamId().equals("test-room-id"));

    final RoomCreatedContext context = createContext();

    context.setRoom(new V4Stream().streamId("test-room-id"));
    assertTrue(activity.matcher().matches(context));
  }

  @Test
  void testBeforeMatcher() {
    final String streamId = "test-room-id";

    final RoomCreatedContext context = createContext();
    context.getSourceEvent().setStream(new V4Stream().streamId(streamId));

    activity.beforeMatcher(context);

    assertEquals(context.getRoom().getStreamId(), streamId);
  }

  private static RoomCreatedContext createContext() {
    return new RoomCreatedContext(new V4Initiator(), new V4RoomCreated());
  }
}
