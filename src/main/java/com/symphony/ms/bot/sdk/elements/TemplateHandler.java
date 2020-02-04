package com.symphony.ms.bot.sdk.elements;

import static com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder.beginsWith;
import static com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder.group;
import static com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder.oneOrMore;
import static com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder.optional;
import static com.symphony.ms.bot.sdk.internal.command.matcher.EscapedCharacter.whiteSpace;
import static com.symphony.ms.bot.sdk.internal.command.matcher.EscapedCharacter.word;
import static services.SmsRenderer.SmsTypes.valueOf;

import com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.elements.ElementsHandler;
import com.symphony.ms.bot.sdk.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUser;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.SmsRenderer.SmsTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Sample code. CommandHandler that uses Symphony Renderer templates.
 *
 * @author Gabriel Berberian
 */
public class TemplateHandler extends ElementsHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateHandler.class);

  private static final String FORM_ID = "template-form";

  private static final String SIMPLE_TEMPLATE = "simple-template";
  private static final String ALERT_TEMPLATE = "alert-template";
  private static final String INFORMATION_TEMPLATE = "information-template";
  private static final String NOTIFICATION_TEMPLATE = "notification-template";
  private static final String LIST_TEMPLATE = "list-template";
  private static final String TABLE_TEMPLATE = "table-template";
  private static final String INVALID_TEMPLATE = "invalid-template";
  private static final Pattern STRUCTURED_LIST_PATTERN =
      Pattern.compile("^(\\s*\\[[^\\[\\]\\|]*\\|[^\\[\\]\\|]*\\]\\s*)+$");
  private static final Pattern STRUCTURED_LIST_ELEMENT_PATTERN =
      Pattern.compile("\\[([^\\[\\]\\|]*\\|[^\\[\\]\\|]*)\\]");
  private static final Pattern TABLE_PATTERN =
      Pattern.compile("\\s*\\[([^\\[\\]]*)\\]\\s*");

  private static final String[] DESCRIPTIONS = {
      "/template simple",
      "/template alert",
      "/template information",
      "/template notification",
      "/template list",
      "/template table"
  };

  private Pattern templateCommandPattern;

  @Override
  public void init() {
    templateCommandPattern = buildTemplateCommandPattern();
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return templateCommandPattern.asPredicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  @Override
  public void displayElements(BotCommand command, SymphonyMessage elementsResponse) {
    Optional<SmsTypes> templateType = getTemplateType(command.getMessageEvent().getMessage());
    if (templateType.isPresent()) {
      Map<String, String> data = new HashMap<>();
      data.put("form-id", getElementsFormId());
      switch (templateType.get()) {
        case SIMPLE:
          elementsResponse.setTemplateFile(SIMPLE_TEMPLATE, data);
          break;
        case ALERT:
          elementsResponse.setTemplateFile(ALERT_TEMPLATE, data);
          break;
        case INFORMATION:
          elementsResponse.setTemplateFile(INFORMATION_TEMPLATE, data);
          break;
        case NOTIFICATION:
          elementsResponse.setTemplateFile(NOTIFICATION_TEMPLATE, data);
          break;
        case LIST:
          elementsResponse.setTemplateFile(LIST_TEMPLATE, data);
          break;
        case TABLE:
          elementsResponse.setTemplateFile(TABLE_TEMPLATE, data);
      }
    } else {
      Map<String, Object> data = new HashMap<>();
      data.put("botMention", "@" + getBotName());
      data.put("descriptions", DESCRIPTIONS);
      elementsResponse.setTemplateFile(INVALID_TEMPLATE, data);
    }
  }

  @Override
  public void handleAction(SymphonyElementsEvent event, SymphonyMessage elementsResponse) {
    Map<String, Object> formValues = event.getFormValues();
    SmsTypes action = SmsTypes.valueOf(((String) formValues.get("action")).toUpperCase());
    formValues.remove("action");
    Map<String, Object> message = new HashMap<>();
    Object messageValue = null;
    switch (action) {
      case SIMPLE:
      case ALERT:
      case INFORMATION:
        messageValue = formValues;
        break;
      case NOTIFICATION:
        messageValue = handleNotification(formValues);
        break;
      case LIST:
        messageValue = handleList(formValues);
        break;
      case TABLE:
        messageValue = handleTable(formValues);
    }
    message.put("message", messageValue);
    elementsResponse.setTemplateFile(action.getName(), message);
  }

  private Pattern buildTemplateCommandPattern() {
    return beginsWith("@")
        .followedBy(getBotName())
        .followedBy(whiteSpace())
        .followedBy("/template")
        .followedBy(
            optional(
                new CommandMatcherBuilder()
                    .followedBy(whiteSpace())
                    .followedBy(
                        group(
                            oneOrMore(word())
                        )
                    )
            )
        ).pattern();
  }

  private Optional<SmsTypes> getTemplateType(String commandMessage) {
    Matcher matcher = templateCommandPattern.matcher(commandMessage);
    try {
      if (!matcher.find()) {
        return Optional.empty();
      }
      String typeName = matcher.group(2);
      if (StringUtils.isBlank(typeName)) {
        return Optional.empty();
      }
      SmsTypes type = valueOf(typeName.toUpperCase());
      return Optional.of(type);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private Map<String, Object> handleNotification(Map<String, Object> formValues) {
    Boolean alert = Boolean.parseBoolean((String) formValues.get("alert"));
    formValues.put("alert", alert);

    Map<String, String> content = new HashMap<>();
    String header = (String) formValues.get("contentHeader");
    if (header != null && !header.trim().isEmpty()) {
      content.put("header", header);
      String body = (String) formValues.get("contentBody");
      if (body != null && !body.trim().isEmpty()) {
        content.put("body", body);
      }
    }
    if (content.size() > 0) {
      formValues.put("content", content);
    }
    formValues.remove("contentHeader");
    formValues.remove("contentBody");

    Map<String, String> comment = new HashMap<>();
    comment.put("body", (String) formValues.get("comment"));
    formValues.put("comment", comment);

    Object assignee = formValues.get("assignee");
    if (assignee != null) {
      Long userId = (Long) ((List) assignee).get(0);
      SymphonyUser user = getUserById(userId);
      if (user != null) {
        Map<String, String> displayName = new HashMap<>();
        displayName.put("displayName", user.getDisplayName());
        formValues.put("assignee", displayName);
      }
    }

    Boolean showStatusBar = Boolean.parseBoolean((String) formValues.get("showStatusBar"));
    formValues.put("showStatusBar", showStatusBar);

    Map<String, String> typeName = new HashMap<>();
    typeName.put("name", (String) formValues.get("type"));
    formValues.put("type", typeName);

    Map<String, String> priorityName = new HashMap<>();
    priorityName.put("name", (String) formValues.get("priority"));
    formValues.put("priority", priorityName);

    Map<String, String> statusName = new HashMap<>();
    statusName.put("name", (String) formValues.get("status"));
    formValues.put("status", statusName);

    List<Map<String, String>> labels = new ArrayList<>();
    for (String label : ((String) formValues.get("labels")).split(",")) {
      Map<String, String> text = new HashMap<>();
      text.put("text", label.trim());
      labels.add(text);
    }
    formValues.put("labels", labels);
    return formValues;
  }

  private SymphonyUser getUserById(long userId) {
    try {
      SymphonyUser user = usersClient.getUserFromId(userId, true);
      return user != null ? user : usersClient.getUserFromId(userId, false);
    } catch (SymphonyClientException e) {
      LOGGER.error("Exception getting user by id {}", userId);
      return null;
    }
  }

  private Map<String, Object> handleList(Map<String, Object> formValues) {
    String contentValue = (String) formValues.get("content");
    if (STRUCTURED_LIST_PATTERN.matcher(contentValue).matches()) {
      return handleStructuredList(formValues, contentValue);
    } else {
      return handleSimpleList(formValues, contentValue);
    }
  }

  private Map<String, Object> handleStructuredList(Map<String, Object> formValues,
      String contentValue) {
    List<Map<String, String>> content = new ArrayList<>();
    Matcher listElementMatcher = STRUCTURED_LIST_ELEMENT_PATTERN.matcher(contentValue);
    while (listElementMatcher.find()) {
      String[] element = listElementMatcher.group(1).split("\\|");
      Map<String, String> contentElement = new HashMap<>();
      contentElement.put("header", element[0].trim());
      contentElement.put("body", element[1].trim());
      content.add(contentElement);
    }
    formValues.put("content", content);
    return formValues;
  }

  private Map<String, Object> handleSimpleList(Map<String, Object> formValues,
      String contentValue) {
    List<String> content =
        Arrays.stream(contentValue.split(",")).map(String::trim).collect(Collectors.toList());
    formValues.put("content", content);
    return formValues;
  }

  private List<Map<String, String>> handleTable(Map<String, Object> formValues) {
    List<Map<String, String>> content = new ArrayList<>();
    String contentValue = ((String) formValues.get("content")).trim();
    Matcher tableMatcher = TABLE_PATTERN.matcher(contentValue);
    if (tableMatcher.find()) {
      String headerLine = tableMatcher.group(1);
      String[] headers = headerLine.split("\\|");
      while (tableMatcher.find()) {
        String[] columns = tableMatcher.group(1).split("\\|");
        Map<String, String> contentElement = new TreeMap<>();
        for (int i = 0; i < headers.length; i++) {
          if (i < columns.length) {
            contentElement.put(headers[i].trim(), columns[i].trim());
          } else {
            contentElement.put(headers[i].trim(), null);
          }
        }
        content.add(contentElement);
      }
    }
    return content;
  }

}
