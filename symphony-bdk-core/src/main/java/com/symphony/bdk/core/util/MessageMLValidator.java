package com.symphony.bdk.core.util;

import com.symphony.bdk.core.service.message.exception.MessageValidationException;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.user.UserService;

import org.apiguardian.api.API;
import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * The validator being used to validate the length of the message ML markdown text.
 */
@API(status = API.Status.INTERNAL)
public class MessageMLValidator {

  private static final int MARKDOWN_TEXT_LIMIT_LENGTH = 60785;

  private final MessageMLContext context;

  public MessageMLValidator(UserService userService) {
    DataProvider dataProvider = new DataProvider(userService);
    this.context = new MessageMLContext(dataProvider);
  }

  /**
   * Validate the length of the markdown text extracted from the message.
   *
   * @param message message to be validated.
   * @throws InvalidInputException thrown on invalid MessageMLV2 input
   * @throws ProcessingException thrown on errors generating the document tree
   * @throws IOException thrown on invalid EntityJSON input
   */
  public void dataExceededValidate(Message message) {
    String markdownText;
    try {
      markdownText = this.extractMarkdownTextFromMessageML(message);
    } catch (InvalidInputException | IOException | ProcessingException e) {
      throw new MessageValidationException("Couldn't extract the markdown text length of the message to verify.", e);
    }


    // apply UTF-8 encoding to the resulting markdown into array of bytes
    byte[] messageBytes = markdownText.getBytes(StandardCharsets.UTF_8);

    if (messageBytes.length > MARKDOWN_TEXT_LIMIT_LENGTH) {
      throw new MessageValidationException("The length of the markdown text of the message is exceeded.");
    }
  }

  private String extractMarkdownTextFromMessageML(Message message)
      throws InvalidInputException, IOException, ProcessingException {
    this.context.parseMessageML(message.getContent(), message.getData(), message.getVersion());
    return this.context.getMarkdown();
  }
}
