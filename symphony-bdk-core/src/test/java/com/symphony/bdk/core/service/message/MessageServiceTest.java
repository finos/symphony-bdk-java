package com.symphony.bdk.core.service.message;

import static com.symphony.bdk.core.util.IdUtil.fromUrlSafeId;
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
import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.core.test.BdkMockServer;
import com.symphony.bdk.core.test.BdkMockServerExtension;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageMetadataResponseParent;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageBlastResponse;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBodyPart;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MessageServiceTest {

  private static final String V4_STREAM_MESSAGE = "/agent/v4/stream/{sid}/message";
  private static final String V4_STREAM_MESSAGE_CREATE = "/agent/v4/stream/{sid}/message/create";
  private static final String V4_MESSAGE_IMPORT = "/agent/v4/message/import";
  private static final String V4_BLAST_MESSAGE = "/agent/v4/message/blast";
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
  private static final String MESSAGE = "<messageML>message</messageML>";
  private static final String TOKEN = "1234";

  private MockApiClient mockApiClient;
  private MessageService messageService;
  private StreamsApi streamsApi;
  private AttachmentsApi attachmentsApi;
  private TemplateEngine templateEngine;
  private AuthSession authSession;
  private MessagesApi messagesApi;
  private MessageApi messageApi;
  private MessageSuppressionApi messageSuppressionApi;
  private PodApi podApi;
  private DefaultApi defaultApi;

  @BeforeEach
  void setUp() {
    authSession = mock(AuthSession.class);
    when(authSession.getSessionToken()).thenReturn(TOKEN);
    when(authSession.getKeyManagerToken()).thenReturn(TOKEN);

    mockApiClient = new MockApiClient();
    ApiClient podClient = mockApiClient.getApiClient("/pod");
    ApiClient agentClient = mockApiClient.getApiClient("/agent");

    templateEngine = mock(TemplateEngine.class);
    streamsApi = spy(new StreamsApi(podClient));
    attachmentsApi = spy(new AttachmentsApi(agentClient));
    messagesApi = new MessagesApi(agentClient);
    messageApi = new MessageApi(podClient);
    messageSuppressionApi = new MessageSuppressionApi(podClient);
    podApi = new PodApi(podClient);
    defaultApi = new DefaultApi(podClient);

    messageService = new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi,
        attachmentsApi, defaultApi, authSession, templateEngine, new RetryWithRecoveryBuilder<>());
  }

  @Test
  void nonOboEndpointShouldThrowExceptionInOboMode() {
    messageService = new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi,
        attachmentsApi, defaultApi, templateEngine, new RetryWithRecoveryBuilder<>());

    assertThrows(IllegalStateException.class, () -> messageService.getMessage("message.id"));
  }

  @Test
  void testSendMessageObo() throws IOException {
    mockApiClient.onPost(V4_STREAM_MESSAGE_CREATE.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/send_message.json"));

    messageService = new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi,
        attachmentsApi, defaultApi, templateEngine, new RetryWithRecoveryBuilder<>());
    final V4Message sentMessage = messageService.obo(authSession).send(STREAM_ID, MESSAGE);

    assertEquals(MESSAGE_ID, sentMessage.getMessageId());
  }

  @Test
  void testGetMessagesWithStreamObject() {
    MessageService service = spy(messageService);
    doReturn(Collections.emptyList()).when(service).listMessages(anyString(), any());

    final V4Stream v4Stream = new V4Stream().streamId(STREAM_ID);
    Instant now = Instant.now();
    assertNotNull(service.listMessages(v4Stream, now));
    verify(service).listMessages(eq(STREAM_ID), eq(now));
  }

  @Test
  void testGetPaginationMessagesWithStreamObject() {
    MessageService service = spy(messageService);
    doReturn(Collections.emptyList()).when(service).listMessages(anyString(), any(), any());

    final V4Stream v4Stream = new V4Stream().streamId(STREAM_ID);
    Instant now = Instant.now();
    PaginationAttribute pagination = new PaginationAttribute(2, 2);
    assertNotNull(service.listMessages(v4Stream, now, pagination));
    verify(service).listMessages(eq(STREAM_ID), eq(now), eq(pagination));
  }

  @Test
  void testGetMessages() throws IOException {
    final String streamId = "streamid";
    mockApiClient.onGet(V4_STREAM_MESSAGE.replace("{sid}", streamId),
        JsonHelper.readFromClasspath("/message/get_message_stream_id.json"));

    final List<V4Message> messages = messageService.listMessages(streamId, Instant.now());

    assertEquals(2, messages.size());
    assertEquals(Arrays.asList("messageId1", "messageId2"),
        messages.stream().map(V4Message::getMessageId).collect(Collectors.toList()));
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
  void testSendPassingMessageInstanceToStreamId(@TempDir Path tmpDir) throws IOException {
    Path tempFilePath = tmpDir.resolve("tempFile");
    IOUtils.write("test", new FileOutputStream(tempFilePath.toFile()), StandardCharsets.UTF_8);
    mockApiClient.onPost(V4_STREAM_MESSAGE_CREATE.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/send_message.json"));

    InputStream inputStream = new FileInputStream(tempFilePath.toString());
    Message message = Message.builder()
        .content(MESSAGE)
        .addAttachment(inputStream, "test.png")
        .build();

    final V4Message sentMessage = messageService.send(STREAM_ID, message);

    assertEquals(MESSAGE_ID, sentMessage.getMessageId());
    assertEquals("gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA", sentMessage.getStream().getStreamId());
  }

  @Test
  void testSendPassingMessageInstanceToStream(@TempDir Path tmpDir) throws IOException {
    Path tempFilePath = tmpDir.resolve("tempFile");
    IOUtils.write("test", new FileOutputStream(tempFilePath.toFile()), StandardCharsets.UTF_8);
    mockApiClient.onPost(V4_STREAM_MESSAGE_CREATE.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/send_message.json"));

    InputStream inputStream = new FileInputStream(tempFilePath.toString());
    Message message = Message.builder()
        .content(MESSAGE)
        .addAttachment(inputStream, "test.png")
        .build();

    final V4Message sentMessage = messageService.send(new V4Stream().streamId(STREAM_ID), message);

    assertEquals(MESSAGE_ID, sentMessage.getMessageId());
    assertEquals("gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA", sentMessage.getStream().getStreamId());
  }

  @Test
  void testSendPassingMessageInstanceToStreamWrongAttachmentName(@TempDir Path tmpDir) throws IOException {
    Path tempFilePath = tmpDir.resolve("tempFile");
    IOUtils.write("test", new FileOutputStream(tempFilePath.toFile()), StandardCharsets.UTF_8);
    mockApiClient.onPost(V4_STREAM_MESSAGE_CREATE.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/message/send_message.json"));

    InputStream inputStream = new FileInputStream(tempFilePath.toString());

    assertThrows(MessageCreationException.class,
        () -> {
          final Message message = Message.builder()
              .content(MESSAGE)
              .addAttachment(inputStream, "wrong-name")
              .build();
          messageService.send(new V4Stream().streamId(STREAM_ID), message);
        });
  }

  @Test
  void testMessageCreationFailed(@TempDir Path tmpDir) throws IOException {
    Path tempFilePath = tmpDir.resolve("tempFile");
    IOUtils.write("test", new FileOutputStream(tempFilePath.toFile()), StandardCharsets.UTF_8);

    InputStream inputStream = new FileInputStream(tempFilePath.toString());
    assertThrows(MessageCreationException.class,
        () -> Message.builder()
            .content(MESSAGE)
            .addAttachment(inputStream, "test.png")
            .data(new MockObject("wrong object")).build());
  }

  @Test
  void testMessageCreationFailsIfPreviewsNotAsManyAsAttachments() {
    final InputStream firstAttachment = IOUtils.toInputStream("First attachment", StandardCharsets.UTF_8);
    final InputStream secondAttachment = IOUtils.toInputStream("Second Attachment", StandardCharsets.UTF_8);
    final InputStream preview = IOUtils.toInputStream("Preview file", StandardCharsets.UTF_8);

    assertThrows(MessageCreationException.class,
        () -> Message.builder()
            .content(MESSAGE)
            .addAttachment(firstAttachment, "test1.txt")
            .addAttachment(secondAttachment, preview, "test2.txt")
            .data(new MockObject("wrong object")).build());
  }

  @Test
  void testMessageCreationSuccess() {
    final InputStream inputStream = IOUtils.toInputStream("test string", StandardCharsets.UTF_8);
    final Message message =
        Message.builder().content(MESSAGE).addAttachment(inputStream, "test.doc").build();

    assertEquals("2.0", message.getVersion());
    assertEquals(MESSAGE, message.getContent());
    assertEquals("test.doc", message.getAttachments().get(0).getFilename());
  }

  @Test
  void testGetAttachment() throws ApiException {
    final String attachmentId = "attachmentId";

    doReturn(new byte[0]).when(attachmentsApi).v1StreamSidAttachmentGet(any(), any(), any(), any(), any());

    assertNotNull(messageService.getAttachment(STREAM_ID, MESSAGE_ID, attachmentId));
    verify(attachmentsApi).v1StreamSidAttachmentGet(eq(STREAM_ID), eq(attachmentId), eq(MESSAGE_ID), anyString(),
        anyString());
  }

  @Test
  void testGetAttachmentThrowingApiException() throws ApiException {
    doThrow(new ApiException(400, "error")).when(attachmentsApi)
        .v1StreamSidAttachmentGet(any(), any(), any(), any(), any());

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
  void testGetMessage_base64() throws IOException {
    mockApiClient.onGet(V1_MESSAGE_GET.replace("{id}", "E_U_0jnuzmQcBOr1CIGPqX___ouMNdY5bQ"),
        JsonHelper.readFromClasspath("/message/get_message.json"));

    final V4Message message = messageService.getMessage(fromUrlSafeId("E_U_0jnuzmQcBOr1CIGPqX___ouMNdY5bQ"));

    assertEquals("E_U_0jnuzmQcBOr1CIGPqX___ouMNdY5bQ", message.getMessageId());
  }

  @Test
  void testListAttachments() throws IOException {
    mockApiClient.onGet(V1_STREAM_ATTACHMENTS.replace("{sid}", STREAM_ID),
        JsonHelper.readFromClasspath("/stream/list_attachments.json"));

    List<StreamAttachmentItem> attachments =
        messageService.listAttachments(STREAM_ID, null, null, null, AttachmentSort.ASC);

    assertEquals(2, attachments.size());
  }

  @Test
  void testListAttachmentWithSortDirAsc() throws ApiException {
    doReturn(Collections.emptyList()).when(streamsApi)
        .v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any());

    assertNotNull(messageService.listAttachments(STREAM_ID, null, null, null, AttachmentSort.ASC));
    verify(streamsApi).v1StreamsSidAttachmentsGet(eq(STREAM_ID), any(), any(), any(), any(), eq("ASC"));
  }

  @Test
  void testListAttachmentWithSortDirDesc() throws ApiException {
    doReturn(Collections.emptyList()).when(streamsApi)
        .v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any());

    assertNotNull(messageService.listAttachments(STREAM_ID, null, null, null, AttachmentSort.DESC));
    verify(streamsApi).v1StreamsSidAttachmentsGet(eq(STREAM_ID), any(), any(), any(), any(), eq("DESC"));
  }

  @Test
  void testListAttachmentWithSortDirNull() throws ApiException {
    doReturn(Collections.emptyList()).when(streamsApi)
        .v1StreamsSidAttachmentsGet(any(), any(), any(), any(), any(), any());

    assertNotNull(messageService.listAttachments(STREAM_ID, null, null, null, null));
    verify(streamsApi).v1StreamsSidAttachmentsGet(eq(STREAM_ID), any(), any(), any(), any(), eq("ASC"));
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
  @ExtendWith(BdkMockServerExtension.class)
  void testDoSend(final BdkMockServer mockServer) throws IOException, ApiException {
    final Message message = Message.builder()
        .content("<MessageML>Hello world</MessageML>")
        .build();

    assertInvokeApiCalledWithCorrectParams(mockServer, message, Collections.emptyList(), Collections.emptyList());
  }

  @Test
  @ExtendWith(BdkMockServerExtension.class)
  void testDoSendWithAttachment(final BdkMockServer mockServer) throws IOException, ApiException {
    final Message message = Message.builder()
        .content("<MessageML>Hello world</MessageML>")
        .addAttachment(IOUtils.toInputStream("Attached file", StandardCharsets.UTF_8), "file.txt")
        .build();

    assertInvokeApiCalledWithCorrectParams(mockServer, message,
        Collections.singletonList("file.txt"), Collections.emptyList());

  }

  @Test
  @ExtendWith(BdkMockServerExtension.class)
  void testDoSendWithAttachmentAndPreview(final BdkMockServer mockServer) throws IOException, ApiException {
    final Message message = Message.builder()
        .content("<MessageML>Hello world</MessageML>")
        .addAttachment(IOUtils.toInputStream("Attached file", StandardCharsets.UTF_8),
            IOUtils.toInputStream("Preview file", StandardCharsets.UTF_8), "file.txt")
        .build();

    assertInvokeApiCalledWithCorrectParams(mockServer, message,
        Collections.singletonList("file.txt"), Collections.singletonList("preview-file.txt"));
  }

  @Test
  @ExtendWith(BdkMockServerExtension.class)
  void testDoSendBlast(final BdkMockServer mockServer) throws IOException, ApiException {
    final Message message = Message.builder()
        .content("<MessageML>Hello world</MessageML>")
        .addAttachment(IOUtils.toInputStream("Attached file", StandardCharsets.UTF_8),
            IOUtils.toInputStream("Preview file", StandardCharsets.UTF_8), "file.txt")
        .build();

    ApiClient agentClient = spy(mockServer.newApiClient("/agent"));
    messageService = new MessageService(new MessagesApi(agentClient), null, null, null, null, null, null, authSession,
        templateEngine, new RetryWithRecoveryBuilder<>());

    final String response = JsonHelper.readFromClasspath("/message/blast_message.json");
    mockServer.onPost(V4_BLAST_MESSAGE, res -> res.withBody(response));

    final V4MessageBlastResponse blastResponse =
        messageService.send(Arrays.asList("sid1", "sid2"), message);

    //assert on response body
    assertNotNull(blastResponse);
    assertEquals(2, blastResponse.getMessages().size());

    //assert on arguments passed to invokeApi
    Map<String, String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("sessionToken", TOKEN);
    expectedHeaders.put("keyManagerToken", TOKEN);

    final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

    verify(agentClient)
        .invokeAPI(
            eq("/v4/message/blast"),
            eq("POST"),
            eq(Collections.emptyList()),
            isNull(),
            eq(expectedHeaders),
            eq(Collections.emptyMap()),
            captor.capture(),
            eq("application/json"),
            eq("multipart/form-data"),
            eq(new String[0]),
            any());

    //assert on form fields not related to attachments
    final Map<String, Object> value = captor.getValue();
    assertEquals(message.getContent(), value.get("message"));
    assertEquals(message.getData(), value.get("data"));
    assertEquals(message.getVersion(), value.get("version"));
    assertEquals("sid1,sid2", value.get("sids"));

    //assert on attachments and previews
    List<String> attachmentFileNames = Stream.of((ApiClientBodyPart[]) value.get("attachment"))
        .map(ApiClientBodyPart::getFilename)
        .collect(Collectors.toList());

    List<String> previewFileNames = Stream.of((ApiClientBodyPart[]) value.get("preview"))
        .map(ApiClientBodyPart::getFilename)
        .collect(Collectors.toList());

    assertEquals(Collections.singletonList("file.txt"), attachmentFileNames);
    assertEquals(Collections.singletonList("preview-file.txt"), previewFileNames);
  }

  private void assertInvokeApiCalledWithCorrectParams(final BdkMockServer mockServer, Message message,
      List<String> expectedAttachmentFilenames, List<String> expectedPreviewFilenames)
      throws IOException, ApiException {
    ApiClient agentClient = spy(mockServer.newApiClient("/agent"));
    messageService = new MessageService(new MessagesApi(agentClient), null, null, null, null, null, null, authSession,
        templateEngine, new RetryWithRecoveryBuilder<>());

    final String response = JsonHelper.readFromClasspath("/message/send_message.json");
    mockServer.onPost("/agent/v4/stream/streamid/message/create", res -> res.withBody(response));

    final V4Message sentMessage = messageService.send("streamId", message);

    //assert on response body
    assertNotNull(sentMessage);
    assertEquals(MESSAGE_ID, sentMessage.getMessageId());
    assertEquals("gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA", sentMessage.getStream().getStreamId());

    //assert on arguments passed to invokeApi
    Map<String, String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("sessionToken", TOKEN);
    expectedHeaders.put("keyManagerToken", TOKEN);

    final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

    verify(agentClient)
        .invokeAPI(
            eq("/v4/stream/streamId/message/create"),
            eq("POST"),
            eq(Collections.emptyList()),
            isNull(),
            eq(expectedHeaders),
            eq(Collections.emptyMap()),
            captor.capture(),
            eq("application/json"),
            eq("multipart/form-data"),
            eq(new String[0]),
            any());

    //assert on form fields not related to attachments
    final Map<String, Object> value = captor.getValue();
    assertEquals(message.getContent(), value.get("message"));
    assertEquals(message.getData(), value.get("data"));
    assertEquals(message.getVersion(), value.get("version"));

    //assert on attachments and previews
    List<String> attachmentFileNames = Stream.of((ApiClientBodyPart[]) value.get("attachment"))
        .map(ApiClientBodyPart::getFilename)
        .collect(Collectors.toList());

    List<String> previewFileNames = Stream.of((ApiClientBodyPart[]) value.get("preview"))
        .map(ApiClientBodyPart::getFilename)
        .collect(Collectors.toList());

    assertEquals(expectedAttachmentFilenames, attachmentFileNames);
    assertEquals(expectedPreviewFilenames, previewFileNames);
  }

  private static class MockObject {
    private String content;

    MockObject(String content) {
      this.content = content;
    }

  }

}
