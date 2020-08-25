package com.symphony.bdk.bot.sdk.symphony;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.symphony.bdk.bot.sdk.event.model.MessageAttachment;
import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.lib.jsonmapper.JsonMapper;
import com.symphony.bdk.bot.sdk.lib.templating.TemplateService;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

import clients.SymBotClient;
import model.Attachment;
import model.ContentAttachment;
import model.ImageInfo;
import model.InboundMessage;
import model.OutboundMessage;
import model.Stream;

@Service
public class MessageClientImpl implements MessageClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageClientImpl.class);
  private static final String ENTITY_TAG = "<div class='entity' data-entity-id='%s'>%s</div>";

  private final SymBotClient symBotClient;
  private final TemplateService templateService;
  private final JsonMapper jsonMapper;

  public MessageClientImpl(SymBotClient symBotClient, TemplateService templateService,
      JsonMapper jsonMapper) {
    this.symBotClient = symBotClient;
    this.templateService = templateService;
    this.jsonMapper = jsonMapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, SymphonyMessage message)
      throws SymphonyClientException {
    String symMessage = getSymphonyMessage(message);
    String symJsonData = null;
    if (message.isEnrichedMessage()) {
      symMessage = entitify(message.getEntityName(), symMessage);
      symJsonData = getEnricherData(message);
    }

    sendMessage(streamId, symMessage, symJsonData, message.getAttachments());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, String message, String jsonData)
      throws SymphonyClientException {
    sendMessage(streamId, message, jsonData, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, String message, String jsonData,
      List<MessageAttachmentFile> attachments) throws SymphonyClientException {
    LOGGER.debug("Sending message to stream: {}", streamId);
    List<ContentAttachment> contentAttachments = null;
    if (attachments != null && !attachments.isEmpty()) {
      contentAttachments =
          attachments.stream().map(this::toContentAttachment).collect(Collectors.toList());
    }

    OutboundMessage outMessage = new OutboundMessage(
        message != null ? message : "", jsonData, contentAttachments);

    try {
      symBotClient.getMessagesClient().sendMessage(streamId, outMessage);
    } catch (Exception e) {
      LOGGER.error("Error sending message to stream: {}", streamId);
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<MessageAttachmentFile> downloadMessageAttachments(MessageEvent messageEvent)
      throws SymphonyClientException {
    try {
      InboundMessage message = new InboundMessage();
      message.setMessageId(messageEvent.getMessageId());
      Stream stream = new Stream();
      stream.setStreamId(messageEvent.getStreamId());
      message.setStream(stream);
      List<Attachment> attachments = new ArrayList();
      for (MessageAttachment messageAttachment : messageEvent.getAttachments()) {
        Attachment attachment = new Attachment();
        attachment.setSize(messageAttachment.getSize());
        attachment.setName(messageAttachment.getName());
        if (messageAttachment.getImage() != null) {
          ImageInfo imageInfo = new ImageInfo();
          imageInfo.setId(messageAttachment.getImage().getId());
          imageInfo.setDimension(messageAttachment.getImage().getDimension());
          attachment.setImage(imageInfo);
        }
        attachment.setId(messageAttachment.getId());
        attachments.add(attachment);
      }
      message.setAttachments(attachments);
      return symBotClient.getMessagesClient()
          .getMessageAttachments(message)
          .stream()
          .map(MessageAttachmentFile::new)
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error("Error getting message attachments: {} {}", messageEvent.getMessageId(),
          messageEvent.getStreamId());
      throw new SymphonyClientException(e);
    }
  }

  /**
   * Sends message to Symphony swallowing any communication error.
   * Internal use only.
   *
   * @param streamId the stream ID
   * @param message the message to be sent
   */
  public void _sendMessage(String streamId, SymphonyMessage message) {
    try {
      sendMessage(streamId, message);
    } catch (SymphonyClientException sce) {
      LOGGER.error("Could not send message to Symphony", sce);
    }
  }

  private ContentAttachment toContentAttachment(MessageAttachmentFile messageAttachmentFile) {
    ContentAttachment contentAttachment = new ContentAttachment();
    contentAttachment.setData(messageAttachmentFile.getFileContent());
    contentAttachment.setFileName(messageAttachmentFile.getFileName());
    return contentAttachment;
  }

  private String getSymphonyMessage(SymphonyMessage message) {
    String symMessage = message.getMessage();
    if (message.hasTemplate()) {
      symMessage = processTemplateMessage(message);
    }

    return symMessage;
  }

  private String getEnricherData(SymphonyMessage message) {
    return jsonMapper.toEnricherString(message.getEntityName(),
        message.getEntity(), message.getVersion());
  }

  private String processTemplateMessage(SymphonyMessage message) {
    String renderedString = null;
    if (message.usesTemplateFile()) {
      renderedString = templateService.processTemplateFile(
          message.getTemplateFile(), message.getTemplateData());
    } else {
      renderedString = templateService.processTemplateString(
          message.getTemplateString(), message.getTemplateData());
    }

    return renderedString;
  }

  private String entitify(String entityName, String content) {
    return String.format(ENTITY_TAG, entityName, content);
  }

}
