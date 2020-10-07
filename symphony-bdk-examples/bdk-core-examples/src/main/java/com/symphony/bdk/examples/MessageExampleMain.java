package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.gen.api.model.MessageIdsFromStream;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4ImportedMessage;
import com.symphony.bdk.gen.api.model.V4Message;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * This demonstrates a basic usage of the message service.
 */
public class MessageExampleMain {

  public static final String STREAM_ID = "gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA";
  public static final String MESSAGE = "<messageML>Hello, World!</messageML>";

  public static final Instant SINCE = Instant.now().minus(Duration.ofHours(1));
  public static final Instant TO = Instant.now();

  public static void main(String[] args) throws Exception {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    //send a regular message
    final V4Message regularMessage = bdk.messages().send(STREAM_ID, MESSAGE);

    //use templates

    final V4Message sendWithCustomTemplate = bdk.messages()
        .sendWithTemplate(STREAM_ID, "/complex-message.ftl", Collections.singletonMap("name", "Freemarker"));

    //retrieve the details of existing messages
    final V4Message message = bdk.messages().getMessage(regularMessage.getMessageId());

    //retrieve a list of messages
    final List<V4Message> messages = bdk.messages().getMessages(STREAM_ID, SINCE, null, 2);
    final Stream<V4Message> messagesStream = bdk.messages().getMessagesStream(STREAM_ID, SINCE, 2, 6);

    //retrieve a list of messageIds
    final MessageIdsFromStream messageIdsByTimestamp =
        bdk.messages().getMessageIdsByTimestamp(STREAM_ID, SINCE, TO, 2, 0);

    final Stream<String> messageIdsByTimestampStream =
        bdk.messages().getMessageIdsByTimestampStream(STREAM_ID, SINCE, TO, 2, 6);

    //message status, receipts, relationships
    final MessageStatus messageStatus = bdk.messages().getMessageStatus(message.getMessageId());
    final MessageReceiptDetailResponse messageReceiptDetailResponse = bdk.messages().listMessageReceipts(message.getMessageId());
    final MessageMetadataResponse messageRelationships = bdk.messages().getMessageRelationships(message.getMessageId());

    //attachment
    final List<String> attachmentTypes = bdk.messages().getAttachmentTypes();
    final List<StreamAttachmentItem> streamAttachmentItems =
        bdk.messages().listAttachments(STREAM_ID, SINCE, TO, 3, AttachmentSort.ASC);
    final byte[] attachment = bdk.messages().getAttachment(STREAM_ID, message.getMessageId(), message.getAttachments().get(0).getId());

    //import a message
    V4ImportedMessage msg = new V4ImportedMessage();
    msg.setIntendedMessageFromUserId(12987981103694L);
    msg.setIntendedMessageTimestamp(1599481528000L);
    msg.setStreamId(STREAM_ID);
    msg.setMessage(MESSAGE);
    msg.setOriginatingSystemId("fooChat");
    final List<V4ImportResponse> v4ImportResponses = bdk.messages().importMessages(Collections.singletonList(msg));

    // suppress message
    final MessageSuppressionResponse messageSuppression = bdk.messages().suppressMessage(message.getMessageId());
  }
}
