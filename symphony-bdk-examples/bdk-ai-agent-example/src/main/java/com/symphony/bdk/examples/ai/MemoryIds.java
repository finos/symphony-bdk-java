package com.symphony.bdk.examples.ai;

/**
 * Encodes the (streamId, userId) pair used to key each user's conversation memory into a single
 * string, so that the stream a tool call is acting on can be recovered from the LangChain4j
 * {@code @MemoryId}/{@code @ToolMemoryId} value.
 */
final class MemoryIds {

  private static final String SEPARATOR = "::";

  private MemoryIds() {
  }

  static String of(String streamId, Long userId) {
    return streamId + SEPARATOR + userId;
  }

  static String streamId(String memoryId) {
    return memoryId.substring(0, memoryId.indexOf(SEPARATOR));
  }
}
