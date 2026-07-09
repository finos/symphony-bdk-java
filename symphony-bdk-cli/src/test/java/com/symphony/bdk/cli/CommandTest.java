package com.symphony.bdk.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.UserSearchQuery;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2MembershipList;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.gen.api.model.V3HealthStatus;
import com.symphony.bdk.gen.api.model.V4Message;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/** Per-command behaviour against a fully mocked {@link SymphonyBdk} facade. */
class CommandTest extends CliTestBase {

  private static final ObjectMapper JSON = new ObjectMapper();

  @Test
  void whoamiPrintsSession() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final SessionService sessions = mock(SessionService.class);
    when(bdk.sessions()).thenReturn(sessions);
    when(sessions.getSession()).thenReturn(new UserV2().id(7L).username("bot"));

    assertThat(execute(bdk, "whoami")).isZero();
    assertThat(JSON.readTree(stdout()).get("username").asText()).isEqualTo("bot");
  }

  @Test
  void messageSendPostsAndPrintsResult() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final MessageService messages = mock(MessageService.class);
    when(bdk.messages()).thenReturn(messages);
    when(messages.send(eq("STREAM1"), eq("hello"))).thenReturn(new V4Message().messageId("MSG1"));

    assertThat(execute(bdk, "message", "send", "STREAM1", "--message", "hello")).isZero();
    verify(messages).send("STREAM1", "hello");
    assertThat(JSON.readTree(stdout()).get("messageId").asText()).isEqualTo("MSG1");
  }

  @Test
  void messageGetPrintsMessage() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final MessageService messages = mock(MessageService.class);
    when(bdk.messages()).thenReturn(messages);
    when(messages.getMessage("MSG1")).thenReturn(new V4Message().messageId("MSG1").message("hi"));

    assertThat(execute(bdk, "message", "get", "MSG1")).isZero();
    assertThat(JSON.readTree(stdout()).get("message").asText()).isEqualTo("hi");
  }

  @Test
  void messageListPrintsJsonArray() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final MessageService messages = mock(MessageService.class);
    when(bdk.messages()).thenReturn(messages);
    when(messages.listMessages(eq("STREAM1"), any(Instant.class), any(PaginationAttribute.class)))
        .thenReturn(List.of(new V4Message().messageId("A"), new V4Message().messageId("B")));

    assertThat(execute(bdk, "message", "list", "STREAM1")).isZero();
    final JsonNode array = JSON.readTree(stdout());
    assertThat(array.isArray()).isTrue();
    assertThat(array).hasSize(2);
  }

  @Test
  void streamListPrintsJsonArray() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final StreamService streams = mock(StreamService.class);
    when(bdk.streams()).thenReturn(streams);
    when(streams.listStreams(any(), any())).thenReturn(List.of(new StreamAttributes().id("S1")));

    assertThat(execute(bdk, "stream", "list")).isZero();
    assertThat(JSON.readTree(stdout()).isArray()).isTrue();
  }

  @Test
  void streamMembersPrintsResult() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final StreamService streams = mock(StreamService.class);
    when(bdk.streams()).thenReturn(streams);
    when(streams.listStreamMembers("S1")).thenReturn(new V2MembershipList());

    assertThat(execute(bdk, "stream", "members", "S1")).isZero();
    assertThat(JSON.readTree(stdout())).isNotNull();
  }

  @Test
  void streamGetPrintsStream() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final StreamService streams = mock(StreamService.class);
    when(bdk.streams()).thenReturn(streams);
    when(streams.getStream("S1")).thenReturn(new V2StreamAttributes().id("S1"));

    assertThat(execute(bdk, "stream", "get", "S1")).isZero();
    assertThat(JSON.readTree(stdout()).get("id").asText()).isEqualTo("S1");
  }

  @Test
  void userGetByNumericIdUsesIdLookup() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final UserService users = mock(UserService.class);
    when(bdk.users()).thenReturn(users);
    when(users.listUsersByIds(anyList())).thenReturn(List.of(new UserV2().id(42L)));

    assertThat(execute(bdk, "user", "get", "42")).isZero();
    final ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
    verify(users).listUsersByIds(captor.capture());
    assertThat(captor.getValue()).containsExactly(42L);
    assertThat(JSON.readTree(stdout()).get("id").asLong()).isEqualTo(42L);
  }

  @Test
  void userGetByEmailUsesEmailLookup() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final UserService users = mock(UserService.class);
    when(bdk.users()).thenReturn(users);
    when(users.listUsersByEmails(anyList()))
        .thenReturn(List.of(new UserV2().emailAddress("a@b.com")));

    assertThat(execute(bdk, "user", "get", "a@b.com")).isZero();
    verify(users).listUsersByEmails(Collections.singletonList("a@b.com"));
    assertThat(JSON.readTree(stdout()).get("emailAddress").asText()).isEqualTo("a@b.com");
  }

  @Test
  void userGetNotFoundExitsThree() {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final UserService users = mock(UserService.class);
    when(bdk.users()).thenReturn(users);
    when(users.listUsersByIds(anyList())).thenReturn(Collections.emptyList());

    assertThat(execute(bdk, "user", "get", "999")).isEqualTo(3);
  }

  @Test
  void userSearchPrintsJsonArray() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final UserService users = mock(UserService.class);
    when(bdk.users()).thenReturn(users);
    when(users.searchUsers(any(UserSearchQuery.class), any(), any()))
        .thenReturn(List.of(new UserV2().id(1L), new UserV2().id(2L)));

    assertThat(execute(bdk, "user", "search", "alice")).isZero();
    assertThat(JSON.readTree(stdout()).isArray()).isTrue();
  }

  @Test
  void healthCheckPrintsStatus() throws Exception {
    final SymphonyBdk bdk = mock(SymphonyBdk.class);
    final HealthService health = mock(HealthService.class);
    when(bdk.health()).thenReturn(health);
    when(health.healthCheck()).thenReturn(new V3Health().status(V3HealthStatus.UP));

    assertThat(execute(bdk, "health", "check")).isZero();
    assertThat(JSON.readTree(stdout()).get("status").asText()).isEqualTo("UP");
  }
}
