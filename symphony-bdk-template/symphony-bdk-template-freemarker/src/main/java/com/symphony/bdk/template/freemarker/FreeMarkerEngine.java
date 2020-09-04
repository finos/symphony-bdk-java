package com.symphony.bdk.template.freemarker;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FilenameUtils;
import org.apiguardian.api.API;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FreeMarker specific implementation of {@link TemplateEngine}. Instantiates {@link FreeMarkerTemplate} objects.
 */
@API(status = API.Status.INTERNAL)
public class FreeMarkerEngine implements TemplateEngine {

  public static final String FREEMARKER_EXTENSION = ".ftl";

  private static Configuration configuration = createConfiguration();
  private static Map<String, String> builtInTemplates = getBuiltInTemplateMap();

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getBuiltInTemplates() {
    return builtInTemplates.keySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newBuiltInTemplate(String template) throws TemplateException {
    if (!builtInTemplates.containsKey(template)) {
      throw new TemplateException("Template " + template + " not found", null);
    }
    return newTemplateFromClasspath(builtInTemplates.get(template));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromFile(String templatePath) throws TemplateException {
    try {
      return getTemplateFromFile(templatePath);
    } catch (IOException e) {
      throw new TemplateException("Unable to open template file", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromClasspath(String templatePath) throws TemplateException {
    try {
      configuration.setClassForTemplateLoading(getClass(), "/");
      return new FreeMarkerTemplate(configuration.getTemplate(templatePath));
    } catch (IOException e) {
      throw new TemplateException("Unable to load template from classpath", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Template newTemplateFromUrl(String url) throws TemplateException {
    try {
      return getTemplateFromUrl(url);
    } catch (IOException e) {
      throw new TemplateException("Unable to load template from url", e);
    }
  }


  private FreeMarkerTemplate getTemplateFromFile(String templatePath) throws IOException {
    String directory = FilenameUtils.getFullPathNoEndSeparator(templatePath);
    String file = FilenameUtils.getName(templatePath);

    configuration.setDirectoryForTemplateLoading(new File(directory));
    return new FreeMarkerTemplate(configuration.getTemplate(file));
  }

  private Template getTemplateFromUrl(String url) throws IOException {
    String baseUrl = FilenameUtils.getFullPathNoEndSeparator(url);
    String file = FilenameUtils.getName(url);

    configuration.setTemplateLoader(new UrlTemplateLoader(baseUrl));
    return new FreeMarkerTemplate(configuration.getTemplate(file));
  }

  private static Configuration createConfiguration() {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
    return cfg;
  }

  private static Map<String, String> getBuiltInTemplateMap() {
    Reflections reflections = new Reflections(FreeMarkerEngine.class.getPackage().getName(), new ResourcesScanner());

    final Set<String> resources = reflections.getResources(n -> n.endsWith(FREEMARKER_EXTENSION));
    return resources.stream().collect(Collectors.toMap(r -> extractTemplateName(r), Function.identity()));
  }

  private static String extractTemplateName(String templatePath) {
    // remove the path part until the last '/' and the FREEMARKER_EXTENSION
    return templatePath.substring(templatePath.lastIndexOf('/') + 1,
        templatePath.length() - FREEMARKER_EXTENSION.length());
  }
}
