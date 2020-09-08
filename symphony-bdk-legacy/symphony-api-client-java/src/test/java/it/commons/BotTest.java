package it.commons;

import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.PodConstants;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.Before;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BotTest extends ServerTest {
    protected SymBotClient symBotClient;

    @Before
    public void initSymBot() {
        stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("{\r\n" +
                    "  \"id\": 1,\r\n" +
                    "  \"emailAddress\": \"admin@symphony.com\",\r\n" +
                    "  \"firstName\": \"Symphony\",\r\n" +
                    "  \"lastName\": \"Admin\",\r\n" +
                    "  \"displayName\": \"Symphony Admin\",\r\n" +
                    "  \"title\": \"Administrator\",\r\n" +
                    "  \"company\": \"Acme\",\r\n" +
                    "  \"username\": \"admin@symphony.com\",\r\n" +
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

        stubFor(post(urlEqualTo("/login/pubkay/authenticate"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("{\r\n" +
                    " \"token\": " + UUID.randomUUID().toString() + ",\r\n" +
                    "}")));

        SymBotRSAAuth auth = new SymBotRSAAuth(config);
        auth.setSessionToken("eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJ0ZXN0LWJvdCIsImlzcyI6InN5bXBob255Iiwic2Vzc2lvbklkIjoiZmRiOTAxMmQzOTgwMGE3NzNkMTJjYWFmZGY5MjU4ZjZjOWEyMTE2MmYyZDU1ODQ3M2Y5ZDU5MDUyNjA0Mjg1ZjU0MWM5Yzg0Mzc5YTE0MjZmODNiZmZkZTljYmQ5NjRjMDAwMDAxNmRmMjMyODIwNTAwMDEzZmYwMDAwMDAxZTgiLCJ1c2VySWQiOiIzNTE3NzUwMDE0MTIwNzIifQ.DlQ_-sAqZLlAcVTr7t_PaYt_Muq_P82yYrtbEEZWMpHMl-7qCciwfi3uXns7oRbc1uvOrhQd603VKQJzQxaZBZBVlUPS-2ysH0tBpCS57ocTS6ZwtQwPLCZYdT-EZ70EzQ95kG6P5TrLENH6UveohgeDdmyzSPOEiwyEUjjmzaXFE8Tu0R3xQDwl-BKbsyUAAgd1X7T0cUDC3WIDl9xaTvyxavep4ZJnZJl4qPc1Tan0yU7JrxtXeD8uwNYlKLudT3UVxduFPMQP_2jyj5Laa-YWGKvRtXkcy2d3hzf4ll1l1wVnyJc1e6hW2EnRlff_Nxge-QCJMcZ_ALrpOUtAyQ");
        symBotClient = SymBotClient.initBot(config, auth);
    }

    protected static StubMapping stubGet(String url, String returnedJsonResponse) {
        return stubFor(get(urlEqualTo(url))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody(returnedJsonResponse))
        );
    }

    protected static StubMapping stubDelete(String url, String returnedJsonResponse) {
        return stubFor(delete(urlEqualTo(url))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody(returnedJsonResponse))
        );
    }

    public static StubMapping stubPost(String url, String returnedJsonResponse) {
        return stubPost(url,returnedJsonResponse, 200);
    }

    public static StubMapping stubPost(String url, String returnedJsonResponse, int status) {
        return stubFor(post(urlEqualTo(url))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(status)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody(returnedJsonResponse))
        );
    }
}
