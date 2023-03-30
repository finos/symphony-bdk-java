package com.symphony.bdk.examples;

import static com.symphony.bdk.test.SymphonyBdkTestUtils.pushEventToDataFeed;
import static com.symphony.bdk.test.SymphonyBdkTestUtils.pushMessageToDF;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4User;
import com.symphony.bdk.test.SymphonyBdkTestUtils;
import com.symphony.bdk.test.annotation.SymphonyBdkTest;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@SymphonyBdkTest
public class SampleBdkIntegrationTest {
  private final V4User initiator = new V4User().displayName("user").userId(2L);
  private final V4Stream stream = new V4Stream().streamId("my-room");

  private SymphonyBdk bdk;

  @Test
  @DisplayName("Reply echo slash command, inject bdk as parameter")
  void testEchoSlashCommand(SymphonyBdk bdk) {
    final SlashCommand slashCommand = SlashCommand.slash("/echo {argument}", false,
        context -> bdk.messages()
            .send(context.getStreamId(),
                String.format("Received argument: %s", context.getArguments().get("argument"))),
        "echo slash command");
    bdk.activities().register(slashCommand);

    // given
    when(bdk.messages().send(anyString(), any(Message.class))).thenReturn(mock(V4Message.class));

    // when
    pushMessageToDF(initiator, stream, "/echo arg");

    // then
    verify(bdk.messages()).send(eq("my-room"), contains("Received argument: arg"));
  }

  @Test
  @DisplayName("Reply upon received gif category form reply, inject bdk as property")
  void gif_form_replyWithMessage() {

    bdk.activities().register(new GifFormActivity(bdk.messages()));

    // given
    when(bdk.messages().send(anyString(), anyString())).thenReturn(mock(V4Message.class));

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
        .type(SymphonyBdkTestUtils.V4EventType.SYMPHONYELEMENTSACTION.name()));

    // then
    verify(bdk.messages()).send(eq("my-room"), contains("Gif category is \"bdk\""));
  }

  public static class GifFormActivity extends FormReplyActivity<FormReplyContext> {

    private final MessageService messageService;

    public GifFormActivity(MessageService messageService) {
      this.messageService = messageService;
    }

    @Override
    public ActivityMatcher<FormReplyContext> matcher() {
      return context -> "gif-category-form".equals(context.getFormId())
          && "submit".equals(context.getFormValue("action"))
          && StringUtils.isNotEmpty(context.getFormValue("category"));
    }

    @Override
    public void onActivity(FormReplyContext context) {
      this.messageService.send(context.getStreamId(),
          String.format("Gif category is \"%s\"", context.getFormValue("category")));
    }

    @Override
    protected ActivityInfo info() {
      return new ActivityInfo()
          .type(ActivityType.FORM)
          .name("Gif Display category form command")
          .description("\"Form handler for the Gif Category form\"");
    }
  }
}
