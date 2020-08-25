package com.symphony.bdk.core.test;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.jersey2.ApiClientBuilderJersey2;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import java.util.function.Consumer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * MockServer helper, only for testing purpose.
 *
 * @see <a href="https://www.mock-server.com/">MockServer</a>
 */
public class BdkMockServer {

  private ClientAndServer mockServer;
  private MockServerClient mockServerClient;

  public BdkMockServer() {
    // nothing to be done here
  }

  public void start() {
    this.mockServer = startClientAndServer(10000);
    this.mockServerClient = new MockServerClient("localhost", this.mockServer.getPort());
  }

  public void stop() {
    this.mockServer.stop();
    this.mockServerClient.stop();
  }

  public ApiClient newApiClient(String contextPath) {
    return new ApiClientBuilderJersey2()
        .basePath("http://localhost:" + this.mockServer.getPort() + contextPath)
        .buildClient();
  }

  public void onPost(String path, Consumer<HttpResponse> resModifier) {
    this.onRequest("POST", path, resModifier);
  }

  public void onGet(String path, Consumer<HttpResponse> resModifier) {
    this.onRequest("GET", path, resModifier);
  }

  public void onRequest(String method, String path, Consumer<HttpResponse> resModifier) {

    final HttpResponse httpResponse = response()
        .withContentType(MediaType.APPLICATION_JSON_UTF_8)
        .withStatusCode(200);

    resModifier.accept(httpResponse);

    this.mockServerClient
        .when(request().withMethod(method).withPath(path))
        .respond(httpResponse);
  }
}
