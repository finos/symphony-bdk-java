package com.symphony.ms.songwriter.internal.lib.templating;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateServiceImpl implements TemplateService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);

  // Caching to mitigate expensive template generation
  private Map<String, Template> templateMap = new HashMap<>();

  private Configuration freemarkerConfig;

  private JsonMapper jsonMapper;

  public TemplateServiceImpl(Configuration freemarkerConfig, JsonMapper jsonMapper) {
    this.freemarkerConfig = freemarkerConfig;
    this.jsonMapper = jsonMapper;
  }

  @Override
  public String processTemplateFile(String templateFile, Object data) {
    try {
      Template template = freemarkerConfig.getTemplate(templateFile, "UTF-8");
      StringWriter sw = new StringWriter();
      template.process(jsonMapper.objectToMap(data), sw);

      return sw.getBuffer().toString();
    } catch (IOException ioe) {
      LOGGER.error("Could not find template file: {}", templateFile);
      throw new TemplateFileNotFoundException();
    } catch (TemplateException te) {
      LOGGER.error("Failed to process template file: {}\n{}",templateFile, te);
      throw new TemplateProcessingException();
    }
  }

  @Override
  public String processTemplateString(String templateString, Object data) {
    try {
      String templateHash = md5(templateString);
      Template template = null;

      if (!templateMap.containsKey(templateHash)) {
        template = new Template(templateHash,
            new StringReader(templateString), freemarkerConfig);
        templateMap.put(templateHash, template);
      } else {
        template = templateMap.get(templateHash);
      }

      StringWriter sw = new StringWriter();
      template.process(jsonMapper.objectToMap(data), sw);

      return sw.getBuffer().toString();
    } catch (TemplateException te) {
      LOGGER.error("Failed to process template string: {}\n{}", templateString, te);
      throw new TemplateProcessingException();
    } catch (IOException | NoSuchAlgorithmException e) {
      LOGGER.error("Error processing string template:\n{}", e);
      throw new TemplateProcessingException();
    }
  }

  private String md5(String templateString) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(templateString.getBytes());
    byte[] digest = md.digest();
    return DatatypeConverter
      .printHexBinary(digest).toUpperCase();
  }

}
