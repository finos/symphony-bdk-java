package com.symphony.bdk.core.service.version.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentVersionTest {

  @Test
  public void testHigherVersion() {
    AgentVersion agent_20_9 = new AgentVersion(20,9);
    AgentVersion agent_24_12 = new AgentVersion(24,12);
    AgentVersion agent_25_01 = new AgentVersion(25,1);


    assertTrue(agent_24_12.isHigher(agent_20_9));
    assertTrue(agent_25_01.isHigher(agent_20_9));
    assertTrue(agent_25_01.isHigher(agent_24_12));
  }
}
