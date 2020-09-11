package com.symphony.bdk.core.api.invoker.jersey2;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiResponse;
import com.symphony.bdk.core.api.invoker.Pair;
import org.apiguardian.api.API;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jersey2 implementation for the {@link ApiClient} interface called by generated code.
 */
@API(status = API.Status.STABLE)
public class ApiClientJersey2 implements ApiClient {

  protected Client httpClient;
  protected String basePath;
  protected Map<String, String> defaultHeaderMap;
  protected String tempFolderPath;

  public ApiClientJersey2(final Client httpClient, String basePath, Map<String, String> defaultHeaders, String temporaryFolderPath) {
    this.httpClient = httpClient;
    this.basePath = basePath;
    this.defaultHeaderMap = new HashMap<>(defaultHeaders);
    this.tempFolderPath = temporaryFolderPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> ApiResponse<T> invokeAPI(
      final String path,
      final String method,
      final List<Pair> queryParams,
      final Object body,
      final Map<String, String> headerParams,
      final Map<String, String> cookieParams,
      final Map<String, Object> formParams,
      final String accept,
      final String contentType,
      final String[] authNames,
      final GenericType<T> returnType
  ) throws ApiException {

    // Not using `.target(this.basePath).path(path)` below,
    // to support (constant) query string in `path`, e.g. "/posts?draft=1"
    WebTarget target = httpClient.target(this.basePath + path);

    if (queryParams != null) {
      for (Pair queryParam : queryParams) {
        if (queryParam.getValue() != null) {
          target = target.queryParam(queryParam.getName(), escapeString(queryParam.getValue()));
        }
      }
    }

    Invocation.Builder invocationBuilder = target.request().accept(accept);

    for (Entry<String, String> entry : headerParams.entrySet()) {
      String value = entry.getValue();
      if (value != null) {
        invocationBuilder = invocationBuilder.header(entry.getKey(), value);
      }
    }

    for (Entry<String, String> entry : cookieParams.entrySet()) {
      String value = entry.getValue();
      if (value != null) {
        invocationBuilder = invocationBuilder.cookie(entry.getKey(), value);
      }
    }

    for (Entry<String, String> entry : defaultHeaderMap.entrySet()) {
      String key = entry.getKey();
      if (!headerParams.containsKey(key)) {
        String value = entry.getValue();
        if (value != null) {
          invocationBuilder = invocationBuilder.header(key, value);
        }
      }
    }

    Entity<?> entity = (body == null && formParams == null) ? Entity.json("") : this.serialize(body, formParams, contentType);

    Response response = null;

    try {
      if ("GET".equals(method)) {
        response = invocationBuilder.get();
      } else if ("POST".equals(method)) {
        response = invocationBuilder.post(entity);
      } else if ("PUT".equals(method)) {
        response = invocationBuilder.put(entity);
      } else if ("DELETE".equals(method)) {
        response = invocationBuilder.method("DELETE", entity);
      } else if ("PATCH".equals(method)) {
        response = invocationBuilder.method("PATCH", entity);
      } else if ("HEAD".equals(method)) {
        response = invocationBuilder.head();
      } else if ("OPTIONS".equals(method)) {
        response = invocationBuilder.options();
      } else if ("TRACE".equals(method)) {
        response = invocationBuilder.trace();
      } else {
        throw new ApiException(500, "unknown method type " + method);
      }

      int statusCode = response.getStatusInfo().getStatusCode();
      Map<String, List<String>> responseHeaders = buildResponseHeaders(response);

      if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
        return new ApiResponse<>(statusCode, responseHeaders);
      } else if (response.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL) {
        if (returnType == null) {
          return new ApiResponse<>(statusCode, responseHeaders);
        } else {
          return new ApiResponse<>(statusCode, responseHeaders, deserialize(response, returnType));
        }
      } else {
        String message = "error";
        String respBody = null;
        if (response.hasEntity()) {
          try {
            respBody = String.valueOf(response.readEntity(String.class));
            message = respBody;
          } catch (RuntimeException e) {
            // e.printStackTrace();
          }
        }
        throw new ApiException(
            response.getStatus(),
            message,
            buildResponseHeaders(response),
            respBody);
      }
    } finally {
      try {
        response.close();
      } catch (Exception e) {
        // it's not critical, since the response object is local in method invokeAPI; that's fine, just continue
      }
    }
  }

