package com.symphony.bdk.core.util;

import com.symphony.bdk.core.util.exception.PresentationMLParserException;

import lombok.SneakyThrows;
import org.apiguardian.api.API;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Helper class for parsing the PresentationML to text content.
 */
@API(status = API.Status.STABLE)
public class PresentationMLParser {

  private static final DocumentBuilder builder = initBuilder();

  /**
   * Get message text content from PresentationML
   *
   * @param presentationML the PresentationML to be parsed
   * @return the message text content extracted from the given PresentationML
   */
  public static String getMessageTextContent(String presentationML) {
    try {
      final Document doc = builder.parse(new ByteArrayInputStream(presentationML.getBytes(StandardCharsets.UTF_8)));
      return doc.getChildNodes().item(0).getTextContent();
    } catch (SAXException | IOException e) {
      throw new PresentationMLParserException("Failed to parse the PresentationML", e);
    }
  }

  @SneakyThrows
  private static DocumentBuilder initBuilder() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder();
  }
}
