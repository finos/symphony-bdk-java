package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.impl.AuthSessionImpl;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRSAImpl;
import com.symphony.bdk.core.auth.impl.OboAuthenticatorRSAImpl;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.BdkConfig;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

/**
 *
 */
public class AuthenticatorFactory {

  private final BdkConfig config;
  private final ApiClient loginApiClient;
  private final ApiClient relayApiClient;

  @SneakyThrows
  public AuthenticatorFactory(@Nonnull BdkConfig bdkConfig, @Nonnull ApiClient loginClient, @Nonnull ApiClient relayClient) {
    this.config = bdkConfig;
    this.loginApiClient = loginClient;
    this.relayApiClient = relayClient;
  }

  @SneakyThrows
  public AuthSession getBotAuthSession() {

    final BotAuthenticatorRSAImpl authenticator = new BotAuthenticatorRSAImpl(
        this.config.getUsername(),
        JwtHelper.parseRSAPrivateKey(this.loadPrivateKeyContent(this.config.getPrivateKeyPath())),
        this.loginApiClient,
        this.relayApiClient
    );

    return new AuthSessionImpl(authenticator);
  }

  @SneakyThrows
  public OboAuthenticator getOboAuthenticator() {

    return new OboAuthenticatorRSAImpl(
        this.loginApiClient,
        this.config.getAppId(),
        JwtHelper.parseRSAPrivateKey(this.loadPrivateKeyContent(this.config.getAppPrivateKeyPath()))
    );
  }

  private String loadPrivateKeyContent(String path) throws IOException {
    return IOUtils.toString(new FileInputStream(path), StandardCharsets.UTF_8);
  }
}
