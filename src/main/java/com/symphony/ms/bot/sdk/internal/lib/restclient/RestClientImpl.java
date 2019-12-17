package com.symphony.ms.bot.sdk.internal.lib.restclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.symphony.ms.bot.sdk.internal.lib.restclient.model.RestResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * Spring RestTemplate-based implementation of the {@link RestClient}
 *
 * @author Marcus Secato
 *
 */
public class RestClientImpl implements RestClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RestClientImpl.class);

  private RestTemplate restTemplate;

  private CircuitBreakerConfig cbConfig;

  private CircuitBreakerRegistry cbRegistry;

  private BulkheadConfig bhConfig;

  private BulkheadRegistry bhRegistry;

  public RestClientImpl(RestTemplate restTemplate,
      CircuitBreakerConfig cbConfig, BulkheadConfig bhConfig) {
    this.restTemplate = restTemplate;
    this.cbConfig = cbConfig;
    this.bhConfig = bhConfig;

    cbRegistry = CircuitBreakerRegistry.of(cbConfig);
    bhRegistry = BulkheadRegistry.of(bhConfig);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> RestResponse<T> getRequest(String url, Class<T> clazz) {
    return doRequest(url, HttpMethod.GET, null, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> RestResponse<T> getRequest(String url,
      Map<String, String> headers, Class<T> clazz) {
    return doRequest(url, HttpMethod.GET, headers, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T, U> RestResponse<T> postRequest(String url, U body,
      Class<T> clazz) {
    return doRequestWithBody(url, HttpMethod.POST, null, body, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T, U> RestResponse<T> postRequest(String url, U body,
      Map<String, String> headers, Class<T> clazz) {
    return doRequestWithBody(url, HttpMethod.POST, headers, body, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T, U> RestResponse<T> putRequest(String url, U body,
      Class<T> clazz) {
    return doRequestWithBody(url, HttpMethod.PUT, null, body, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T, U> RestResponse<T> putRequest(String url, U body,
      Map<String, String> headers, Class<T> clazz) {
    return doRequestWithBody(url, HttpMethod.PUT, headers, body, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> RestResponse<T> deleteRequest(String url, Class<T> clazz) {
    return doRequest(url, HttpMethod.DELETE, null, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> RestResponse<T> deleteRequest(String url,
      Map<String, String> headers, Class<T> clazz) {
    return doRequest(url, HttpMethod.DELETE, headers, clazz);
  }

  private <T> RestResponse<T> doRequest(String url, HttpMethod httpMethod,
     Map<String, String> headers, Class<T> clazz) {
    HttpEntity<?> httpEntity = new HttpEntity<>(convertHeaders(headers));

    return applyResourceControl(url, httpMethod, httpEntity, clazz);
  }

  private <T, U> RestResponse<T> doRequestWithBody(String url,
      HttpMethod httpMethod, Map<String, String> headers, U body, Class<T> clazz) {
    HttpEntity<?> httpEntity = new HttpEntity<>(body, convertHeaders(headers));

    return applyResourceControl(url, httpMethod, httpEntity, clazz);
  }

  /**
   * Applies circuit-breaker and bulkhead controls to improve resources usage
   * when communicating with external systems.
   */
  private <T> RestResponse<T> applyResourceControl(String url,
      HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> clazz) {

    String registryKey = getBaseUrl(url);

    CircuitBreaker cb = cbRegistry.circuitBreaker(registryKey, cbConfig);
    Supplier<RestResponse<T>> cbSupplier = CircuitBreaker.decorateSupplier(cb,
        () -> internalDoRequest(url, httpMethod, httpEntity, clazz));

    Bulkhead bh = bhRegistry.bulkhead(registryKey, bhConfig);
    Supplier<RestResponse<T>> bhSupplier = Bulkhead.decorateSupplier(
        bh, cbSupplier);

    try {
      return bhSupplier.get();
    } catch (CallNotPermittedException cnpe) {
      LOGGER.warn("Circuit breaker is now OPEN. Rejecting request {}", url);
      throw new RestClientConnectionException();
    }
  }

  private <T> RestResponse<T> internalDoRequest(String url,
      HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> clazz) {
    LOGGER.debug("Performing request {} {}", httpMethod, url);

    RestResponse<T> response = null;
    try {
      ResponseEntity<T> re = restTemplate.exchange(
          url, httpMethod, httpEntity, clazz);

      T body = re.hasBody() ? re.getBody() : null;
      response = new RestResponse<>(body,
          re.getHeaders().toSingleValueMap(), re.getStatusCodeValue());
    } catch (HttpClientErrorException | HttpServerErrorException reqEx) {
      LOGGER.debug("Unsuccessful response executing request {} {}\n",
          httpMethod, url, reqEx);
      response = new RestResponse<>(
          reqEx.getResponseHeaders().toSingleValueMap(),
          reqEx.getRawStatusCode());
    } catch (RestClientException rce) {
      LOGGER.error("Unexpected error executing request {} {}\n{}",
          httpMethod, url, rce);
      throw new RestClientConnectionException();
    }

    return response;
  }

  private MultiValueMap<String, String> convertHeaders(
      Map<String, String> headers) {
    if (headers != null) {
      MultiValueMap<String, String> resttemplateHeaders =
          new LinkedMultiValueMap<>();

      headers.entrySet().stream()
      .forEach(e -> resttemplateHeaders.add(e.getKey(), e.getValue()));
      return resttemplateHeaders;
    }

    return null;
  }

  private String getBaseUrl(String url) {
    try {
      URI uri = new URI(url);
      return uri.getHost();
    } catch (URISyntaxException use) {
      LOGGER.debug("Could not parse URL: {}", url);
      return url;
    }

  }

}
