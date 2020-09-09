package com.symphony.bdk.core.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MessageIdsFromStream;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * Test class for the {@link MessageService}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MessageServiceTest {

  private static final String MESSAGE = "message";
  private static final String STREAM_ID = "streamId";
  private static final String MESSAGE_ID = "messageId";
  private static final String TEMPLATE_NAME = "template";

  public static final Instant SINCE = Instant.now().minus(Duration.ofHours(12));
  public static final Instant TO = Instant.now();

  @Mock
  private MessagesApi messagesApi;

  @Mock
  private MessageApi messageApi;

  @Mock
  private MessageSuppressionApi messageSuppressionApi;

  @Mock
  private StreamsApi streamsApi;

  @Mock
  private PodApi podApi;

  @Mock
  private AttachmentsApi attachmentsApi;

  @Mock
  private DefaultApi defaultApi;

  @Mock
  private AuthSession authSession;

  @Mock
  private TemplateEngine templateEngine;

  private MessageService messageService;

  @BeforeEach
  void setUp() {
    messageService = new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi,
        attachmentsApi, defaultApi, authSession, templateEngine);
    when(authSession.getSessionToken()).thenReturn("sessionToken");
    when(authSession.getKeyManagerToken()).thenReturn("keyManagerToken");
  }

  @Test
  void testGetMessages() throws ApiException {
    when(messagesApi.v4StreamSidMessageGet(any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());

    assertNotNull(messageService.getMessages(STREAM_ID, SINCE, 0, 0));
  }

  @Test
  void testSendWithStreamObject() throws ApiException {
    when(messagesApi.v4StreamSidMessageCreatePost(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(new V4Message());

    final V4Stream v4Stream = new V4Stream();
    v4Stream.setStreamId(STREAM_ID);

    assertNotNull(messageService.send(v4Stream, MESSAGE));
  }

  @Test
  void testSendWithTemplateAndStreamObject() throws ApiException, TemplateException {
    when(messagesApi.v4StreamSidMessageCreatePost(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(new V4Message());

    final String templateName = TEMPLATE_NAME;
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(templateName));
    when(templateEngine.newBuiltInTemplate(eq(templateName))).thenReturn(parameters -> MESSAGE);

    final V4Stream v4Stream = new V4Stream();
    v4Stream.setStreamId(STREAM_ID);

    assertNotNull(messageService.send(v4Stream, templateName, Collections.emptyMap()));
  }

  @Test
  void testSendWithTemplate() throws ApiException, TemplateException {
    when(messagesApi.v4StreamSidMessageCreatePost(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(new V4Message());

    final String templateName = TEMPLATE_NAME;
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(templateName));
    when(templateEngine.newBuiltInTemplate(eq(templateName))).thenReturn(parameters -> MESSAGE);

    assertNotNull(messageService.send(STREAM_ID, templateName, Collections.emptyMap()));
  }

  @Test
  void testSendWithTemplateThrowingTemplateException() throws TemplateException {
    final String templateName = TEMPLATE_NAME;
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(templateName));
    when(templateEngine.newBuiltInTemplate(eq(templateName))).thenReturn(parameters -> {
      throw new TemplateException("error");
    });

    final V4Stream v4Stream = new V4Stream();
    v4Stream.setStreamId(STREAM_ID);

    assertThrows(TemplateException.class, () -> messageService.send(v4Stream, templateName, Collections.emptyMap()));
  }

  @Test
  void testGetAttachment() throws ApiException {
    when(attachmentsApi.v1StreamSidAttachmentGet(any(), any(), any(), any(), any())).thenReturn(new byte[0]);
    assertNotNull(messageService.getAttachment(STREAM_ID, MESSAGE_ID, "attachmentId"));
  }

  @Test
  void testImportMessages() throws ApiException {
    when(messagesApi.v4MessageImportPost(any(), any(), any())).thenReturn(Collections.emptyList());
    assertNotNull(messageService.importMessages(Collections.emptyList()));
  }

  @Test
  void testSuppressMessage() throws ApiException {
    when(messageSuppressionApi.v1AdminMessagesuppressionIdSuppressPost(any(), any()))
        .thenReturn(new MessageSuppressionResponse());
    assertNotNull(messageService.suppressMessage(MESSAGE_ID));
  }

  @Test
  void testGetMessageStatus() throws ApiException {
    when(messageApi.v1MessageMidStatusGet(any(), any())).thenReturn(new MessageStatus());
    assertNotNull(messageService.getMessageStatus(MESSAGE_ID));
  }

  @Test
  void testGetAttachmentTypes() throws ApiException {
    when(podApi.v1FilesAllowedTypesGet(any())).thenReturn(Collections.emptyList());
    assertNotNull(messageService.getAttachmentTypes());
  }

  @Test
  void testGetMessage() throws ApiException {
    when(messagesApi.v1MessageIdGet(any(), any(), any())).thenReturn(new V4Message());
    assertNotNull(messageService.getMessage(MESSAGE_ID));
  }

  @Test
  void testListAttachmentsWithSortAscendingNull() throws ApiException {
    when(streamsApi.v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());
    assertNotNull(messageService.listAttachments(STREAM_ID, SINCE, TO, 0, null));
    verify(streamsApi).v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), eq("ASC"));
  }

  @Test
  void testListAttachmentsWithSortAscendingTrue() throws ApiException {
    when(streamsApi.v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());
    assertNotNull(messageService.listAttachments(STREAM_ID, SINCE, TO, 0, true));
    verify(streamsApi).v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), eq("ASC"));
  }

  @Test
  void testListAttachmentsWithSortAscendingFalse() throws ApiException {
    when(streamsApi.v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());
    assertNotNull(messageService.listAttachments(STREAM_ID, SINCE, TO, 0, false));
    verify(streamsApi).v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), eq("DESC"));
  }

  @Test
  void testGetMessageIdsByTimestamp() throws ApiException {
    when(defaultApi.v2AdminStreamsStreamIdMessageIdsGet(any(), any(), any(), any(), any(), any()))
        .thenReturn(new MessageIdsFromStream());
    assertNotNull(messageService.getMessageIdsByTimestamp(STREAM_ID, SINCE, TO, 0, 0));
  }

  @Test
  void testListMessageReceipts() throws ApiException {
    when(defaultApi.v1AdminMessagesMessageIdReceiptsGet(any(), any(), any(), any()))
        .thenReturn(new MessageReceiptDetailResponse());
    assertNotNull(messageService.listMessageReceipts(MESSAGE_ID));
  }

  @Test
  void testGetMessageRelationships() throws ApiException {
    when(defaultApi.v1AdminMessagesMessageIdMetadataRelationshipsGet(any(), any(), any()))
        .thenReturn(new MessageMetadataResponse());
    assertNotNull(messageService.getMessageRelationships(MESSAGE_ID));
  }

  @Test
  void testApiCallThrowingApiException() throws ApiException {
    when(defaultApi.v1AdminMessagesMessageIdMetadataRelationshipsGet(any(), any(), any()))
        .thenThrow(new ApiException(500, "error"));
    assertThrows(ApiRuntimeException.class, () -> messageService.getMessageRelationships(MESSAGE_ID));
  }
}
