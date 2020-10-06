package utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageUtilsTest {

  @Test
  public void escapeTextTest() {
    String rawText = "This is a test &<>'\"$#()=;\\.`%*[]{}";
    String convertedText = MessageUtils.escapeText(rawText);
    String expectedResult = "This is a test &amp;&lt;&gt;&apos;&quot;&#36;&#35;&#40;&#41;&#61;&#59;&#92;&#46;&#96;&#37;&#42;&#91;&#93;&#123;&#125;";
    assertEquals(expectedResult, convertedText);
  }

  @Test
  public void escapeStreamIdTest() {
    String rawStreamId = "XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==";
    String convertedStreamId = MessageUtils.escapeStreamId(rawStreamId);
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ", convertedStreamId);
  }
}
