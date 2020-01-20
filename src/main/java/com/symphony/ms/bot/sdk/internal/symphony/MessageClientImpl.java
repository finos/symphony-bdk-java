package com.symphony.ms.bot.sdk.internal.symphony;

import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachment;
import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachmentFile;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.lib.file.FileUtils;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;

import clients.SymBotClient;
import model.Attachment;
import model.ImageInfo;
import model.InboundMessage;
import model.OutboundMessage;
import model.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageClientImpl implements MessageClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageClientImpl.class);

  private final SymBotClient symBotClient;
  private final FeatureManager featureManager;

  public MessageClientImpl(SymBotClient symBotClient,
      FeatureManager featureManager) {
    this.symBotClient = symBotClient;
    this.featureManager = featureManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, String message, String jsonData)
      throws SymphonyClientException {
    OutboundMessage outMessage = new OutboundMessage(message != null ? message : "", jsonData);
    internalSendMessage(streamId, outMessage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, String message, String jsonData,
      List<MessageAttachmentFile> attachments) throws SymphonyClientException {
    UUID uuid = UUID.randomUUID();
    File[] attachmentsFile = storeAttachments(attachments, uuid);
    OutboundMessage outMessage =
        new OutboundMessage(message != null ? message : "", jsonData, attachmentsFile);
    internalSendMessage(streamId, outMessage);
    deleteAttachments(uuid);
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

  private void internalSendMessage(String streamId, OutboundMessage message)
      throws SymphonyClientException {
    LOGGER.debug("Sending message to stream: {}", streamId);
    try {
      symBotClient.getMessagesClient().sendMessage(streamId, message);
    } catch (Exception e) {
      LOGGER.error("Error sending message to stream: {}", streamId);
      throw new SymphonyClientException(e);
    }
  }

  private File[] storeAttachments(List<MessageAttachmentFile> attachments, UUID uuid) {
    return attachments.stream()
        .map(attachment -> storeAttachment(attachment, uuid))
        .toArray(File[]::new);
  }

  private File storeAttachment(MessageAttachmentFile attachment, UUID uuid) {
    try {
      String path = featureManager.getStorePath() + "/" + uuid + "/" + attachment.getFileName();
      return FileUtils.writeFile(path, attachment.getFileContent());
    } catch (IOException e) {
      LOGGER.error("Failure storing attachment file", e);
    }
    return null;
  }

  private void deleteAttachments(UUID uuid) {
    String path = featureManager.getStorePath() + "/" + uuid;
    FileUtils.deleteDirectory(path);
  }

}
