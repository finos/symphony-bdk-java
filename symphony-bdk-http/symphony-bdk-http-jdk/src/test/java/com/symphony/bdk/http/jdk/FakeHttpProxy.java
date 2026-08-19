package com.symphony.bdk.http.jdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Minimal raw-socket forward HTTP proxy used to test {@link ApiClientBuilderJdk}'s proxy support (D9) without
 * pulling in a third-party proxy test double. For plain HTTP forward-proxying, the client sends an
 * absolute-URI request line directly to the proxy and the proxy is free to answer on behalf of the origin
 * server; this fake never actually forwards anywhere, it just inspects the request line/headers it received
 * and answers with a canned response, optionally gating on {@code Proxy-Authorization}.
 */
class FakeHttpProxy implements AutoCloseable {

  private final ServerSocket serverSocket;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final boolean requireAuth;
  private final AtomicInteger requestCount = new AtomicInteger();

  private volatile String lastRequestLine;
  private volatile String lastProxyAuthorizationHeader;

  FakeHttpProxy(boolean requireAuth) throws IOException {
    this.requireAuth = requireAuth;
    this.serverSocket = new ServerSocket(0);
    this.executor.submit(this::acceptLoop);
  }

  int getPort() {
    return this.serverSocket.getLocalPort();
  }

  int getRequestCount() {
    return this.requestCount.get();
  }

  String getLastRequestLine() {
    return this.lastRequestLine;
  }

  String getLastProxyAuthorizationHeader() {
    return this.lastProxyAuthorizationHeader;
  }

  private void acceptLoop() {
    while (!this.serverSocket.isClosed()) {
      try (Socket socket = this.serverSocket.accept()) {
        this.handle(socket);
      } catch (IOException e) {
        return;
      }
    }
  }

  private void handle(Socket socket) throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
    this.lastRequestLine = reader.readLine();

    String line;
    String authHeader = null;
    while ((line = reader.readLine()) != null && !line.isEmpty()) {
      if (line.regionMatches(true, 0, "Proxy-Authorization:", 0, "Proxy-Authorization:".length())) {
        authHeader = line.substring(line.indexOf(':') + 1).trim();
      }
    }
    this.lastProxyAuthorizationHeader = authHeader;
    this.requestCount.incrementAndGet();

    OutputStream out = socket.getOutputStream();
    if (this.requireAuth && authHeader == null) {
      String response = "HTTP/1.1 407 Proxy Authentication Required\r\n"
          + "Proxy-Authenticate: Basic realm=\"fake-proxy\"\r\n"
          + "Content-Length: 0\r\n"
          + "Connection: close\r\n\r\n";
      out.write(response.getBytes(StandardCharsets.US_ASCII));
    } else {
      String body = "{}";
      String response = "HTTP/1.1 200 OK\r\n"
          + "Content-Type: application/json\r\n"
          + "Content-Length: " + body.length() + "\r\n"
          + "Connection: close\r\n\r\n"
          + body;
      out.write(response.getBytes(StandardCharsets.US_ASCII));
    }
    out.flush();
  }

  @Override
  public void close() {
    this.executor.shutdownNow();
    try {
      this.serverSocket.close();
    } catch (IOException ignored) {
      // best-effort cleanup
    }
  }
}
