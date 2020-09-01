package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserExampleMain {

  private static final String BOT_USERNAME = "tibot";

  public static void main(String[] args)
      throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException {

    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.yaml");

    SymphonyBdk bdk = new SymphonyBdk(config);
    log.info("Get user by username {}", BOT_USERNAME);
    UserV2 user = bdk.user().getUserByUsername("tibot");
    log.info("Bot's display name: {}", user.getDisplayName());
    log.info("Bot's company: {}", user.getCompany());
  }
}
