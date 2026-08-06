# AI Agent example

Shows how little code is needed to turn a Symphony bot into an AI agent on top of the BDK:

- [LangChain4j](https://docs.langchain4j.dev/) `AiServices` wraps a chat model into a typed
  `Assistant` interface.
- The model is [Vertex AI Gemini](https://docs.langchain4j.dev/integrations/language-models/google-vertex-ai-gemini).
- The assistant is given BDK-backed tools (`BdkTools`) so it can look up Symphony users, list room
  members and send messages on its own, on top of just answering questions.
- Each (stream, user) pair gets its own conversation memory, so the bot remembers context per
  person per room/IM.

## Running

1. Set up a regular BDK bot `~/.symphony/config.yaml` (see the other example modules).
2. Authenticate to Google Cloud so that Application Default Credentials are available, e.g.:
   ```shell
   gcloud auth application-default login
   ```
3. Set environment variables:

   | Variable | Required | Default |
   |---|---|---|
   | `GCP_PROJECT_ID` | yes | - |
   | `GCP_LOCATION` | no | `us-central1` |
   | `GEMINI_MODEL_NAME` | no | `gemini-1.5-flash` |

4. Run `AiAgentMain`, then in a Symphony chat with the bot: `@BotMention what can you do?`

## How it works

```
@bot <question> ──▶ AskAiActivity (PatternCommandActivity)
                          │
                          ▼
                    Assistant.chat(memoryId, question)   [LangChain4j AiServices]
                          │
                          ├─▶ VertexAiGeminiChatModel
                          └─▶ BdkTools (lookupUser / listCurrentRoomMembers / sendMessageToStream)
                          │
                          ▼
                    bdk.messages().send(streamId, answer)
```
