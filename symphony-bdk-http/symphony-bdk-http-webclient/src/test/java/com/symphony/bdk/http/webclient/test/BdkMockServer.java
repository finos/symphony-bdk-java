package com.symphony.bdk.http.webclient.test;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.webclient.ApiClientBuilderWebClient;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import java.util.function.Consumer;

public class BdkMockServer {

  private ClientAndServer mockServer;

  public BdkMockServer() {
    // nothing to be done here
  }

  public void start() {
    this.mockServer = startClientAndServer();
  }

  public void stop() {
    this.mockServer.stopAsync();
  }

  public ApiClient newApiClient(String contextPath) {
    return new ApiClientBuilderWebClient()
        .withBasePath("http://localhost:" + this.mockServer.getPort() + contextPath)
        .build();
  }

  public void onPost(String path, Consumer<HttpResponse> resModifier) {
    this.onRequest("POST", path, resModifier);
  }

  public void onGet(String path, Consumer<HttpResponse> resModifier) {
    this.onRequest("GET", path, resModifier);
  }

  public void onDelete(String path, Consumer<HttpResponse> resModifier) {
    this.onRequest("DELETE", path, resModifier);
  }

  public void onRequest(String method, String path, Consumer<HttpResponse> resModifier) {
    this.onRequestWithResponseCode(method, 200, path, resModifier);
  }

  public void onPostFailed(int errorCode, String path, Consumer<HttpResponse> resModifier) {
    this.onRequestWithResponseCode("POST", errorCode, path, resModifier);
  }

  public void onGetFailed(int errorCode, String path, Consumer<HttpResponse> resModifier) {
    this.onRequestWithResponseCode("GET", errorCode, path, resModifier);
  }

  public void onDeleteFailed(int errorCode, String path, Consumer<HttpResponse> resModifier) {
    this.onRequestWithResponseCode("DELETE", errorCode, path, resModifier);
  }

  public void onRequestWithResponseCode(String method, int responseCode, String path, Consumer<HttpResponse> resModifier) {
    final HttpResponse httpResponse = response()
        .withContentType(MediaType.APPLICATION_JSON_UTF_8)
        .withStatusCode(responseCode);

    resModifier.accept(httpResponse);
    this.mockServer
        .when(request().withMethod(method).withPath(path))
        .respond(httpResponse);
  }

  public void onRequestModifierWithResponse(int responseCode, Consumer<HttpRequest> reqModifier, Consumer<HttpResponse> resModifier) {
    final HttpRequest httpRequest = request();
    reqModifier.accept(httpRequest);

    final HttpResponse httpResponse = response()
        .withContentType(MediaType.APPLICATION_JSON_UTF_8)
        .withStatusCode(responseCode);
    resModifier.accept(httpResponse);

    this.mockServer.when(httpRequest).respond(httpResponse);
  }
}
