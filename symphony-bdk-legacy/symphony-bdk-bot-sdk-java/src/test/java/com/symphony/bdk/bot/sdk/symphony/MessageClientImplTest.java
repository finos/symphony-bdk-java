package com.symphony.bdk.bot.sdk.symphony;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.symphony.bdk.bot.sdk.event.model.MessageAttachment;
import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.event.model.StreamDetails;
import com.symphony.bdk.bot.sdk.event.model.UserDetails;
import com.symphony.bdk.bot.sdk.lib.jsonmapper.JsonMapper;
import com.symphony.bdk.bot.sdk.lib.templating.TemplateService;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.StreamType;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

import clients.SymBotClient;
import clients.symphony.api.MessagesClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.Attachment;
import model.FileAttachment;
import model.ImageInfo;
import model.InboundMessage;
import model.OutboundMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageClientImplTest {

  private MessageClientImpl messageClientImpl;
  private MessageClientImpl messageClientImplMocked; // for the case of _sendMessage that throws an exception
  private MessagesClient messagesClient;
  private SymBotClient symBotClient;
  private TemplateService templateService;
  private JsonMapper jsonMapper;

  @Before
  public void initBot() {
    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");

    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(symConfig);

    this.templateService = Mockito.mock(TemplateService.class);
    this.jsonMapper = Mockito.mock(JsonMapper.class);

    this.messagesClient = Mockito.mock(MessagesClient.class);
    Mockito.when(this.symBotClient.getMessagesClient()).thenReturn(this.messagesClient);

    this.messageClientImpl = new MessageClientImpl(this.symBotClient, this.templateService, this.jsonMapper);

    this.messageClientImplMocked = Mockito.mock(MessageClientImpl.class);
  }

  @Test
  public void testSendMessageSymphonyMessage() throws SymphonyClientException {
    final String streamId = "streamId";
    this.testSendMessage(streamId, true);
    this.testSendMessage(streamId, false);
  }

  @Test
  public void testSendMessageJsonData() throws SymphonyClientException {
    final String streamId = "streamId";
    final String message = "This is a message";
    final String jsonData = "{\"message\": \"This is a message\"}";
    this.messageClientImpl.sendMessage(streamId, message, jsonData);
  }

  @Test
  public void testSendMessageJsonDataWithAttachments() throws SymphonyClientException {
    final String streamId = "streamId";
    final String message = "This is a message";
    final String jsonData = "{\"message\": \"This is a message\"}";
    final List<MessageAttachmentFile> messageAttachmentFiles = this.initMessageAttachmentFiles();
    this.messageClientImpl.sendMessage(streamId, message, jsonData, messageAttachmentFiles);
  }

  @Test(expected = SymphonyClientException.class)
  public void testSendMessageJsonDataWithAttachmentsWithException() throws SymphonyClientException {
    final String streamId = "streamId";
    final String message = "This is a message";
    final String jsonData = "{\"message\": \"This is a message\"}";
    final List<MessageAttachmentFile> messageAttachmentFiles = this.initMessageAttachmentFiles();

    Mockito.when(this.symBotClient.getMessagesClient().sendMessage(anyString(), any(OutboundMessage.class)))
        .thenThrow(SymClientException.class);

    this.messageClientImpl.sendMessage(streamId, message, jsonData, messageAttachmentFiles);
  }

  @Test
  public void testDownloadMessageAttachments() throws SymphonyClientException {
    final MessageEvent messageEvent = this.initMessageEvent();
    final byte[] fileContent1 = new byte[] {(byte) 6789, (byte) 47, (byte) 567};
    final byte[] fileContent2 = new byte[] {(byte) 990, (byte) 467, (byte) 5654};
    final byte[] fileContent3 = new byte[] {(byte) 687, (byte) 479, (byte) 574};
    final List<FileAttachment> fileAttachmentList = this.initFileAttachmentsList(fileContent1, fileContent2, fileContent3);

    Mockito.when(this.messagesClient.getMessageAttachments(any(InboundMessage.class))).thenReturn(fileAttachmentList);

    final List<MessageAttachmentFile> messageAttachmentFilesResult =
        this.messageClientImpl.downloadMessageAttachments(messageEvent);
    assertNotNull(messageAttachmentFilesResult);
    assertEquals(3, messageAttachmentFilesResult.size());

    this.verifyMessageAttachmentFile(fileContent1, messageAttachmentFilesResult, "test1", 0, 5678L);
    this.verifyMessageAttachmentFile(fileContent2, messageAttachmentFilesResult, "test2", 1, 8599L);
    this.verifyMessageAttachmentFile(fileContent3, messageAttachmentFilesResult, "test3", 2, 890L);
  }

  @Test(expected = SymphonyClientException.class)
  public void testDownloadMessageAttachmentsWithException() throws SymphonyClientException {
    final MessageEvent messageEvent = this.initMessageEvent();

    Mockito.when(this.symBotClient.getMessagesClient()
        .getMessageAttachments(any(InboundMessage.class))
        .stream()
        .map(MessageAttachmentFile::new)
        .collect(Collectors.toList())).thenThrow(SymClientException.class);

    this.messageClientImpl.downloadMessageAttachments(messageEvent);
  }

  @Test
  public void test_sendMessage() {
    final String streamId = "streamId";
    this.test_sendMessage(streamId, true);
    this.test_sendMessage(streamId, false);
  }

  @Test(expected = SymClientException.class)
  public void test_sendMessageWithException() {
    final String streamId = "streamId";
    this.testSendMessageWithException(streamId, true);
    this.testSendMessageWithException(streamId, false);
  }

  private void testSendMessageWithException(final String streamId, final boolean enrichedMessage) {
    final SymphonyMessage symphonyMessage = this.initSymphonyMessage(enrichedMessage);

    Mockito.doThrow(SymClientException.class)
        .when(this.messageClientImplMocked)
        ._sendMessage(anyString(), any(SymphonyMessage.class));

    this.messageClientImplMocked._sendMessage(streamId, symphonyMessage);
  }

  private void test_sendMessage(final String streamId, final boolean enrichedMessage) {
    final SymphonyMessage symphonyMessage = this.initSymphonyMessage(enrichedMessage);
    this.messageClientImpl._sendMessage(streamId, symphonyMessage);
  }

  private void verifyMessageAttachmentFile(final byte[] fileContent,
      final List<MessageAttachmentFile> messageAttachmentFileList, final String fileName, final int rang,
      final long size) {
    assertEquals(fileName, messageAttachmentFileList.get(rang).getFileName());
    assertEquals(fileContent, messageAttachmentFileList.get(rang).getFileContent());
    assertEquals(size, messageAttachmentFileList.get(rang).getSize().longValue());
  }

  private List<FileAttachment> initFileAttachmentsList(byte[] fileContent1, byte[] fileContent2, byte[] fileContent3) {
    final FileAttachment fileAttachment1 = this.initFileAttachment(fileContent1, "test1", 5678L);
    final FileAttachment fileAttachment2 = this.initFileAttachment(fileContent2, "test2", 8599L);
    final FileAttachment fileAttachment3 = this.initFileAttachment(fileContent3, "test3", 890L);
    final List<FileAttachment> fileAttachmentList = Arrays.asList(fileAttachment1, fileAttachment2, fileAttachment3);
    return fileAttachmentList;
  }

  private FileAttachment initFileAttachment(byte[] fileContent, final String fileName, final Long size) {
    final FileAttachment fileAttachment = new FileAttachment();
    fileAttachment.setFileContent(fileContent);
    fileAttachment.setFileName(fileName);
    fileAttachment.setSize(size);
    return fileAttachment;
  }

  private MessageEvent initMessageEvent() {
    final MessageEvent messageEvent = new MessageEvent();
    messageEvent.setStreamId("strId");
    messageEvent.setUserId(1L);
    messageEvent.setMessageId("msgId");
    messageEvent.setTimestamp(7857L);
    messageEvent.setMessage("message");
    messageEvent.setRawMessage("rawMessage");
    messageEvent.setData("data");
    messageEvent.setAttachments(this.initMessageAttachments());
    messageEvent.setExternalRecipients(true);
    messageEvent.setDiagnostic("diagnostic");
    messageEvent.setUserAgent("userAgent");
    messageEvent.setOriginalFormat("format");
    messageEvent.setUser(this.initUserDetails(1L));
    messageEvent.setStream(this.initStreamDetails("ROOM"));
    return messageEvent;
  }

  private StreamDetails initStreamDetails(final String streamType) {
    final StreamType sType = this.initStreamType(streamType);
    final List<UserDetails> members = this.initUserDetailsList();

    final StreamDetails streamDetails = new StreamDetails();
    streamDetails.setStreamType(sType);
    streamDetails.setRoomName("Room");
    streamDetails.setMembers(members);
    streamDetails.setExternal(true);
    streamDetails.setCrossPod(true);

    return streamDetails;
  }

  private List<UserDetails> initUserDetailsList() {
    final UserDetails userDetails1 = this.initUserDetails(1L);
    final UserDetails userDetails2 = this.initUserDetails(2L);
    final UserDetails userDetails3 = this.initUserDetails(3L);
    final List<UserDetails> userDetailsList = Arrays.asList(userDetails1, userDetails2, userDetails3);
    return userDetailsList;
  }

  private StreamType initStreamType(final String streamType) {
    return StreamType.value(streamType);
  }

  private UserDetails initUserDetails(final Long id) {
    final UserDetails userDetails = new UserDetails();
    userDetails.setUserId(id);
    userDetails.setEmail("test@symphony.com");
    userDetails.setFirstName("first");
    userDetails.setLastName("last");
    userDetails.setDisplayName("first last");
    userDetails.setUsername("username");
    return userDetails;
  }

  private List<MessageAttachment> initMessageAttachments() {
    final Attachment attachment1 = this.initAttachment("1");
    final Attachment attachment2 = this.initAttachment("2");
    final Attachment attachment3 = this.initAttachment("3");

    final MessageAttachment messageAttachment1 = new MessageAttachment(attachment1);
    final MessageAttachment messageAttachment2 = new MessageAttachment(attachment2);
    final MessageAttachment messageAttachment3 = new MessageAttachment(attachment3);

    final List<MessageAttachment> messageAttachmentsList =
        Arrays.asList(messageAttachment1, messageAttachment2, messageAttachment3);
    return messageAttachmentsList;
  }

  private Attachment initAttachment(String id) {
    final Attachment attachment = new Attachment();
    attachment.setId(id);
    attachment.setName(id);
    attachment.setSize(8L);

    final ImageInfo imageInfo = this.initImageInfo(id);

    final ImageInfo imageInfo1 = this.initImageInfo(id + "1");
    final ImageInfo imageInfo2 = this.initImageInfo(id + "2");
    final ImageInfo imageInfo3 = this.initImageInfo(id + "3");

    final List<ImageInfo> imageInfos = Arrays.asList(imageInfo1, imageInfo2, imageInfo3);

    attachment.setImage(imageInfo);
    attachment.setImages(imageInfos);

    return attachment;
  }

  private ImageInfo initImageInfo(String id) {
    final ImageInfo imageInfo = new ImageInfo();
    imageInfo.setId(id);
    imageInfo.setDimension("small");
    return imageInfo;
  }

  private void testSendMessage(final String streamId, final boolean enrichedMessage) throws SymphonyClientException {
    final SymphonyMessage symphonyMessage = this.initSymphonyMessage(enrichedMessage);
    this.messageClientImpl.sendMessage(streamId, symphonyMessage);
  }

  private SymphonyMessage initSymphonyMessage(final boolean enrichedMessage) {
    final String entityName = enrichedMessage ? "entityName" : null;
    final SymphonyMessage symphonyMessage = new SymphonyMessage("message");
    symphonyMessage.setEnrichedTemplateMessage("template message", new Object(), entityName, new Object(),
        "testVersion");
    symphonyMessage.setTemplateFile("template file", new Object());

    final List<MessageAttachmentFile> messageAttachmentFiles = this.initMessageAttachmentFiles();
    symphonyMessage.setAttachments(messageAttachmentFiles);

    return symphonyMessage;
  }

  private List<MessageAttachmentFile> initMessageAttachmentFiles() {
    final byte[] bytes1 = {(byte) 6789, (byte) 47, (byte) 567};
    final byte[] bytes2 = {(byte) 990, (byte) 467, (byte) 5654};
    final byte[] bytes3 = {(byte) 347, (byte) 456, (byte) 56};
    final List<String> namesArray = Arrays.asList("name1", "name2", "name3");
    final List<Long> sizes = Arrays.asList(5678L, 8599L, 9567L);

    final List<MessageAttachmentFile> messageAttachmentFiles = Arrays.asList(
        this.initMessageAttachmentFile(bytes1, namesArray.get(0), sizes.get(0)),
        this.initMessageAttachmentFile(bytes2, namesArray.get(1), sizes.get(1)),
        this.initMessageAttachmentFile(bytes3, namesArray.get(2), sizes.get(2)));

    return messageAttachmentFiles;
  }

  private MessageAttachmentFile initMessageAttachmentFile(final byte[] bytes, final String name, final Long size) {
    final FileAttachment fileAttachment = new FileAttachment();
    this.initFileAttachment(fileAttachment, bytes, name, size);
    final MessageAttachmentFile messageAttachmentFile = new MessageAttachmentFile(fileAttachment);
    return messageAttachmentFile;
  }

  private void initFileAttachment(final FileAttachment fileAttachment, final byte[] bytes, final String name,
      final Long size) {
    fileAttachment.setFileContent(bytes);
    fileAttachment.setFileName(name);
    fileAttachment.setSize(size);
  }
}
