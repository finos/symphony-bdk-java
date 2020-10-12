package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OboServiceFactoryTest {

  private OboServiceFactory oboServiceFactory;

  @BeforeEach
  void setUp() throws BdkConfigException {
    this.oboServiceFactory = new OboServiceFactory(mock(ApiClientFactory.class),  mock(AuthSession.class), new BdkConfig());
  }

  @Test
  void getOboUserServiceTest() {
    assertNotNull(this.oboServiceFactory.getObUserService());
  }

  @Test
  void getOboStreamServiceTest() {
    assertNotNull(this.oboServiceFactory.getOboStreamService());
  }

  @Test
  void getOboMessageServiceTest() {
    assertNotNull(this.oboServiceFactory.getOboMessageService());
  }
}
