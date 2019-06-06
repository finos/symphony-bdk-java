package it.clients;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.ServerTest;

public class SymBotClientTest extends ServerTest {
  @Test
  public void symBotClientInitSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"id\": 1,\r\n" +
                "  \"emailAddress\": \"bot@symphony.com\",\r\n" +
                "  \"firstName\": \"Symphony\",\r\n" +
                "  \"lastName\": \"Bot\",\r\n" +
                "  \"displayName\": \"Symphony Bot\",\r\n" +
                "  \"title\": \"Bot\",\r\n" +
                "  \"company\": \"Acme\",\r\n" +
                "  \"username\": \"bot@symphony.com\",\r\n" +
                "  \"location\": \"California\",\r\n" +
                "  \"avatars\": [\r\n" +
                "    { \"size\": \"original\", \"url\": \"../avatars/static/150/default.png\" },\r\n" +
                "    { \"size\": \"small\", \"url\": \"../avatars/static/50/default.png\" }\r\n" +
                "  ],\r\n" +
                "  \"roles\": [\r\n" +
                "     \"CONTENT_MANAGEMENT\",\r\n" +
                "     \"INDIVIDUAL\",\r\n" +
                "     \"USER_PROVISIONING\"\r\n" +
                "  ]\r\n" +
                "}")));

    SymBotClient symBotClient = SymBotClient.initBot(config, new SymBotRSAAuth(config));
    assertNotNull(symBotClient);
    assertEquals("bot@symphony.com", symBotClient.getBotUserInfo().getUsername());
  }
}
