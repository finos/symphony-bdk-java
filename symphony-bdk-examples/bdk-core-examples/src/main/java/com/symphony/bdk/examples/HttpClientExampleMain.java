package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.util.GenericClass;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

public class HttpClientExampleMain {

  public static void main(String[] args) throws BdkConfigException, IOException, GeneralSecurityException,
      ApiException, AuthUnauthorizedException, AuthInitializationException {

    JwtHelper jwtHelper = new JwtHelper();
    BdkConfig config = loadFromSymphonyDir("config.yaml");
    final SymphonyBdk bdk = new SymphonyBdk(config);
    PrivateKey privateKey = jwtHelper.parseRsaPrivateKey(
        IOUtils.toString(new FileInputStream(config.getBot().getPrivateKeyPath()), StandardCharsets.UTF_8));
    final String jwt =
        jwtHelper.createSignedJwt(config.getBot().getUsername(), JwtHelper.JWT_EXPIRATION_MILLIS, privateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    Token token = bdk.http()
        .path("https://devx1.symphony.com/login/pubkey/authenticate")
        .body(req)
        .post(new GenericClass<Token>() {});
    System.out.println(token);
  }
}
