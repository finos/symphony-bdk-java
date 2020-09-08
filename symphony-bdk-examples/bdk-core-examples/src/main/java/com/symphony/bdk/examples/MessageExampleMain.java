package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.gen.api.model.MessageIdsFromStream;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4ImportedMessage;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.template.api.TemplateException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This demonstrates a basic usage of the message service.
 */
public class MessageExampleMain {

  public static final String STREAM_ID = "gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA";
  public static final String MESSAGE = "<messageML>Hello, World!</messageML>";
  public static final String MESSAGE_ID = "LE1TxlLArVbpNKn-CXZuZn___ouXdRpnbQ";

  public static void main(String[] args)
      throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException, TemplateException {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    //send a regular message
    final V4Message regularMessage = bdk.messages().send(STREAM_ID, MESSAGE);

    //use templates
    final V4Message sentWithBuiltInTemplate = bdk.messages().send(STREAM_ID, "simpleMML",
        new HashMap<String, String>() {{
          put("message", "Hello from a built-in template!");
        }});

    final V4Message sendWithCustomTemplate = bdk.messages()
        .send(STREAM_ID, "customTemplate.ftl", new HashMap<String, String>());

    //retrieve the details of existing messages
    final V4Message message = bdk.messages().getMessage(MESSAGE_ID);
    final List<V4Message> messages = bdk.messages().getMessages(STREAM_ID, 1599474256000L, null, 2);
    final MessageIdsFromStream messageIdsByTimestamp =
        bdk.messages().getMessageIdsByTimestamp(STREAM_ID, 1599474256000L, 1599494456000L, 2, 0);

    final MessageStatus messageStatus = bdk.messages().getMessageStatus(MESSAGE_ID);
    final MessageReceiptDetailResponse messageReceiptDetailResponse = bdk.messages().listMessageReceipts(MESSAGE_ID);
    final MessageMetadataResponse messageRelationships = bdk.messages().getMessageRelationships(MESSAGE_ID);

    //attachment
    final List<String> attachmentTypes = bdk.messages().getAttachmentTypes();
    final List<StreamAttachmentItem> streamAttachmentItems =
        bdk.messages().listAttachments(STREAM_ID, 1599481767310L, 1599481767330L, 3, true);
    final byte[] attachment = bdk.messages().getAttachment(STREAM_ID, MESSAGE_ID, "internal_14568529");

    //import a message
    V4ImportedMessage msg = new V4ImportedMessage();
    msg.setIntendedMessageFromUserId(12987981103694L);
    msg.setIntendedMessageTimestamp(1599481528000L);
    msg.setStreamId(STREAM_ID);
    msg.setMessage(MESSAGE);
    msg.setOriginatingSystemId("fooChat");
    final List<V4ImportResponse> v4ImportResponses = bdk.messages().importMessages(Collections.singletonList(msg));

    // suppress message
    final MessageSuppressionResponse messageSuppression = bdk.messages().suppressMessage(MESSAGE_ID);
  }
}
