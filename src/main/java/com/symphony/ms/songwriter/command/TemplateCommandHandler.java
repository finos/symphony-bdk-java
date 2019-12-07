package com.symphony.ms.songwriter.command;

import static services.SmsRenderer.SmsTypes.ALERT;
import static services.SmsRenderer.SmsTypes.INFORMATION;
import static services.SmsRenderer.SmsTypes.LIST;
import static services.SmsRenderer.SmsTypes.NOTIFICATION;
import static services.SmsRenderer.SmsTypes.SIMPLE;
import static services.SmsRenderer.SmsTypes.TABLE;

import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapperException;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapperImpl;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import services.SmsRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateCommandHandler extends CommandHandler {

  private JsonMapper jsonMapper;

  public TemplateCommandHandler() {
    jsonMapper = new JsonMapperImpl(new ObjectMapper());
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern.compile("^@" + getBotName() + " /template").asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    Optional<SmsRenderer.SmsTypes> templateType = getTemplateType(command.getMessage());
    if (templateType.isPresent()) {
      Optional<String> commandParameter =
          getCommandParameter(command.getMessage(), templateType.get());
      if (commandParameter.isPresent()) {
        renderTemplate(templateType.get(), commandParameter.get(), commandResponse);
      } else {
        commandResponse.setMessage("Please, provide a parameter to the specified template");
      }
    } else {
      commandResponse.setMessage("Please, specify the name of a valid template");
    }
  }

  private Optional<SmsRenderer.SmsTypes> getTemplateType(String commandMessage) {
    Matcher matcher = Pattern.compile("(?<=@" + getBotName() + "\\s/template\\s)[^\\s]+")
        .matcher(commandMessage);
    if (matcher.find()) {
      String typeName = commandMessage.substring(matcher.start(), matcher.end());
      SmsRenderer.SmsTypes type = SmsRenderer.SmsTypes.valueOf(typeName.toUpperCase());
      if (type != null) {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }

  private Optional<String> getCommandParameter(String commandMessage,
      SmsRenderer.SmsTypes templateType) {
    Matcher matcher = Pattern.compile(
        "(?<=" + getBotName() + "\\s\\/template\\s" + templateType.getName() + "\\s)[^\\s].*")
        .matcher(commandMessage);
    if (matcher.find()) {
      return Optional.of(commandMessage.substring(matcher.start()));
    }
    return Optional.empty();
  }

  public void renderTemplate(SmsRenderer.SmsTypes templateType, String commandParameter,
      SymphonyMessage commandResponse) {
    switch (templateType) {
      case SIMPLE:
        renderSimpleTemplate(commandParameter, commandResponse);
        break;
      case ALERT:
        renderAlertTemplate(commandParameter, commandResponse);
        break;
      case INFORMATION:
        renderInformationTemplate(commandParameter, commandResponse);
        break;
      case NOTIFICATION:
        renderNotificationTemplate(commandParameter, commandResponse);
        break;
      case TABLE:
        renderTableTemplate(commandParameter, commandResponse);
        break;
      case LIST:
        renderListTemplate(commandParameter, commandResponse);
    }
  }

  private void renderSimpleTemplate(String commandParameter, SymphonyMessage commandResponse) {
    try {
      Map<String, Object> data = jsonMapper.toObject(commandParameter, Map.class);
      commandResponse.setTemplateFile(SIMPLE.getName(), wrapData(data));
    } catch (JsonMapperException e) {
      commandResponse.setMessage("Invalid parameter for " + SIMPLE.getName() + " template");
    }
  }

  private void renderAlertTemplate(String commandParameter, SymphonyMessage commandResponse) {
    try {
      Map<String, Object> data = jsonMapper.toObject(commandParameter, Map.class);
      commandResponse.setTemplateFile(ALERT.getName(), wrapData(data));
    } catch (JsonMapperException e) {
      commandResponse.setMessage("Invalid parameter for " + ALERT.getName() + " template");
    }
  }

  private void renderInformationTemplate(String commandParameter, SymphonyMessage commandResponse) {
    try {
      Map<String, Object> data = jsonMapper.toObject(commandParameter, Map.class);
      commandResponse.setTemplateFile(INFORMATION.getName(), wrapData(data));
    } catch (JsonMapperException e) {
      commandResponse.setMessage("Invalid parameter for " + INFORMATION.getName() + " template");
    }
  }

  private void renderNotificationTemplate(String commandParameter,
      SymphonyMessage commandResponse) {
    try {
      Map<String, Object> data = jsonMapper.toObject(commandParameter, Map.class);
      commandResponse.setTemplateFile(NOTIFICATION.getName(), wrapData(data));
    } catch (JsonMapperException e) {
      commandResponse.setMessage("Invalid parameter for " + NOTIFICATION.getName() + " template");
    }
  }

  private void renderTableTemplate(String commandParameter, SymphonyMessage commandResponse) {
    try {
      List data = jsonMapper.toObject(commandParameter, List.class);
      commandResponse.setTemplateFile(TABLE.getName(), wrapData(data));
    } catch (JsonMapperException e) {
      commandResponse.setMessage("Invalid parameter for " + TABLE.getName() + " template");
    }
  }

  private void renderListTemplate(String commandParameter,
      SymphonyMessage commandResponse) {
    try {
      Map<String, Object> data = jsonMapper.toObject(commandParameter, Map.class);
      commandResponse.setTemplateFile(LIST.getName(), wrapData(data));
    } catch (JsonMapperException e) {
      commandResponse.setMessage("Invalid parameter for " + LIST.getName() + " template");
    }
  }

  private Map<String, Object> wrapData(Object data) {
    Map<String, Object> wrapper = new HashMap<>();
    wrapper.put("message", data);
    return wrapper;
  }

}
