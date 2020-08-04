package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.exception.AuthenticationException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.security.PrivateKey;

@Slf4j
@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public class BotAuthenticatorRSAImpl {

  private final String username;
  private final PrivateKey privateKey;

  private final ApiClient loginApiClient;
  private final ApiClient relayApiClient;

  public String retrieveSessionToken() throws AuthenticationException {
    log.debug("Start retrieving sessionToken using RSA authentication...");
    return this.doRetrieveToken(this.loginApiClient);
  }

  public String retrieveKeyManagerToken() throws AuthenticationException {
    log.debug("Start retrieving keyManagerToken using RSA authentication...");
    return this.doRetrieveToken(this.relayApiClient);
  }

  private String doRetrieveToken(ApiClient client) throws AuthenticationException {
    final String jwt = JwtHelper.createSignedJwt(this.username, 30_000, this.privateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    try {
      return new AuthenticationApi(client).pubkeyAuthenticatePost(req).getToken();
    } catch (ApiException ex) {
      throw new AuthenticationException("Cannot retrieve keyManagerToken for user : " + this.username, ex);
    }
  }
}
