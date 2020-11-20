package com.symphony.bdk.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.util.exception.PresentationMLParserException;

import org.junit.jupiter.api.Test;

public class PresentationMLParserTest {

  @Test
  void getMessageFromPresentationMLTest() {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> \n"
        + "  <a href=\"http://www.symphony.com\">This is a link to Symphony's Website</a> \n"
        + "</div>";

    String content = PresentationMLParser.getMessageTextContent(presentationML);

    assertEquals(content.trim(), "This is a link to Symphony's Website");
  }

  @Test
  void getMessageFromPresentationMLFailedToParse() {
    String presentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> \n"
        + "  <a href=\"http://www.symphony.com\">This is a link to Symphony's Website<a> \n"
        + "</div>";

    assertThrows(PresentationMLParserException.class, () -> PresentationMLParser.getMessageTextContent(presentationML));
  }

  @Test
  void testInitParser() {
    PresentationMLParser parser = new PresentationMLParser();
    assertNotNull(parser);
  }
}
