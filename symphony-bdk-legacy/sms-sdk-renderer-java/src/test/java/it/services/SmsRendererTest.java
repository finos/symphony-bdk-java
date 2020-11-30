package it.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import services.SmsRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SmsRendererTest {

  @Test
  public void renderInBotJSONObjectTest() throws IOException {
    this.testJsonObject(SmsRenderer.SmsTypes.LIST);
    this.testJsonObject(SmsRenderer.SmsTypes.TABLE);
    this.testJsonObject(SmsRenderer.SmsTypes.INFORMATION);
    this.testJsonObject(SmsRenderer.SmsTypes.NOTIFICATION);
    this.testJsonObject(SmsRenderer.SmsTypes.ALERT);
    this.testJsonObject(SmsRenderer.SmsTypes.SIMPLE);
  }

  @Test
  public void renderInBotJSONArrayTest() throws IOException {
    this.testJsonArray(SmsRenderer.SmsTypes.LIST);
    this.testJsonArray(SmsRenderer.SmsTypes.TABLE);
    this.testJsonArray(SmsRenderer.SmsTypes.INFORMATION);
    this.testJsonArray(SmsRenderer.SmsTypes.NOTIFICATION);
    this.testJsonArray(SmsRenderer.SmsTypes.ALERT);
    this.testJsonArray(SmsRenderer.SmsTypes.SIMPLE);
  }

  @Test
  public void renderInBotStringTest() throws IOException {
    this.testString(SmsRenderer.SmsTypes.LIST);
    this.testString(SmsRenderer.SmsTypes.TABLE);
    this.testString(SmsRenderer.SmsTypes.INFORMATION);
    this.testString(SmsRenderer.SmsTypes.NOTIFICATION);
    this.testString(SmsRenderer.SmsTypes.ALERT);
    this.testString(SmsRenderer.SmsTypes.SIMPLE);
  }

  // Private methods
  private void testString(final SmsRenderer.SmsTypes smsTypes) throws IOException {
    assertNotNull(smsTypes);

    final String fileName = smsTypes.getName().toLowerCase() + "Json.json";
    final String jsonMessageContext = this.getStringFromFile("/SmsRenderer/JSONData/"+fileName);

    final String compiledTemplate = this.minifyHTML(SmsRenderer.renderInBot(jsonMessageContext, smsTypes));
    assertNotNull(compiledTemplate);
  }

  private void testJsonObject(final SmsRenderer.SmsTypes smsTypes) throws IOException {
    assertNotNull(smsTypes);

    final String fileName = smsTypes.getName().toLowerCase() + "Json.json";
    final String jsonMessageContext = this.getStringFromFile("/SmsRenderer/JSONData/"+fileName);

    final JSONObject jsonObject = this.getJsonObject(jsonMessageContext);
    assertNotNull(jsonObject);

    final String compiledTemplate = this.minifyHTML(SmsRenderer.renderInBot(jsonObject, smsTypes));
    assertNotNull(compiledTemplate);
  }

  private String getStringFromFile(final String fileName) throws IOException {
    assertNotNull(fileName);

    final String jsonMessageContext = this.removeCarriageReturn(this.readResourceContent(fileName));
    assertNotNull(jsonMessageContext);
    return jsonMessageContext;
  }

  private void testJsonArray(final SmsRenderer.SmsTypes smsTypes) throws IOException {
    assertNotNull(smsTypes);

    final String fileName = smsTypes.getName().toLowerCase() + "JsonArray.json";
    final String jsonMessageContext = this.getStringFromFile("/SmsRenderer/JSONData/"+fileName);

    final JSONArray jsonArray = new JSONArray();
    jsonArray.add(jsonMessageContext);

    final String compiledTemplate = this.minifyHTML(SmsRenderer.renderInBot(jsonArray, smsTypes));
    assertNotNull(compiledTemplate);
  }

  private String readResourceContent(final String path) throws IOException {
    assertNotNull(path);

    final InputStream resourceStream = SmsRendererTest.class.getResourceAsStream(path);
    return IOUtils.toString(resourceStream, StandardCharsets.UTF_8.name());
  }

  private String minifyHTML(final String text) {
    return (text==null)?null : this.removeCarriageReturn(text.replaceAll("\\s{2,}", "").replaceAll("\n", "").replaceAll("\r", ""));
  }

  private String removeCarriageReturn(final String text) {
    return (text==null)?null : text.replaceAll("\n", "").replaceAll("\r", "");
  }

  private JSONObject getJsonObject(final String jsonMessageContext) {
    assertNotNull(jsonMessageContext);

    final JSONParser parser = new JSONParser();
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) parser.parse(jsonMessageContext);
    } catch (final ParseException e) {
      fail(e.getMessage());
    }
    return jsonObject;
  }
}
