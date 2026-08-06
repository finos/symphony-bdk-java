package com.symphony.bdk.examples.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * LangChain4j AI Service interface: the assistant's contract is a plain method, LangChain4j
 * generates the implementation (prompt assembly, memory handling, tool invocation loop) at runtime.
 */
public interface Assistant {

  @SystemMessage("""
      You are a helpful assistant answering questions inside a Symphony chat.
      You can use the provided tools to look up users, list room members and send messages.
      Keep answers concise, they will be rendered as a chat message.
      """)
  String chat(@MemoryId String memoryId, @UserMessage String message);
}
