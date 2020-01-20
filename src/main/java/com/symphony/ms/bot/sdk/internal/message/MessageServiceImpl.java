package com.symphony.ms.bot.sdk.internal.message;

import com.symphony.ms.bot.sdk.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.MessageClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
  private static final String ENTITY_TAG = "<div class='entity' data-entity-id='%s'>%s</div>";

  private final MessageClient messageClient;
  private final TemplateService templateService;
  private final JsonMapper jsonMapper;

  public MessageServiceImpl(MessageClient messageClient, TemplateService templateService,
      JsonMapper jsonMapper) {
    this.messageClient = messageClient;
    this.templateService = templateService;
    this.jsonMapper = jsonMapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, SymphonyMessage message) {
    String symMessage = getSymphonyMessage(message);
    String symJsonData = null;
    if (message.isEnrichedMessage()) {
      symMessage = entitify(message.getEntityName(), symMessage);
      symJsonData = getEnricherData(message);
    }
    try {
      if (message.getAttachments() == null || message.getAttachments().isEmpty()) {
        messageClient.sendMessage(streamId, symMessage, symJsonData);
      } else {
        messageClient.sendMessage(streamId, symMessage, symJsonData, message.getAttachments());
      }
    } catch (SymphonyClientException sce) {
      LOGGER.error("Could not send message to Symphony", sce);
    }
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
