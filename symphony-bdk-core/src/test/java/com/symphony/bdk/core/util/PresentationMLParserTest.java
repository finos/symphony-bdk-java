package com.symphony.bdk.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.service.message.exception.PresentationMLParserException;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;

import org.junit.jupiter.api.Test;

public class PresentationMLParserTest {

  @Test
  void getMessageFromPresentationMLTest() throws PresentationMLParserException {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> \n"
        + "  <a href=\"http://www.symphony.com\">This is a link to Symphony's Website</a> \n"
        + "</div>";

    String content = PresentationMLParser.getTextContent(presentationML);

    assertEquals(content, "This is a link to Symphony's Website");
  }

  @Test
  void getMessageFromPresentationMLNotTrimTest() throws PresentationMLParserException {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> \n"
        + "  <a href=\"http://www.symphony.com\">This is a link to Symphony's Website</a> \n"
        + "</div>";

    String content = PresentationMLParser.getTextContent(presentationML, false);

    assertNotEquals(content, "This is a link to Symphony's Website");
    assertEquals(content.trim(), "This is a link to Symphony's Website");
  }

  @Test
  void getMessageFromPresentationMLWithEscapedContentTest() throws PresentationMLParserException {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">&nbsp;</div>";
    String content = PresentationMLParser.getTextContent(presentationML, false);

    assertNotEquals(content, "&#160;");
  }

  @Test
  void getMessageFromPresentationMLFailedToParse() {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> \n"
        + "  <a href=\"http://www.symphony.com\">This is a link to Symphony's Website<a> \n"
        + "</div>";

    assertThrows(PresentationMLParserException.class, () -> PresentationMLParser.getTextContent(presentationML));
  }

  @Test
  void getMessageFromEmptyPresentationMLFailed() {
    String presentationML = "";

    assertThrows(PresentationMLParserException.class, () -> PresentationMLParser.getTextContent(presentationML));
  }

  @Test
  void getMessageFromInvalidPresentationMLFailed() {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">";

    assertThrows(PresentationMLParserException.class, () -> PresentationMLParser.getTextContent(presentationML));
  }
}
