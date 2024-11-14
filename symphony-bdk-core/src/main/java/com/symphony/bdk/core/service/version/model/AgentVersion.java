package com.symphony.bdk.core.service.version.model;

import org.apiguardian.api.API;

/**
 * AgentVersion model used by AgentVersionService.
 * Allows to compare two versions of the Agent for feature gating
 */
@API(status = API.Status.INTERNAL)
public class AgentVersion {
  public static final AgentVersion AGENT_24_12 = new AgentVersion(24,12);


  private final int major;
  private final int minor;

  public AgentVersion(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }

  public boolean isHigher(AgentVersion agentVersion) {
    if (major == agentVersion.major) {
      return minor >= agentVersion.minor;
    }
    return major > agentVersion.major;
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor;
  }
}
