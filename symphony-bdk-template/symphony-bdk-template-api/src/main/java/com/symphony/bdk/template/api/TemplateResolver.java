package com.symphony.bdk.template.api;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Class to ease resolution of a {@link Template} based on a {@link TemplateEngine} and a name,
 * which can be a built-in template name, a path to a resource in the classpath,
 * a path to a file on the file system or a URL to a template file.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class TemplateResolver {
  private final TemplateEngine templateEngine;

  /**
   * Constructor which uses the default {@link TemplateEngine} returned by {@link TemplateEngine#getDefaultImplementation()}
   * in order to resolve templates.
   */
  public TemplateResolver() {
    this(TemplateEngine.getDefaultImplementation());
  }

  /**
   * Constructor which uses the {@link TemplateEngine} put in parameter.
   * @param templateEngine the {@link TemplateEngine} instance to use to resolve and create {@link Template} instances.
   */
  public TemplateResolver(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  /**
   * Looks for a template in this order : in the built-in templates, in the classpath, in the filesystem and from a URL.
   * @param template can be the name of a built-in template, a path to a template file in the classpath or in the file system
   *                 or a URL to a template file
   * @return a new {@link Template} instance corresponding to the template parameter
   * @throws TemplateException if no template has been found.
   */
  public Template resolve(String template) throws TemplateException {
    Template resolved = null;
    if (isBuiltInTemplate(template)) {
      resolved = tryFetchBuiltInTemplate(template);
    }

    if (resolved == null) {
      resolved = tryFetchTemplateFromClasspath(template);
    }

    if (resolved == null) {
      resolved = tryFetchTemplateFromFileSystem(template);
    }

    if (resolved == null) {
      resolved = tryFetchTemplateFromUrl(template);
    }

    if (resolved == null) {
      throw new TemplateException("Template " + template + " not found");
    }
    return resolved;
  }

  private boolean isBuiltInTemplate(String template) {
    return this.templateEngine.getBuiltInTemplates().contains(template);
  }

  private Template tryFetchBuiltInTemplate(String template) {
    try {
      return this.templateEngine.newBuiltInTemplate(template);
    } catch (TemplateException e) {
      log.debug("{} is not a built-in template", template);
    }
    return null;
  }

  private Template tryFetchTemplateFromClasspath(String template) {
    try {
      return this.templateEngine.newTemplateFromClasspath(template);
    } catch (TemplateException e) {
      log.debug("{} is not found in classpath", template);
    }
    return null;
  }

  private Template tryFetchTemplateFromFileSystem(String template) {
    try {
      return this.templateEngine.newTemplateFromFile(template);
    } catch (TemplateException e) {
      log.debug("{} is not found on file system", template);
    }
    return null;
  }

  private Template tryFetchTemplateFromUrl(String template) {
    try {
      return this.templateEngine.newTemplateFromUrl(template);
    } catch (TemplateException e) {
      log.debug("{} is not found from URL", template);
    }
    return null;
  }
}
