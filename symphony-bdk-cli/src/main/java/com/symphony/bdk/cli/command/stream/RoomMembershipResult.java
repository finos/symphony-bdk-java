package com.symphony.bdk.cli.command.stream;

import java.util.LinkedHashMap;
import java.util.Map;

/** JSON confirmation payload shared by the room membership/role commands. */
final class RoomMembershipResult {

  private RoomMembershipResult() {
  }

  static Map<String, Object> of(String roomId, Long userId, String status) {
    final Map<String, Object> result = new LinkedHashMap<>();
    result.put("roomId", roomId);
    result.put("userId", userId);
    result.put("status", status);
    return result;
  }
}
