package com.symphony.bdk.core.activity.room;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.gen.api.model.V4User;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserJoinedRoomActivityTest {

  private TestUserJoinedRoomActivity activity;

  @Mock DatafeedLoop datafeedLoop;

  @BeforeEach
  void setUp() {
    activity = new TestUserJoinedRoomActivity();
    activity.bindToRealTimeEventsSource(datafeedLoop::subscribe);
  }

  @Test
  void testMatcher() {
    activity.setMatcher(c -> c.getRoomId().equals("test-room-id") && c.getUserId() == 1234L);

    final UserJoinedRoomContext context = createContext();

    context.setRoomId("test-room-id");
    context.setUserId(1234L);
    assertTrue(activity.matcher().matches(context));
  }

  @Test
  void testBeforeMatcher() {
    final String roomId = "test-room-id";

    final UserJoinedRoomContext context = createContext();
    context.getSourceEvent().setStream(new V4Stream().streamId(roomId));
    context.getSourceEvent().setAffectedUser(new V4User().userId(1234L));

    activity.beforeMatcher(context);

    assertEquals(context.getRoomId(), roomId);
    assertEquals(context.getUserId(), 1234L);
  }

  private static UserJoinedRoomContext createContext() {
    return new UserJoinedRoomContext(new V4Initiator(), new V4UserJoinedRoom());
  }
}
