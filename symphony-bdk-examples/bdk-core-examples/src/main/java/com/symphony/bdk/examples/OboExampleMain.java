package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;

import com.symphony.bdk.gen.api.model.V4Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OboExampleMain {

  private static final String STREAM = "2IFEMquh3pOHAxcgLF8jU3___ozwgwIVdA";
  private static final String MESSAGE = "<messageML>Hello, World!</messageML>";

  public static void main(String[] args) throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

    final SymphonyBdk bdk = SymphonyBdk.fromSymphonyDir("config.yaml");

    final AuthSession oboSession = bdk.obo("thibault.pensec");

    final V4Message message = bdk.messages().obo(oboSession).send(STREAM, MESSAGE);
    log.info("Message {} sent using OBO auth mode", message.getMessageId());
  }
}
