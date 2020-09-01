package com.symphony.bdk.core.command;

import com.symphony.bdk.gen.api.model.V4Message;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * TODO: add description here
 */
@Slf4j
public class BotMentionBotCommandMatcher implements BotCommandMatcher {

  private final String botDisplayName;

  public BotMentionBotCommandMatcher(String botDisplayName) {
    this.botDisplayName = botDisplayName;
  }

  @Override
  public String match(V4Message incomingMessage) {

    final String messageTextContent = this.getMessageTextContext(incomingMessage.getMessage());

    if (messageTextContent.startsWith(this.botDisplayName)) {
      return messageTextContent.replace(this.botDisplayName, "");
    }

    return null;
  }

  @SneakyThrows // TODO handle exception properly here
  private String getMessageTextContext(String presentationML) {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.parse(new ByteArrayInputStream(presentationML.getBytes(StandardCharsets.UTF_8)));
    return doc.getChildNodes().item(0).getTextContent();
  }
}
