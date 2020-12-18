package com.symphony.bdk.bot.sdk.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

import model.FileAttachment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MultiResponseComposerImplTest {

  private SymphonyMessage symphonyMessage;
  private MultiResponseComposerImpl multiResponseComposerImpl;
  private MultiResponseComposerImpl multiResponseComposerImplMock; // to test hasContent()

  @Before
  public void init() {
    this.symphonyMessage = this.initSymphonyMessage();
    this.multiResponseComposerImpl = new MultiResponseComposerImpl();
    assertTrue(this.multiResponseComposerImpl.isComplete());

    this.multiResponseComposerImplMock = Mockito.mock(MultiResponseComposerImpl.class);
  }

  @Test
  public void testCompose() {
    final ComposerMessageDefinition composerMessageDefinition = this.multiResponseComposerImpl.compose();
    assertFalse(this.multiResponseComposerImpl.isComplete());
    assertEquals(composerMessageDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithMessage() {
    final String message = "message1";

    final ComposerAttachmentOrStreamDefinition composerAttachmentOrStreamDefinition =
        this.multiResponseComposerImpl.withMessage(message);
    assertEquals(composerAttachmentOrStreamDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithEnrichedMessage() {
    final String message = "message2";
    final String entityName = "entityName";
    final Object entity = new Object();
    final String version = "version";

    final ComposerAttachmentOrStreamDefinition composerAttachmentOrStreamDefinition =
        this.multiResponseComposerImpl.withEnrichedMessage(message, entityName, entity, version);
    assertEquals(composerAttachmentOrStreamDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithTemplateMessage() {
    final String templateMessage = "template message";
    final Object templateData = new Object();

    final ComposerAttachmentOrStreamDefinition composerAttachmentOrStreamDefinition =
        this.multiResponseComposerImpl.withTemplateMessage(templateMessage, templateData);
    assertEquals(composerAttachmentOrStreamDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithEnrichedTemplateMessage() {
    final String templateMessage = "template message 1";
    final Object templateData = new Object();
    final String entityName = "entityName 1";
    final Object entity = new Object();
    final String version = "version1";

    final ComposerAttachmentOrStreamDefinition composerAttachmentOrStreamDefinition =
        this.multiResponseComposerImpl.withEnrichedTemplateMessage(templateMessage, templateData, entityName, entity,
            version);
    assertEquals(composerAttachmentOrStreamDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithTemplateFile() {
    final String templateFile = "template file";
    final Object templateData = new Object();

    final ComposerAttachmentOrStreamDefinition composerAttachmentOrStreamDefinition =
        this.multiResponseComposerImpl.withTemplateFile(templateFile, templateData);
    assertEquals(composerAttachmentOrStreamDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithEnrichedTemplateFile() {
    final String templateFile = "template file 1";
    final Object templateData = new Object();
    final String entityName = "entityName 2";
    final Object entity = new Object();
    final String version = "version2";

    final ComposerAttachmentOrStreamDefinition composerAttachmentOrStreamDefinition =
        this.multiResponseComposerImpl.withEnrichedTemplateFile(templateFile, templateData, entityName, entity,
            version);
    assertEquals(composerAttachmentOrStreamDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testToStreamsStrings() {
    final String streamId1 = "streamId1";
    final String streamId2 = "streamId2";
    final String streamId3 = "streamId3";

    this.multiResponseComposerImpl.compose(); // to initialize composedResponse

    final ComposerMessageDefinition composerMessageDefinition =
        this.multiResponseComposerImpl.toStreams(streamId1, streamId2, streamId3);
    assertEquals(composerMessageDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testToStreamsCollectionStrings() {
    final String streamId1 = "streamId1";
    final String streamId2 = "streamId2";
    final String streamId3 = "streamId3";
    final Collection<String> streamIds = Arrays.asList(streamId1, streamId2, streamId3);

    this.multiResponseComposerImpl.compose(); // to initialize composedResponse

    final ComposerMessageDefinition composerMessageDefinition = this.multiResponseComposerImpl.toStreams(streamIds);
    assertEquals(composerMessageDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithAttachments() {
    final List<MessageAttachmentFile> messageAttachmentFiles = this.symphonyMessage.getAttachments();
    final MessageAttachmentFile messageAttachmentFile1 = messageAttachmentFiles.get(0);
    final MessageAttachmentFile messageAttachmentFile2 = messageAttachmentFiles.get(1);
    final MessageAttachmentFile messageAttachmentFile3 = messageAttachmentFiles.get(2);

    this.multiResponseComposerImpl.withMessage("Message"); // to initialize message

    final ComposerStreamsDefinition composerStreamsDefinition =
        this.multiResponseComposerImpl.withAttachments(messageAttachmentFile1, messageAttachmentFile2,
            messageAttachmentFile3);
    assertEquals(composerStreamsDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testWithAttachmentsCollection() {
    final Collection<MessageAttachmentFile> messageAttachmentFiles = this.symphonyMessage.getAttachments();

    this.multiResponseComposerImpl.withMessage("Message"); // to initialize message

    final ComposerStreamsDefinition composerStreamsDefinition =
        this.multiResponseComposerImpl.withAttachments(messageAttachmentFiles);
    assertEquals(composerStreamsDefinition, this.multiResponseComposerImpl);
  }

  @Test
  public void testComplete() {
    this.multiResponseComposerImpl.compose(); // to set complete to false at the beginning
    assertFalse(this.multiResponseComposerImpl.isComplete());

    this.multiResponseComposerImpl.complete();

    assertTrue(this.multiResponseComposerImpl.isComplete());
  }

  @Test
  public void testHasContentWithNullComposedResponse() {
    Mockito.when(this.multiResponseComposerImplMock.getComposedResponse()).thenReturn(null);
    assertFalse(this.multiResponseComposerImplMock.hasContent());
  }

  @Test
  public void testHasContentWithEmptyComposedResponse() {
    this.multiResponseComposerImpl.compose(); // to set complete to empty list at the beginning
    assertFalse(this.multiResponseComposerImpl.hasContent());
  }

  @Test
  public void testHasContentWithFullComposedResponse() {
    final String streamId1 = "streamId1";
    final String streamId2 = "streamId2";
    final String streamId3 = "streamId3";

    this.multiResponseComposerImpl.compose(); // to initialize composedResponse

    this.multiResponseComposerImpl.toStreams(streamId1, streamId2, streamId3);

    assertTrue(this.multiResponseComposerImpl.hasContent());
  }

  private SymphonyMessage initSymphonyMessage() {
    final SymphonyMessage symphonyMessage = new SymphonyMessage("message");
    symphonyMessage.setEnrichedTemplateMessage("template message", new Object(), "entityName", new Object(),
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
