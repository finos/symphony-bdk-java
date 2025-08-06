package com.symphony.bdk.examples.spring;

import static com.symphony.bdk.test.SymphonyBdkTestUtils.V4EventType;
import static com.symphony.bdk.test.SymphonyBdkTestUtils.pushEventToDataFeed;
import static com.symphony.bdk.test.SymphonyBdkTestUtils.pushMessageToDF;
import static com.symphony.bdk.test.mockito.MessageMatchers.containsContent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.ext.group.SymphonyGroupService;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4User;
import com.symphony.bdk.test.spring.annotation.SymphonyBdkSpringBootTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@SymphonyBdkSpringBootTest(properties = {"bot.id=1", "bot.username=my-bot", "bot.display-name=my bot"})
public class SampleSpringAppIntegrationTest {
  private final V4User initiator = new V4User().displayName("user").userId(2L);
  private final V4Stream stream = new V4Stream().streamId("my-room");

  @MockitoBean SymphonyGroupService symphonyGroupService;

  @Test
  @DisplayName("Reply message upon received echo command")
  void echo_command_replyWithMessage(@Autowired MessageService messageService, @Autowired UserV2 botInfo) {
    // given
    when(messageService.send(anyString(), any(Message.class))).thenReturn(mock(V4Message.class));

    // when
    pushMessageToDF(initiator, stream, "/echo arg", botInfo);

    // then
    verify(messageService).send(eq("my-room"), contains("Received argument: arg at"));
  }

  @Test
  @DisplayName("Reply message upon received gif command")
  void gif_command_replyWithFormMessage(@Autowired MessageService messageService, @Autowired UserV2 botInfo) {
    // given
    when(messageService.send(anyString(), any(Message.class))).thenReturn(mock(V4Message.class));

    // when
    pushMessageToDF(initiator, stream, "/gif", botInfo);

    // then
    verify(messageService).send(eq("my-room"), containsContent("<form id=\"gif-category-form\">"));
  }

  @Test
  @DisplayName("Reply message upon received gif category form reply")
  void gif_form_replyWithMessage(@Autowired MessageService messageService) {
    // given
    when(messageService.send(anyString(), anyString())).thenReturn(mock(V4Message.class));

    // when
    Map<String, Object> values = new HashMap<>();
    values.put("action", "submit");
    values.put("category", "bdk");
    pushEventToDataFeed(new V4Event().id("id").timestamp(Instant.now().toEpochMilli())
                     .initiator(new V4Initiator().user(initiator))
                     .payload(new V4Payload().symphonyElementsAction(
                        new V4SymphonyElementsAction().formId("gif-category-form")
                            .formMessageId("form-message-id")
                            .formValues(values)
                            .stream(stream)))
                    .type(V4EventType.SYMPHONYELEMENTSACTION.name()));

    // then
    verify(messageService).send(eq("my-room"), contains("Gif category is \"bdk\""));
  }

}
