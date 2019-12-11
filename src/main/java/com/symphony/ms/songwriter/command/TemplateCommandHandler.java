package com.symphony.ms.songwriter.command;

import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;
import com.symphony.ms.songwriter.internal.symphony.SymphonyService;

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

  public TemplateCommandHandler(JsonMapper jsonMapper, SymphonyService symphonyService) {
    this.jsonMapper = jsonMapper;
    this.symphonyService = symphonyService;
    this.templateCommandPattern =
        Pattern.compile("^@" + getBotName() + "\\s/template(\\s+(([^\\s]+)(\\s+([\\s\\S]+)?)?)?)?");
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return templateCommandPattern.asPredicate();
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
    Matcher matcher = templateCommandPattern.matcher(commandMessage);
    try {
      if (!matcher.find()) {
        return Optional.empty();
      }
      String typeName = matcher.group(3);
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
    Matcher matcher = templateCommandPattern.matcher(commandMessage);
    if (matcher.find()) {
      String commandParameter = matcher.group(5);
      if (StringUtils.isNotBlank(commandParameter)) {
        return Optional.of(wrapData(commandParameter));
      }
    }
    return Optional.empty();
  }

  private Map<String, Object> wrapData(String data) {
    return jsonMapper.toObject("{\"message\": " + data + "}", Map.class);
  }

  public void renderTemplate(SmsRenderer.SmsTypes templateType,
      Map<String, Object> commandParameter, SymphonyMessage commandResponse) {
    commandResponse.setTemplateFile(templateType.getName(), commandParameter);

  }

}
