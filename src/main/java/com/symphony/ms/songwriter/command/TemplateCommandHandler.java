package com.symphony.ms.songwriter.command;

import static com.symphony.ms.songwriter.internal.command.matcher.CharacterMatcher.any;
import static com.symphony.ms.songwriter.internal.command.matcher.CharacterMatcher.characterSet;
import static com.symphony.ms.songwriter.internal.command.matcher.CharacterMatcher.negatedSet;
import static com.symphony.ms.songwriter.internal.command.matcher.CommandMatcherBuilder.beginsWith;
import static com.symphony.ms.songwriter.internal.command.matcher.CommandMatcherBuilder.group;
import static com.symphony.ms.songwriter.internal.command.matcher.CommandMatcherBuilder.nonCapturingGroup;
import static com.symphony.ms.songwriter.internal.command.matcher.CommandMatcherBuilder.oneOrMore;
import static com.symphony.ms.songwriter.internal.command.matcher.CommandMatcherBuilder.optional;
import static com.symphony.ms.songwriter.internal.command.matcher.EscapedCharacter.character;
import static com.symphony.ms.songwriter.internal.command.matcher.EscapedCharacter.whiteSpace;

import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

import org.apache.commons.lang.StringUtils;
import services.SmsRenderer;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sample code. CommandHandler that uses Symphony Renderer templates.
 */
public class TemplateCommandHandler extends CommandHandler {

  private JsonMapper jsonMapper;
  private Pattern templateCommandPattern;

  public TemplateCommandHandler(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return getTemplateCommandPattern().asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    Optional<SmsRenderer.SmsTypes> templateType = getTemplateType(command.getMessage());
    if (templateType.isPresent()) {
      Optional<Map<String, Object>> commandParameter = getCommandParameter(command.getMessage());
      if (commandParameter.isPresent()) {
        renderTemplate(templateType.get(), commandParameter.get(), commandResponse);
      } else {
        commandResponse.setMessage("Please, provide a valid parameter to the specified template");
      }
    } else {
      commandResponse.setMessage("Please, specify the name of a valid template");
    }
  }

  private Optional<SmsRenderer.SmsTypes> getTemplateType(String commandMessage) {
    Matcher matcher = getTemplateCommandPattern().matcher(commandMessage);
    try {
      if (!matcher.find()) {
        return Optional.empty();
      }
      String typeName = matcher.group(1);
      if (StringUtils.isBlank(typeName)) {
        return Optional.empty();
      }
      SmsRenderer.SmsTypes type = SmsRenderer.SmsTypes.valueOf(typeName.toUpperCase());
      return Optional.of(type);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private Optional<Map<String, Object>> getCommandParameter(String commandMessage) {
    Matcher matcher = getTemplateCommandPattern().matcher(commandMessage);
    if (matcher.find()) {
      String commandParameter = matcher.group(2);
      if (StringUtils.isNotBlank(commandParameter)) {
        return Optional.of(wrapData(commandParameter));
      }
    }
    return Optional.empty();
  }

  private Map<String, Object> wrapData(String data) {
    return jsonMapper.toObject("{\"message\": " + data + "}", Map.class);
  }

  private void renderTemplate(SmsRenderer.SmsTypes templateType,
      Map<String, Object> commandParameter, SymphonyMessage commandResponse) {
    commandResponse.setTemplateFile(templateType.getName(), commandParameter);
  }

  private Pattern getTemplateCommandPattern() {
    if (templateCommandPattern == null) {
      templateCommandPattern = buildTemplateCommandPattern();
    }
    return templateCommandPattern;
  }

  private Pattern buildTemplateCommandPattern() {
    characterSet(character('a'));
    return beginsWith("@")
        .followedBy(getBotName())
        .followedBy(whiteSpace())
        .followedBy("/template")
        .followedBy(
            optional(
                nonCapturingGroup(
                    oneOrMore(whiteSpace()
                    ).followedBy(
                        optional(
                            nonCapturingGroup(
                                group(
                                    oneOrMore(
                                        negatedSet(whiteSpace())
                                    )
                                ).followedBy(
                                    optional(
                                        nonCapturingGroup(
                                            oneOrMore(whiteSpace()
                                            ).followedBy(
                                                optional(
                                                    group(
                                                        oneOrMore(any())
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        ).pattern();
  }

}
