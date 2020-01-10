package com.symphony.ms.bot.sdk.internal.symphony.model;

/**
 * Symphony stream types
 *
 * @author Gabriel Berberian
 */
public enum StreamType {
  ROOM, IM, MIM, UNKNOWN;

  public static StreamType value(String name) {
    switch (name.toUpperCase()) {
      case "ROOM":
        return ROOM;
      case "IM":
        return IM;
      case "MIM":
        return MIM;
      default:
        return UNKNOWN;
    }
  }

}
