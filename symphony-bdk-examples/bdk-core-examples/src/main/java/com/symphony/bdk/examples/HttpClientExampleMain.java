package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.HttpClient;
import com.symphony.bdk.http.api.util.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.UUID;

/**
 * Perform login RSA authentication (to get the sessionToken) using {@link HttpClient}.
 */
@Slf4j
public class HttpClientExampleMain {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(BdkConfigLoader.loadFromSymphonyDir("config.yaml"))
        .build();

    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(generateJwt(bdk.config()));

    final HttpClient httpClient = bdk.http()
        .basePath(bdk.config().getBasePath())
        .header("Connection", "Keep-Alive")
        .header("Keep-Alive", "timeout=5, max=1000")
        .build();

    final Token token = httpClient.path("/login/pubkey/authenticate")
        .header("X-Trace-Id", UUID.randomUUID().toString())
        .body(req)
        .post(new TypeReference<Token>() {});

    log.info(token.getToken());
  }

  private static String generateJwt(BdkConfig config) throws Exception {
    final String pkPath = config.getBot().getPrivateKey().getPath();
    final PrivateKey privateKey = JwtHelper.parseRsaPrivateKey(
        IOUtils.toString(new FileInputStream(pkPath), StandardCharsets.UTF_8)
    );
    return JwtHelper.createSignedJwt(config.getBot().getUsername(), JwtHelper.JWT_EXPIRATION_MILLIS, privateKey);
  }
}
