package com.symphony.bdk.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MessageIdsFromStream;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageMetadataResponseParent;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageServiceTest {

  private static final String V4_STREAM_MESSAGE = "/agent/v4/stream/{sid}/message";
  private static final String V4_STREAM_MESSAGE_CREATE = "/agent/v4/stream/{sid}/message/create";
  private static final String V4_MESSAGE_IMPORT = "/agent/v4/message/import";
  private static final String V1_MESSAGE_SUPPRESSION = "/pod/v1/admin/messagesuppression/{id}/suppress";
  private static final String V1_MESSAGE_STATUS = "/pod/v1/message/{mid}/status";
  private static final String V1_ALLOWED_TYPES = "/pod/v1/files/allowedTypes";
  private static final String V1_STREAM_ATTACHMENTS = "/pod/v1/streams/{sid}/attachments";
  private static final String V1_MESSAGE_GET = "/agent/v1/message/{id}";
  private static final String V2_MESSAGE_IDS = "/pod/v2/admin/streams/{streamId}/messageIds";
  private static final String V1_MESSAGE_RECEIPTS = "/pod/v1/admin/messages/{messageId}/receipts";
  private static final String V1_MESSAGE_RELATIONSHIPS = "/pod/v1/admin/messages/{messageId}/metadata/relationships";

  private static final String STREAM_ID = "streamId";
  private static final String MESSAGE_ID = "messageId";
  private static final String MESSAGE = "message";
  private static final String TEMPLATE_NAME = "template";


  private MockApiClient mockApiClient;
  private MessageService messageService;
  private TemplateEngine templateEngine;
  private StreamsApi streamsApi;
  private AttachmentsApi attachmentsApi;

  @BeforeEach
  void setUp() {
    AuthSession authSession = mock(AuthSession.class);
    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");

    mockApiClient = new MockApiClient();
    ApiClient podClient = mockApiClient.getApiClient("/pod");
    ApiClient agentClient = mockApiClient.getApiClient("/agent");

    templateEngine = mock(TemplateEngine.class);
    streamsApi = spy(new StreamsApi(podClient));
    attachmentsApi = spy(new AttachmentsApi(agentClient));

    messageService = new MessageService(new MessagesApi(agentClient), new MessageApi(podClient),
        new MessageSuppressionApi(podClient), streamsApi, new PodApi(podClient),
        attachmentsApi, new DefaultApi(podClient), authSession, templateEngine, new RetryWithRecoveryBuilder());
  }

  @Test
  void testGetMessagesWithStreamObject() {
    MessageService service = spy(messageService);
    doReturn(Collections.emptyList()).when(service).getMessages(anyString(), any(), any(), any());

    final V4Stream v4Stream = new V4Stream().streamId(STREAM_ID);

    assertNotNull(service.getMessages(v4Stream, null, null, null));
    verify(service).getMessages(eq(STREAM_ID), isNull(), isNull(), isNull());
  }

  @Test
  void testGetMessages() throws IOException {
    final String streamId = "streamid";
    mockApiClient.onGet(V4_STREAM_MESSAGE.replace("{sid}", streamId),
        JsonHelper.readFromClasspath("/message/get_message_stream_id.json"));

    final List<V4Message> messages = messageService.getMessages(streamId, Instant.now(), null, null);

    assertEquals(2, messages.size());
    assertEquals(Arrays.asList("messageId1", "messageId2"),
        messages.stream().map(V4Message::getMessageId).collect(Collectors.toList()));
  }

  @Test
  void testGetMessagesStream() throws IOException {
    mockApiClient.onGet(V4_STREAM_MESSAGE.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/get_message_stream_id.json"));

    final Stream<V4Message> messages = messageService.getMessagesStream(STREAM_ID, Instant.now(), 2, 2);

    assertEquals(Arrays.asList("messageId1", "messageId2"),
        messages.map(V4Message::getMessageId).collect(Collectors.toList()));
  }

  @Test
  void testGetMessagesStreamWithStreamObject() {
    MessageService service = spy(messageService);
    doReturn(Stream.empty()).when(service).getMessagesStream(anyString(), any(), any(), any());

    final V4Stream v4Stream = new V4Stream().streamId(STREAM_ID);

    assertNotNull(service.getMessagesStream(v4Stream, null, null, null));
    verify(service).getMessagesStream(eq(STREAM_ID), isNull(), isNull(), isNull());
  }

  @Test
  void testSend() throws IOException {
    mockApiClient.onPost(V4_STREAM_MESSAGE_CREATE.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/send_message.json"));

    final V4Message sentMessage = messageService.send(STREAM_ID, MESSAGE);

    assertEquals(MESSAGE_ID, sentMessage.getMessageId());
    assertEquals("gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA", sentMessage.getStream().getStreamId());
  }

  @Test
  void testSendWithStreamObjectCallsSendWithStreamId() {
    MessageService service = spy(messageService);
    doReturn(new V4Message()).when(service).send(anyString(), anyString());

    final V4Stream v4Stream = new V4Stream().streamId(STREAM_ID);

    assertNotNull(service.send(v4Stream, MESSAGE));
    verify(service).send(eq(STREAM_ID), eq(MESSAGE));
  }

  @Test
  void testSendWithTemplateAndStreamObjectCallsSendWithCorrectStreamIdAndMessage() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(TEMPLATE_NAME));
    when(templateEngine.newBuiltInTemplate(eq(TEMPLATE_NAME))).thenReturn(parameters -> MESSAGE);

    MessageService service = spy(messageService);
    doReturn(new V4Message()).when(service).send(anyString(), anyString());

    final V4Stream v4Stream = new V4Stream().streamId(STREAM_ID);

    assertNotNull(service.send(v4Stream, TEMPLATE_NAME, Collections.emptyMap()));
    verify(service).send(eq(STREAM_ID), eq(MESSAGE));
  }

  @Test
  void testSendWithTemplateCallsSendWithCorrectMessage() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(TEMPLATE_NAME));
    when(templateEngine.newBuiltInTemplate(eq(TEMPLATE_NAME))).thenReturn(parameters -> MESSAGE);

    MessageService service = spy(messageService);
    doReturn(new V4Message()).when(service).send(anyString(), anyString());

    assertNotNull(service.send(STREAM_ID, TEMPLATE_NAME, Collections.emptyMap()));
    verify(service).send(eq(STREAM_ID), eq(MESSAGE));
  }

  @Test
  void testSendWithTemplateThrowingTemplateException() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(TEMPLATE_NAME));
    when(templateEngine.newBuiltInTemplate(eq(TEMPLATE_NAME))).thenThrow(new TemplateException("error"));

    assertThrows(TemplateException.class, () -> messageService.send(STREAM_ID, TEMPLATE_NAME, Collections.emptyMap()));
  }

  @Test
  void testGetAttachment() throws ApiException {
    final String attachmentId = "attachmentId";

    doReturn(new byte[0]).when(attachmentsApi).v1StreamSidAttachmentGet(any(), any(), any(), any(), any());

    assertNotNull(messageService.getAttachment(STREAM_ID, MESSAGE_ID, attachmentId));
    verify(attachmentsApi).v1StreamSidAttachmentGet(eq(STREAM_ID), eq(attachmentId), eq(MESSAGE_ID), anyString(), anyString());
  }

  @Test
  void testGetAttachmentThrowingApiException() throws ApiException {
    doThrow(new ApiException(500, "error")).when(attachmentsApi).v1StreamSidAttachmentGet(any(), any(), any(), any(), any());

    assertThrows(ApiRuntimeException.class, () -> messageService.getAttachment(STREAM_ID, MESSAGE_ID, "attachmentId"));
  }

  @Test
  void testImportMessage() throws IOException {
    mockApiClient.onPost(V4_MESSAGE_IMPORT, JsonHelper.readFromClasspath("/message/import_message.json"));

    final List<V4ImportResponse> v4ImportResponses = messageService.importMessages(Collections.emptyList());
    assertEquals(1, v4ImportResponses.size());

    final V4ImportResponse v4ImportResponse = v4ImportResponses.get(0);
    assertEquals("gfAq8THE-rtVKBAMP6aJPH___ouMxrzTbQ", v4ImportResponse.getMessageId());
    assertEquals("fooChat", v4ImportResponse.getOriginatingSystemId());
  }

  @Test
  void testSuppressMessage() throws IOException {
    mockApiClient.onPost(V1_MESSAGE_SUPPRESSION.replace("{id}", MESSAGE_ID),
        JsonHelper.readFromClasspath("/message/suppress_message.json"));

    final MessageSuppressionResponse messageSuppressionResponse = messageService.suppressMessage(MESSAGE_ID);
    assertEquals(true, messageSuppressionResponse.getSuppressed());
    assertEquals("FB2h29Egp6X_r3_K7cuuE3___ouM3iRdbQ", messageSuppressionResponse.getMessageId());
  }

  @Test
  void testGetMessageStatus() throws IOException {
    mockApiClient.onGet(V1_MESSAGE_STATUS.replace("{mid}", MESSAGE_ID),
        JsonHelper.readFromClasspath("/message/get_message_status.json"));

    final MessageStatus messageStatus = messageService.getMessageStatus(MESSAGE_ID);
    assertNotNull(messageStatus.getAuthor());
    assertEquals(1, messageStatus.getRead().size());
    assertEquals(1, messageStatus.getDelivered().size());
    assertEquals(0, messageStatus.getSent().size());
  }

  @Test
  void testGetAttachmentTypes() throws IOException {
    mockApiClient.onGet(V1_ALLOWED_TYPES,
        JsonHelper.readFromClasspath("/message/get_attachment_types.json"));

    assertEquals(Arrays.asList(".csv", ".gif"), messageService.getAttachmentTypes());
  }

  @Test
  void testGetMessage() throws IOException {
    mockApiClient.onGet(V1_MESSAGE_GET.replace("{id}", MESSAGE_ID),
        JsonHelper.readFromClasspath("/message/get_message.json"));

    final V4Message message = messageService.getMessage(MESSAGE_ID);
    assertEquals("E_U_0jnuzmQcBOr1CIGPqX___ouMNdY5bQ", message.getMessageId());
    assertEquals("gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA", message.getStream().getStreamId());
  }

  @Test
  void testListAttachments() throws IOException {
    mockApiClient.onGet(V1_STREAM_ATTACHMENTS.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/stream/list_attachments.json"));

    List<StreamAttachmentItem> attachments = messageService.listAttachments(STREAM_ID, null, null, null, AttachmentSort.ASC);

    assertEquals(attachments.size(), 2);
  }

  @Test
  void testListAttachmentWithSortDirAsc() throws ApiException {
    doReturn(Collections.emptyList()).when(streamsApi).v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any());

    assertNotNull(messageService.listAttachments(STREAM_ID, null, null, null, AttachmentSort.ASC));
    verify(streamsApi).v1StreamsSidAttachmentsGet(eq(STREAM_ID), any(), any(), any(), any(), eq("ASC"));
  }

  @Test
  void testListAttachmentWithSortDirDesc() throws ApiException {
    doReturn(Collections.emptyList()).when(streamsApi).v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any());

    assertNotNull(messageService.listAttachments(STREAM_ID, null, null, null, AttachmentSort.DESC));
    verify(streamsApi).v1StreamsSidAttachmentsGet(eq(STREAM_ID), any(), any(), any(), any(), eq("DESC"));
  }

  @Test
  void testListAttachmentWithSortDirNull() throws ApiException {
    doReturn(Collections.emptyList()).when(streamsApi).v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any());

    assertNotNull(messageService.listAttachments(STREAM_ID, null, null, null, null));
    verify(streamsApi).v1StreamsSidAttachmentsGet(eq(STREAM_ID), any(), any(), any(), any(), eq("ASC"));
  }

  @Test
  void testGetMessageIdsByTimestamp() throws IOException {
    mockApiClient.onGet(V2_MESSAGE_IDS.replace("{streamId}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/get_message_ids_by_timestamp.json"));

    final MessageIdsFromStream messageIdsByTimestamp =
        messageService.getMessageIdsByTimestamp(STREAM_ID, Instant.now(), Instant.now(), null, null);
    assertEquals(Arrays.asList("messageId1", "messageId2"), messageIdsByTimestamp.getData());
  }

  @Test
  void testGetMessageIdsByTimestampStream() throws IOException {
    mockApiClient.onGet(V2_MESSAGE_IDS.replace("{streamId}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/get_message_ids_by_timestamp.json"));

    final Stream<String> messageIdsByTimestamp =
        messageService.getMessageIdsByTimestampStream(STREAM_ID, Instant.now(), Instant.now(), null, null);
    assertEquals(Arrays.asList("messageId1", "messageId2"), messageIdsByTimestamp.collect(Collectors.toList()));
  }

  @Test
  void testListMessageReceipts() throws IOException {
    mockApiClient.onGet(V1_MESSAGE_RECEIPTS.replace("{messageId}", MESSAGE_ID),
        JsonHelper.readFromClasspath("/message/get_receipt_details.json"));

    final MessageReceiptDetailResponse messageReceiptDetailResponse = messageService.listMessageReceipts(MESSAGE_ID);
    assertEquals("bot-name", messageReceiptDetailResponse.getCreator().getName());
    assertEquals("gXFV8vN37dNqjojYS/y2wX///o2KxfmUdA==", messageReceiptDetailResponse.getStream().getId());
    assertEquals(2, messageReceiptDetailResponse.getDeliveryReceiptCount());
    assertEquals(2, messageReceiptDetailResponse.getMessageReceiptDetail().size());
  }

  @Test
  void testGetMessageRelationships() throws IOException {
    mockApiClient.onGet(V1_MESSAGE_RELATIONSHIPS.replace("{messageId}", MESSAGE_ID),
        JsonHelper.readFromClasspath("/message/get_message_relationships.json"));

    final MessageMetadataResponse messageRelationships = messageService.getMessageRelationships(MESSAGE_ID);
    assertEquals("kWpOSc30hEJtOoT89gLinH///ouMyhHwdA==", messageRelationships.getMessageId());
    assertEquals(MessageMetadataResponseParent.RelationshipTypeEnum.REPLY,
        messageRelationships.getParent().getRelationshipType());
    assertEquals("FB2h29Egp6X/r3/K7cuuE3///ouM3iRdbQ==", messageRelationships.getParent().getMessageId());
  }


  @Test
  void listAttachmentsTest() throws IOException {
    this.mockApiClient.onGet(V1_STREAM_ATTACHMENTS.replace("{sid}", "1234"), JsonHelper.readFromClasspath("/stream/list_attachments.json"));

    List<StreamAttachmentItem> attachments = messageService.listAttachments("1234", null, null, AttachmentSort.ASC);

    assertEquals(attachments.size(), 2);
    assertEquals(attachments.get(0).getMessageId(), "PYLHNm/1K6ppeOpj+FbQ");
    assertEquals(attachments.get(0).getIngestionDate(), 1548089933946L);
    assertEquals(attachments.get(1).getMessageId(), "KpjYuzMLR+JK1co7QBfukX///peOpZmdbQ==");
  }

  @Test
  void listAttachmentsTestFailed() {
    this.mockApiClient.onGet(400, V1_STREAM_ATTACHMENTS.replace("{sid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> messageService.listAttachments("1234", null, null, AttachmentSort.ASC));
  }

}
