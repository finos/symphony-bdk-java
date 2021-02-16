package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.http.api.HttpClient;
import com.symphony.bdk.http.api.util.TypeReference;
import com.symphony.bdk.http.jersey2.ApiClientBuilderJersey2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;

/**
 * This example demonstrates advanced initialization of the {@link com.symphony.bdk.core.SymphonyBdk} entry point.
 */
@Slf4j
public class AdvancedBdkSetupMain {

  public static void main(String[] args) throws Exception {

    final ObjectMapper globalObjectMapper = new ObjectMapper();
    globalObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    globalObjectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    globalObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    globalObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .configFromSymphonyDir("config.yaml")
        .apiClientBuilderProvider(() -> new CustomApiClientBuilder(globalObjectMapper))
        .build();

    final HttpClient http = bdk.http().basePath(bdk.config().getBasePath()).build();
    final V3Health health = http.path("/agent/v3/health/extended").get(new TypeReference<V3Health>() {});

    log.info("Agent's status is {}", health.getStatus());
  }

  @RequiredArgsConstructor
  static class CustomApiClientBuilder extends ApiClientBuilderJersey2 {

    private final ObjectMapper objectMapper;

    @Override
    protected ClientConfig createClientConfig() {
      final ClientConfig clientConfig = super.createClientConfig();
      clientConfig.register(new RequestLoggingFilter());
      return clientConfig;
    }

    @Override
    protected void configureJackson(ClientConfig clientConfig) {
      clientConfig.register(JacksonFeature.class);
      clientConfig.register(new JacksonJaxbJsonProvider(this.objectMapper, JacksonJsonProvider.BASIC_ANNOTATIONS));
    }
  }

  @ConstrainedTo(RuntimeType.CLIENT)
  @PreMatching
  @Priority(2147483647)
  static class RequestLoggingFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) {
      if (log.isDebugEnabled()) {
        log.debug("HTTP {} to {}", requestContext.getMethod(), requestContext.getUri());
      }
    }
  }
}
