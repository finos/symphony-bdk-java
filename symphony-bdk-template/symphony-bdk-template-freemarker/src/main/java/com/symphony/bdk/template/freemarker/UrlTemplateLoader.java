package com.symphony.bdk.template.freemarker;

import freemarker.cache.URLTemplateLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Internal class used when instantiating a {@link FreeMarkerTemplate} with {@link FreeMarkerEngine#newTemplateFromUrl(String)}
 */
class UrlTemplateLoader extends URLTemplateLoader {

  private String baseUrl;

  public UrlTemplateLoader(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  protected URL getURL(String s) {
    try {
      URL url = new URL(baseUrl + "/" + s);
      if (resourceCanBeAccessed(url)) {
        return url;
      }
    } catch (MalformedURLException e) {
    }
    return null;
  }

  private boolean resourceCanBeAccessed(URL url) {
    try (InputStream ignored = url.openStream()) {
    } catch (IOException e) {
      return false;
    }
    return true;
  }
}
