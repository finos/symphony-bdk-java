package com.symphony.bdk.examples.ai;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.vertexai.gemini.VertexAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;

/**
 * Showcases how little code is needed to turn a Symphony bot into an AI agent: a
 * {@link VertexAiGeminiChatModel}, a LangChain4j {@link AiServices} wiring in BDK-backed
 * {@link BdkTools} and per-user {@link MessageWindowChatMemory}, and one {@link AskAiActivity}
 * bridging the two worlds.
 */
@Slf4j
public class AiAgentMain {

  private static final String DEFAULT_LOCATION = "us-central1";
  private static final String DEFAULT_MODEL_NAME = "gemini-3.5-flash";
  private static final int MAX_MEMORY_MESSAGES = 10;

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    final ChatModel chatModel = VertexAiGeminiChatModel.builder()
        .project(requiredEnv("GCP_PROJECT_ID"))
        .location(getEnv("GCP_LOCATION", DEFAULT_LOCATION))
        .modelName(getEnv("GEMINI_MODEL_NAME", DEFAULT_MODEL_NAME))
        .build();

    final Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .tools(new BdkTools(bdk))
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(MAX_MEMORY_MESSAGES))
        .build();

    bdk.activities().register(new AskAiActivity(assistant, bdk.messages()));

    bdk.datafeed().start();
  }

  private static String getEnv(String name, String defaultValue) {
    final String value = System.getenv(name);
    return value == null || value.isEmpty() ? defaultValue : value;
  }

  private static String requiredEnv(String name) {
    final String value = System.getenv(name);
    if (value == null || value.isEmpty()) {
      throw new IllegalStateException("Missing required environment variable: " + name);
    }
    return value;
  }
}