  @Override
  public String getBasePath() {
    return basePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String parameterToString(Object param) {
    if (param == null) {
      return "";
    } else if (param instanceof Collection) {
      StringBuilder b = new StringBuilder();
      for (Object o : (Collection<?>) param) {
        if (b.length() > 0) {
          b.append(',');
        }
        b.append(o);
      }
      return b.toString();
    } else {
      return String.valueOf(param);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    List<Pair> params = new ArrayList<Pair>();

    // preconditions
    if (name == null || name.isEmpty() || value == null) {
      return params;
    }

    Collection<?> valueCollection;
    if (value instanceof Collection) {
      valueCollection = (Collection) value;
    } else {
      params.add(new Pair(name, parameterToString(value)));
      return params;
    }

    if (valueCollection.isEmpty()) {
      return params;
    }

    // get the collection format (default: csv)
    String format = (collectionFormat == null || collectionFormat.isEmpty() ? "csv" : collectionFormat);

    // create the params based on the collection format
    if ("multi".equals(format)) {
      for (Object item : valueCollection) {
        params.add(new Pair(name, parameterToString(item)));
      }

      return params;
    }

    String delimiter = ",";

    switch (format) {
      case "csv":
        delimiter = ",";
        break;
      case "ssv":
        delimiter = " ";
        break;
      case "tsv":
        delimiter = "\t";
        break;
      case "pipes":
        delimiter = "|";
        break;
    }

    StringBuilder sb = new StringBuilder();
    for (Object item : valueCollection) {
      sb.append(delimiter);
      sb.append(parameterToString(item));
    }

    params.add(new Pair(name, sb.substring(1)));

    return params;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String selectHeaderAccept(String[] accepts) {
    if (accepts.length == 0) {
      return null;
    }
    for (String accept : accepts) {
      if (isJsonMime(accept)) {
        return accept;
      }
    }
    return String.join(",", accepts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String selectHeaderContentType(String[] contentTypes) {
    if (contentTypes.length == 0) {
      return "application/json";
    }
    for (String contentType : contentTypes) {
      if (isJsonMime(contentType)) {
        return contentType;
      }
    }
    return contentTypes[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String escapeString(String str) {
    try {
      return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      return str;
    }
  }

  /**
   * Check if the given MIME is a JSON MIME.
   * JSON MIME examples:
   * application/json
   * application/json; charset=UTF8
   * APPLICATION/JSON
   * application/vnd.company+json
   * "* / *" is also default to JSON
   *
   * @param mime MIME
   * @return True if the MIME type is JSON
   */
  protected boolean isJsonMime(String mime) {
    String jsonMime = "(?i)^(application/json|[^;/ \t]+/[^;/ \t]+[+]json)[ \t]*(;.*)?$";
    return mime != null && (mime.matches(jsonMime) || mime.equals("*/*"));
  }

  /**
   * Serialize the given Java object into string entity according the given
   * Content-Type (only JSON is supported for now).
   *
   * @param obj Object
   * @param formParams Form parameters
   * @param contentType Context type
   * @return Entity
   */
  protected Entity<?> serialize(Object obj, Map<String, Object> formParams, String contentType) {
    Entity<?> entity;
    if (contentType.startsWith("multipart/form-data")) {
      MultiPart multiPart = new MultiPart();
      for (Entry<String, Object> param : formParams.entrySet()) {
        if (param.getValue() instanceof File) {
          File file = (File) param.getValue();
          FormDataContentDisposition contentDisp = FormDataContentDisposition.name(param.getKey())
              .fileName(file.getName()).size(file.length()).build();
          multiPart.bodyPart(new FormDataBodyPart(contentDisp, file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        } else {
          FormDataContentDisposition contentDisp = FormDataContentDisposition.name(param.getKey()).build();
          multiPart.bodyPart(new FormDataBodyPart(contentDisp, parameterToString(param.getValue())));
        }
      }
      entity = Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE);
    } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
      Form form = new Form();
      for (Entry<String, Object> param : formParams.entrySet()) {
        form.param(param.getKey(), parameterToString(param.getValue()));
      }
      entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    } else {
      // We let jersey handle the serialization
      entity = Entity.entity(obj, contentType);
    }
    return entity;
  }

  /**
   * Deserialize response body to Java object according to the Content-Type.
   *
   * @param <T> Type
   * @param response Response
   * @param returnType Return type
   * @return Deserialize object
   * @throws ApiException API exception
   */
  @SuppressWarnings("unchecked")
  protected <T> T deserialize(Response response, GenericType<T> returnType) throws ApiException {
    if (response == null || returnType == null) {
      return null;
    }

    if ("byte[]".equals(returnType.toString())) {
      // Handle binary response (byte array).
      return (T) response.readEntity(byte[].class);
    } else if (returnType.getRawType() == File.class) {
      // Handle file downloading.
      return (T) downloadFileFromResponse(response);
    }

    return response.readEntity(returnType);
  }

  /**
   * Download file from the given response.
   *
   * @param response Response
   * @return File
   * @throws ApiException If fail to read file content from response and write to disk
   */
  protected File downloadFileFromResponse(final Response response) throws ApiException {
    try {
      final File file = this.prepareDownloadFile(response);
      Files.copy(response.readEntity(InputStream.class), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      return file;
    } catch (IOException e) {
      throw new ApiException("Unable to download file from response", e);
    }
  }

  protected File prepareDownloadFile(Response response) throws IOException {
    String filename = null;
    String contentDisposition = (String) response.getHeaders().getFirst("Content-Disposition");
    if (contentDisposition != null && !"".equals(contentDisposition)) {
      // Get filename from the Content-Disposition header.
      Pattern pattern = Pattern.compile("filename=['\"]?([^'\"\\s]+)['\"]?");
      Matcher matcher = pattern.matcher(contentDisposition);
      if (matcher.find()) {
        filename = matcher.group(1);
      }
    }

    String prefix;
    String suffix = null;
    if (filename == null) {
      prefix = "download-";
      suffix = "";
    } else {
      int pos = filename.lastIndexOf('.');
      if (pos == -1) {
        prefix = filename + "-";
      } else {
        prefix = filename.substring(0, pos) + "-";
        suffix = filename.substring(pos);
      }
      // File.createTempFile requires the prefix to be at least three characters long
      if (prefix.length() < 3) {
        prefix = "download-";
      }
    }

    if (tempFolderPath == null) {
      return File.createTempFile(prefix, suffix);
    } else {
      return File.createTempFile(prefix, suffix, new File(tempFolderPath));
    }
  }

  protected Map<String, List<String>> buildResponseHeaders(Response response) {
    Map<String, List<String>> responseHeaders = new HashMap<>();
    for (Entry<String, List<Object>> entry : response.getHeaders().entrySet()) {
      List<Object> values = entry.getValue();
      List<String> headers = new ArrayList<>();
      for (Object o : values) {
        headers.add(String.valueOf(o));
      }
      responseHeaders.put(entry.getKey(), headers);
    }
    return responseHeaders;
  }
}
