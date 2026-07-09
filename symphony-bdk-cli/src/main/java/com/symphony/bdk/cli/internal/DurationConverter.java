package com.symphony.bdk.cli.internal;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a human-friendly duration such as {@code 30s}, {@code 5m}, {@code 1h} or {@code 500ms}.
 * A bare number is interpreted as seconds. ISO-8601 durations ({@code PT30S}) are also accepted.
 */
public class DurationConverter implements ITypeConverter<Duration> {

  private static final Pattern SHORTHAND = Pattern.compile("(?i)\\s*(\\d+)\\s*(ms|s|m|h)?\\s*");

  @Override
  public Duration convert(String value) {
    if (value == null || value.isBlank()) {
      throw new TypeConversionException("empty duration");
    }
    final String trimmed = value.trim();
    if (trimmed.regionMatches(true, 0, "P", 0, 1)) {
      try {
        return Duration.parse(trimmed);
      } catch (Exception e) {
        throw new TypeConversionException("invalid ISO-8601 duration: '" + value + "'");
      }
    }
    final Matcher m = SHORTHAND.matcher(trimmed);
    if (!m.matches()) {
      throw new TypeConversionException("invalid duration: '" + value + "' (use e.g. 30s, 5m, 1h)");
    }
    final long n = Long.parseLong(m.group(1));
    final String unit = m.group(2) == null ? "s" : m.group(2).toLowerCase();
    switch (unit) {
      case "ms":
        return Duration.ofMillis(n);
      case "s":
        return Duration.ofSeconds(n);
      case "m":
        return Duration.ofMinutes(n);
      case "h":
        return Duration.ofHours(n);
      default:
        throw new TypeConversionException("invalid duration unit: '" + unit + "'");
    }
  }
}
