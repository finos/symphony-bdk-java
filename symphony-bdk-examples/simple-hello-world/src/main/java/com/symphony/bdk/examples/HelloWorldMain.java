package com.symphony.bdk.examples;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.jersey2.ApiClientJersey2;
import com.symphony.bdk.core.auth.JwtHelper;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

@Slf4j
public class HelloWorldMain {

  private static final String POD_BASE_URL = "https://devx1.symphony.com";

  public static void main(String[] args) throws Exception {

    final AuthenticationApi loginAuthApi = new AuthenticationApi(new ApiClientJersey2(POD_BASE_URL + "/login"));
    final AuthenticationApi kmAuthApi = new AuthenticationApi(new ApiClientJersey2(POD_BASE_URL + "/relay"));

    final AuthenticateRequest request = new AuthenticateRequest();
    request.setToken(generateJwt());

    final String sessionToken = loginAuthApi.pubkeyAuthenticatePost(request).getToken();
    final String keyManagerToken = kmAuthApi.pubkeyAuthenticatePost(request).getToken();

    log.info("Successfully Authenticated !");
    log.info("#### sessionToken ####\n{}", sessionToken);
    log.info("#### keyManagerToken ####\n{}", keyManagerToken);

    final MessagesApi messagesApi = new MessagesApi(new ApiClientJersey2(POD_BASE_URL + "/agent"));
    final V4Message message = messagesApi.v4StreamSidMessageCreatePost(
        "2IFEMquh3pOHAxcgLF8jU3___ozwgwIVdA",
        sessionToken,
        keyManagerToken,
        "<messageML>Hello, World!</messageML>",
        null, null, null, null
    );

    log.info("Message {} successfully sent", message.getMessageId());
  }

  private static String generateJwt() throws GeneralSecurityException, IOException {

    final String privateKeyPath = System.getProperty("privateKeyPath");
    final String username = System.getProperty("username");

    log.info("privateKeyPath={}", privateKeyPath);
    log.info("username={}", username);

    final PrivateKey privateKey = JwtHelper.parseRSAPrivateKey(IOUtils.toString(new FileInputStream(privateKeyPath), StandardCharsets.UTF_8));
    return JwtHelper.createSignedJwt(username, 30_000, privateKey);
  }
}
