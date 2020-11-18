package com.symphony.bdk.bot.sdk.lib.restclient;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.forbidden;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.symphony.bdk.bot.sdk.lib.restclient.model.RestResponse;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import model.AgentInfo;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class RestClientImplTest {

  private final String commonBody = "{"
      + "\"ipAddress\":\"10.150.192.51\","
      + "\"hostname\":\"ip-10-150-192-51.symphony.comec2.internal\","
      + "\"serverFqdn\":\"https://eis-dev10-agent2.symlabs.info/\","
      + "\"version\":\"Agent-2.57.2-Linux-4.14.177-139.254.amzn2.x86_64\","
      + "\"url\":\"https://eis-dev10.symphony.com/agent\","
      + "\"commitId\":\"254dcb96c31c43d3719ba8843f1fdf0872f70fe2\","
      + "\"onPrem\":true"
      + "}";

  private final String commonTypeHeader = "Content-Type";
  private final String commonValueHeader = "application/json";

  private Map<String, String> expectedHeader;

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(7443);

  @Rule
  public WireMockClassRule instanceRule = wireMockRule;

  private RestClientImpl restClient;

  @Before
  public void initRestClient() {
    final CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
        .slidingWindowSize(20)
        .waitDurationInOpenState(Duration.ofSeconds(30))
        .recordExceptions(RestClientConnectionException.class)
        .build();

    assertNotNull(cbConfig);

    final BulkheadConfig bhConfig = BulkheadConfig.custom()
        .maxConcurrentCalls(30)
        .maxWaitDuration(Duration.ofMillis(500))
        .build();

    assertNotNull(bhConfig);

    this.restClient = new RestClientImpl(new RestTemplate(), cbConfig, bhConfig);
    assertNotNull(this.restClient);

    this.expectedHeader = new HashMap<>();
    this.expectedHeader.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
  }

  @AfterClass
  public static void tearDown(){
    wireMockRule.stop();
  }

  // getRequest with header
  @Test
  public void testGetRequestWithHeaderSuccess(){
    this.myStubForGetOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithHeader();
    assertNotNull(request);
    assertEquals(200, request.getStatus());

    this.verifyContent(request, true);
  }

  @Test
  public void testGetRequestWithHeaderFailure400() {
    this.myStubForGetFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithHeader();
    assertNotNull(request);

    assertEquals(400, request.getStatus());
  }

  @Test
  public void testGetRequestWithHeaderFailure401() {
    this.myStubForGetFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithHeader();
    assertNotNull(request);

    assertEquals(401, request.getStatus());
  }

  @Test
  public void testGetRequestWithHeaderFailure403() {
    this.myStubForGetFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithHeader();
    assertNotNull(request);

    assertEquals(403, request.getStatus());
  }

  @Test
  public void testGetRequestWithHeaderFailure500() {
    this.myStubForGetFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithHeader();
    assertNotNull(request);

    assertEquals(500, request.getStatus());
  }
  // End getRequest with header

  // getRequest without header
  @Test
  public void testGetRequestWithoutHeaderSuccess(){
    this.myStubForGetOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithoutHeader();
    assertEquals(200, request.getStatus());

    this.verifyContent(request, false);
  }

  @Test
  public void testGetRequestWithoutHeaderFailure400() {
    this.myStubForGetFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithoutHeader();
    assertEquals(400, request.getStatus());
  }

  @Test
  public void testGetRequestWithoutHeaderFailure401() {
    this.myStubForGetFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithoutHeader();
    assertEquals(401, request.getStatus());
  }

  @Test
  public void testGetRequestWithoutHeaderFailure403() {
    this.myStubForGetFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithoutHeader();
    assertEquals(403, request.getStatus());
  }

  @Test
  public void testGetRequestWithoutHeaderFailure500() {
    this.myStubForGetFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseGetWithoutHeader();
    assertEquals(500, request.getStatus());
  }
  // End getRequest without header

  // postRequest without header
  @Test
  public void testPostRequestWithoutHeaderSuccess() {
    this.myStubForPostOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithoutHeader();
    assertNotNull(request);

    assertEquals(200, request.getStatus());

    this.verifyContent(request, false);
  }

  @Test
  public void testPostRequestWithoutHeaderFailure400() {
    this.myStubForPostFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithoutHeader();
    assertNotNull(request);

    assertEquals(400, request.getStatus());
  }

  @Test
  public void testPostRequestWithoutHeaderFailure401() {
    this.myStubForPostFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithoutHeader();
    assertNotNull(request);

    assertEquals(401, request.getStatus());
  }

  @Test
  public void testPostRequestWithoutHeaderFailure403() {
    this.myStubForPostFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithoutHeader();
    assertNotNull(request);

    assertEquals(403, request.getStatus());
  }

  @Test
  public void testPostRequestWithoutHeaderFailure500() {
    this.myStubForPostFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithoutHeader();
    assertNotNull(request);

    assertEquals(500, request.getStatus());
  }
  // End postRequest without header

  // postRequest with header
  @Test
  public void testPostRequestWithHeaderSuccess() {
    this.myStubForPostOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithHeader();
    assertNotNull(request);

    this.verifyContent(request, true);
  }

  @Test
  public void testPostRequestWithHeaderFailure400() {
    this.myStubForPostFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithHeader();
    assertNotNull(request);
    assertEquals(400, request.getStatus());
  }

  @Test
  public void testPostRequestWithHeaderFailure401() {
    this.myStubForPostFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithHeader();
    assertNotNull(request);
    assertEquals(401, request.getStatus());
  }

  @Test
  public void testPostRequestWithHeaderFailure403() {
    this.myStubForPostFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithHeader();
    assertNotNull(request);
    assertEquals(403, request.getStatus());
  }

  @Test
  public void testPostRequestWithHeaderFailure500() {
    this.myStubForPostFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePostWithHeader();
    assertNotNull(request);
    assertEquals(500, request.getStatus());
  }
  // End postRequest with header

  // putRequest without header
  @Test
  public void testPutRequestWithoutHeaderSuccess() {
    this.myStubForPutOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithoutHeader();
    assertNotNull(request);

    assertEquals(200, request.getStatus());

    this.verifyContent(request, true);
  }

  @Test
  public void testPutRequestWithoutHeaderFailure400() {
    this.myStubForPutFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithoutHeader();
    assertNotNull(request);

    assertEquals(400, request.getStatus());
  }

  @Test
  public void testPutRequestWithoutHeaderFailure401() {
    this.myStubForPutFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithoutHeader();
    assertNotNull(request);

    assertEquals(401, request.getStatus());
  }

  @Test
  public void testPutRequestWithoutHeaderFailure403() {
    this.myStubForPutFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithoutHeader();
    assertNotNull(request);

    assertEquals(403, request.getStatus());
  }

  @Test
  public void testPutRequestWithoutHeaderFailure500() {
    this.myStubForPutFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithoutHeader();
    assertNotNull(request);

    assertEquals(500, request.getStatus());
  }
  // End putRequest without header

  // putRequest with header
  @Test
  public void testPutRequestWithHeaderSuccess() {
    myStubForPutOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithHeader();
    assertNotNull(request);

    this.verifyContent(request, true);
  }

  @Test
  public void testPutRequestWithHeaderFailure400() {
    this.myStubForPutFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithHeader();
    assertNotNull(request);
    assertEquals(400, request.getStatus());
  }

  @Test
  public void testPutRequestWithHeaderFailure401() {
    this.myStubForPutFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithHeader();
    assertNotNull(request);
    assertEquals(401, request.getStatus());
  }

  @Test
  public void testPutRequestWithHeaderFailure403() {
    this.myStubForPutFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithHeader();
    assertNotNull(request);
    assertEquals(403, request.getStatus());
  }

  @Test
  public void testPutRequestWithHeaderFailure500() {
    this.myStubForPutFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponsePutWithHeader();
    assertNotNull(request);
    assertEquals(500, request.getStatus());
  }
  // End putRequest with header

  // deleteRequest without header
  @Test
  public void testDeleteRequestWithoutHeaderSuccess() {
    this.myStubForDeleteOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithoutHeader();
    assertNotNull(request);

    assertEquals(200, request.getStatus());

    this.verifyContent(request, false);
  }

  @Test
  public void testDeleteRequestWithoutHeaderFailure400() {
    this.myStubForDeleteFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithoutHeader();
    assertNotNull(request);

    assertEquals(400, request.getStatus());
  }

  @Test
  public void testDeleteRequestWithoutHeaderFailure401() {
    this.myStubForDeleteFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithoutHeader();
    assertNotNull(request);

    assertEquals(401, request.getStatus());
  }

  @Test
  public void testDeleteRequestWithoutHeaderFailure403() {
    this.myStubForDeleteFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithoutHeader();
    assertNotNull(request);

    assertEquals(403, request.getStatus());
  }

  @Test
  public void testDeleteRequestWithoutHeaderFailure500() {
    this.myStubForDeleteFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithoutHeader();
    assertNotNull(request);
    assertEquals(500, request.getStatus());
  }
  // End deleteRequest without header

  // deleteRequest with header
  @Test
  public void testDeleteRequestWithHeaderSuccess() {
    this.myStubForDeleteOk();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithHeader();
    assertNotNull(request);
    this.verifyContent(request, true);
  }

  @Test
  public void testDeleteRequestWithHeaderFailure400() {
    this.myStubForDeleteFailure400();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithHeader();
    assertNotNull(request);
    assertEquals(400, request.getStatus());
  }

  @Test
  public void testDeleteRequestWithHeaderFailure401() {
    this.myStubForDeleteFailure401();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithHeader();
    assertNotNull(request);
    assertEquals(401, request.getStatus());
  }

  @Test
  public void testDeleteRequestWithHeaderFailure403() {
    this.myStubForDeleteFailure403();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithHeader();
    assertNotNull(request);
    assertEquals(403, request.getStatus());
  }

  @Test
  public void testDeleteRequestWithHeaderFailure500() {
    this.myStubForDeleteFailure500();

    final RestResponse<AgentInfo> request = getAgentInfoRestResponseDeleteWithHeader();
    assertNotNull(request);
    assertEquals(500, request.getStatus());
  }
  // End deleteRequest with header

  ///////////////////// Private methods
  private RestResponse<AgentInfo> getAgentInfoRestResponseGetWithoutHeader() {
    final RestResponse<AgentInfo> request =
        this.restClient.getRequest("http://localhost:7443/url/path", AgentInfo.class);
    assertNotNull(request);
    return request;
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponsePostWithoutHeader() {
    return this.restClient.postRequest("http://localhost:7443/url/path", this.commonBody, AgentInfo.class);
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponsePostWithHeader() {
    return this.restClient.postRequest("http://localhost:7443/url/path", this.commonBody, this.expectedHeader,
        AgentInfo.class);
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponsePutWithoutHeader() {
    return this.restClient.putRequest("http://localhost:7443/url/path", this.commonBody, AgentInfo.class);
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponseGetWithHeader() {
    return this.restClient.getRequest("http://localhost:7443/url/path", this.expectedHeader, AgentInfo.class);
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponseDeleteWithoutHeader() {
    return this.restClient.deleteRequest("http://localhost:7443/url/path", AgentInfo.class);
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponsePutWithHeader() {
    return this.restClient.putRequest("http://localhost:7443/url/path", this.commonBody, this.expectedHeader,
        AgentInfo.class);
  }

  private RestResponse<AgentInfo> getAgentInfoRestResponseDeleteWithHeader() {
    return this.restClient.deleteRequest("http://localhost:7443/url/path", this.expectedHeader,
        AgentInfo.class);
  }

  private void verifyBody(final AgentInfo body) {
    assertEquals("10.150.192.51", body.getIpAddress());
    assertEquals("ip-10-150-192-51.symphony.comec2.internal", body.getHostname());
    assertEquals("https://eis-dev10-agent2.symlabs.info/", body.getServerFqdn());
    assertEquals("Agent-2.57.2-Linux-4.14.177-139.254.amzn2.x86_64", body.getVersion());
    assertEquals("https://eis-dev10.symphony.com/agent", body.getUrl());
    assertTrue(body.isOnPrem());
  }

  private void verifyHeaders(RestResponse<AgentInfo> request) {
    final Map<String, String> headers = request.getHeaders();
    assertNotNull(headers);
    assertEquals(this.commonValueHeader, headers.get(this.commonTypeHeader));
  }

  private void verifyContent(final RestResponse<AgentInfo> request, final boolean isHeaderPresent){
    if(isHeaderPresent){
      this.verifyHeaders(request);
    }
    final AgentInfo body = request.getBody();
    assertNotNull(body);
    this.verifyBody(body);
  }

  private void myStubForGetOk() {
    stubFor(get(urlEqualTo("/url/path"))
        .willReturn(okJson(this.commonBody)));
  }

  private void myStubForGetFailure400(){
    stubFor(get(urlEqualTo("/url/path"))
        .willReturn(myFailure(400)));
  }

  private void myStubForGetFailure401(){
    stubFor(get(urlEqualTo("/url/path"))
        .willReturn(myFailure(401)));
  }

  private void myStubForGetFailure403(){
    stubFor(get(urlEqualTo("/url/path"))
        .willReturn(myFailure(403)));
  }

  private void myStubForGetFailure500(){
    stubFor(get(urlEqualTo("/url/path"))
        .willReturn(myFailure(500)));
  }

  private void myStubForPostOk() {
    stubFor(post(urlEqualTo("/url/path"))
        .willReturn(okJson(this.commonBody)));
  }

  private void myStubForPostFailure400(){
    stubFor(post(urlEqualTo("/url/path"))
        .willReturn(myFailure(400)));
  }

  private void myStubForPostFailure401(){
    stubFor(post(urlEqualTo("/url/path"))
        .willReturn(myFailure(401)));
  }

  private void myStubForPostFailure403(){
    stubFor(post(urlEqualTo("/url/path"))
        .willReturn(myFailure(403)));
  }

  private void myStubForPostFailure500(){
    stubFor(post(urlEqualTo("/url/path"))
        .willReturn(myFailure(500)));
  }

  private void myStubForPutOk() {
    stubFor(put(urlEqualTo("/url/path"))
        .willReturn(okJson(this.commonBody)));
  }

  private void myStubForPutFailure400(){
    stubFor(put(urlEqualTo("/url/path"))
        .willReturn(myFailure(400)));
  }

  private void myStubForPutFailure401(){
    stubFor(put(urlEqualTo("/url/path"))
        .willReturn(myFailure(401)));
  }

  private void myStubForPutFailure403(){
    stubFor(put(urlEqualTo("/url/path"))
        .willReturn(myFailure(403)));
  }

  private void myStubForPutFailure500(){
    stubFor(put(urlEqualTo("/url/path"))
        .willReturn(myFailure(500)));
  }

  private void myStubForDeleteOk() {
    stubFor(delete(urlEqualTo("/url/path"))
        .willReturn(okJson(this.commonBody)));
  }

  private void myStubForDeleteFailure400(){
    stubFor(delete(urlEqualTo("/url/path"))
        .willReturn(myFailure(400)));
  }

  private void myStubForDeleteFailure401(){
    stubFor(delete(urlEqualTo("/url/path"))
        .willReturn(myFailure(401)));
  }

  private void myStubForDeleteFailure403(){
    stubFor(delete(urlEqualTo("/url/path"))
        .willReturn(myFailure(403)));
  }

  private void myStubForDeleteFailure500(){
    stubFor(delete(urlEqualTo("/url/path"))
        .willReturn(myFailure(500)));
  }

  private ResponseDefinitionBuilder myBadRequest(){
    return badRequest().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("{}");
  }

  private ResponseDefinitionBuilder myUnauthorized(){
    return unauthorized().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("{}");
  }

  private ResponseDefinitionBuilder myForbidden(){
    return forbidden().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("{}");
  }

  private ResponseDefinitionBuilder myServerError(){
    return serverError().withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withBody("{}");
  }

  private ResponseDefinitionBuilder myFailure(int status){
    switch (status){
      case 400: return this.myBadRequest();
      case 401: return this.myUnauthorized();
      case 403: return this.myForbidden();
      case 500: return this.myServerError();
      default: return null;
    }
  }
}
