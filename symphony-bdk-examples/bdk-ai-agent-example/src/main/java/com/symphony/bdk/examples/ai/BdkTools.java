package com.symphony.bdk.examples.ai;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2MemberInfo;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BDK-backed actions exposed to the LLM as tools. LangChain4j decides on its own, based on the
 * user's message, whether and when to call these.
 */
@Slf4j
@RequiredArgsConstructor
public class BdkTools {

  private final SymphonyBdk bdk;

  @Tool("Looks up a Symphony user by username or email address and returns their display name, "
      + "email and user id. Returns 'not found' if no user matches.")
  public String lookupUser(String usernameOrEmail) {
    log.info("Tool call: lookupUser({})", usernameOrEmail);
    final List<UserV2> users = usernameOrEmail.contains("@")
        ? this.bdk.users().listUsersByEmails(Collections.singletonList(usernameOrEmail))
        : this.bdk.users().listUsersByUsernames(Collections.singletonList(usernameOrEmail));

    if (users.isEmpty()) {
      return "not found";
    }
    final UserV2 user = users.get(0);
    return String.format("%s <%s> (id=%d)", user.getDisplayName(), user.getEmailAddress(), user.getId());
  }

  @Tool("Lists the display names of the members of the Symphony room/IM the conversation is "
      + "currently happening in.")
  public String listCurrentRoomMembers(@ToolMemoryId String memoryId) {
    final String streamId = MemoryIds.streamId(memoryId);
    log.info("Tool call: listCurrentRoomMembers() in stream {}", streamId);

    final List<V2MemberInfo> members = this.bdk.streams().listStreamMembers(streamId).getMembers();
    return members.stream()
        .map(member -> member.getUser().getDisplayName())
        .collect(Collectors.joining(", "));
  }

  @Tool("Sends a chat message to another Symphony stream identified by its streamId.")
  public String sendMessageToStream(String streamId, String message) {
    log.info("Tool call: sendMessageToStream({}, {})", streamId, message);
    this.bdk.messages().send(streamId, "<messageML>" + message + "</messageML>");
    return "message sent";
  }
}
