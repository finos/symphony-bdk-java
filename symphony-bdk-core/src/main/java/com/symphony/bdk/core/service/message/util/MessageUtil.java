package com.symphony.bdk.core.service.message.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for pre-processing an outgoing message
 */
@API(status = API.Status.EXPERIMENTAL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageUtil {
  private static Map<String, String> tokens = initializeMap();
  private static final Pattern PATTERN = initializePattern(tokens);

  /**
   * This method takes care of all special characters placed within the messageML that must be HTML-escaped
   * to have a valid MessageML format.
   *
   * @param rawText text to be parsed
   * @return text in a valid messageML format
   */
  public static String escapeSpecialChars(String rawText) {
    Matcher matcher = PATTERN.matcher(rawText);
    StringBuffer parsedText = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(parsedText, getReplacementOf(matcher.group(1)));
    }
    matcher.appendTail(parsedText);
    return parsedText.toString();
  }

  private static String getReplacementOf(String group) {
    String replacement = tokens.get(group);
    if (replacement == null) {
      replacement = tokens.get("\\" + group);
    }
    return replacement;
  }

  private static Map<String, String> initializeMap() {
    tokens = new LinkedHashMap<>();
    tokens.put("&", "&amp;");
    tokens.put("<", "&lt;");
    tokens.put(">", "&gt;");
    tokens.put("'", "&apos;");
    tokens.put("\"", "&quot;");
    tokens.put("#", "&#35;");
    tokens.put("\\$", "&#36;");
    tokens.put("%", "&#37;");
    tokens.put("\\(", "&#40;");
    tokens.put("\\)", "&#41;");
    tokens.put("\\*", "&#42;");
    tokens.put("\\.", "&#46;");
    tokens.put(";", "&#59;");
    tokens.put("=", "&#61;");
    tokens.put("\\[", "&#91;");
    tokens.put("\\\\", "&#92;");
    tokens.put("\\]", "&#93;");
    tokens.put("`", "&#96;");
    tokens.put("\\{", "&#123;");
    tokens.put("\\}", "&#125;");
    return tokens;
  }

  private static Pattern initializePattern(Map<String, String> tokens) {
    return Pattern.compile("(" + StringUtils.join(tokens.keySet(), "|") + ")");
  }

}
