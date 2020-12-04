package it.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import services.SmsRenderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SmsRendererTest {

  @Test
  public void renderInBotStringTest() throws IOException {
    this.testString(SmsRenderer.SmsTypes.LIST);
    this.testString(SmsRenderer.SmsTypes.TABLE);
    this.testString(SmsRenderer.SmsTypes.INFORMATION);
    this.testString(SmsRenderer.SmsTypes.NOTIFICATION);
    this.testString(SmsRenderer.SmsTypes.ALERT);
    this.testString(SmsRenderer.SmsTypes.SIMPLE);
  }

  @Test
  public void renderInBotJSONObjectTest() throws IOException, ParseException {
    this.testJsonObject(SmsRenderer.SmsTypes.LIST);
    this.testJsonObject(SmsRenderer.SmsTypes.TABLE);
    this.testJsonObject(SmsRenderer.SmsTypes.INFORMATION);
    this.testJsonObject(SmsRenderer.SmsTypes.NOTIFICATION);
    this.testJsonObject(SmsRenderer.SmsTypes.ALERT);
    this.testJsonObject(SmsRenderer.SmsTypes.SIMPLE);
  }

  @Test
  public void renderInBotJSONArrayTest() throws IOException, ParseException {
    this.testJsonArray(SmsRenderer.SmsTypes.LIST);
    this.testJsonArray(SmsRenderer.SmsTypes.TABLE);
    this.testJsonArray(SmsRenderer.SmsTypes.INFORMATION);
    this.testJsonArray(SmsRenderer.SmsTypes.NOTIFICATION);
    this.testJsonArray(SmsRenderer.SmsTypes.ALERT);
    this.testJsonArray(SmsRenderer.SmsTypes.SIMPLE);
  }

  // Private methods
  private void testJsonArray(final SmsRenderer.SmsTypes smsTypes) throws IOException, ParseException {
    assertNotNull(smsTypes);

    final String typeName = smsTypes.getName().toLowerCase();

    String jsonMessageContext = null;
    JSONArray jsonArray = null;

    if (smsTypes != SmsRenderer.SmsTypes.TABLE) {
      final String fileName = typeName + "JsonArray.json";
      jsonMessageContext = this.getStringFromFile("/SmsRenderer/JSONData/" + fileName);
      jsonArray = new JSONArray();
      jsonArray.add(jsonMessageContext);
    } else {
      final JSONParser parser = new JSONParser();
      final String sourceFile = System.getProperty("user.dir") + "/src/test/resources/SmsRenderer/JSONData/tableJsonArray.json";
      final Object obj = parser.parse(new FileReader(sourceFile));
      final JSONObject jsonObject = (JSONObject) obj;
      jsonArray = (JSONArray) jsonObject.get("phones");
    }

    this.verifyResultJSON(typeName, "JsonArrayHtml.html", jsonArray, smsTypes);
  }

  private void verifyResultJSON(final String typeName, final String fileName, final JSONArray jsonArray, final SmsRenderer.SmsTypes smsTypes) throws IOException {
    final String compiledTemplate = this.minifyHTML(SmsRenderer.renderInBot(jsonArray, smsTypes));
    assertNotNull(compiledTemplate);

    final String expectedResult = this.minifyHTML(this.getStringFromFile("/SmsRenderer/HTMLResult/" + typeName + fileName));
    assertNotNull(expectedResult);
    assertEquals(expectedResult, compiledTemplate);
  }

  private void verifyResultJSON(final String typeName, final String fileName, final JSONObject jsonObject, final SmsRenderer.SmsTypes smsTypes) throws IOException {
    final String compiledTemplate = this.minifyHTML(SmsRenderer.renderInBot(jsonObject, smsTypes));
    assertNotNull(compiledTemplate);

    final String expectedResult = this.minifyHTML(this.getStringFromFile("/SmsRenderer/HTMLResult/" + typeName + fileName));
    assertNotNull(expectedResult);
    assertEquals(expectedResult, compiledTemplate);
  }

  private void verifyResultJSON(final String typeName, final String jsonMessageContext, final String fileName, final SmsRenderer.SmsTypes smsTypes) throws IOException {
    final String compiledTemplate = this.minifyHTML(SmsRenderer.renderInBot(jsonMessageContext, smsTypes));
    assertNotNull(compiledTemplate);

    final String expectedResult = this.minifyHTML(this.getStringFromFile("/SmsRenderer/HTMLResult/" + typeName + fileName));
    assertNotNull(expectedResult);
    assertEquals(expectedResult, compiledTemplate);
  }

  private void testString(final SmsRenderer.SmsTypes smsTypes) throws IOException {
    assertNotNull(smsTypes);

    final String typeName = smsTypes.getName().toLowerCase();
    final String jsonFileName = typeName + "Json.json";
    final String jsonMessageContext = this.getStringFromFile("/SmsRenderer/JSONData/" + jsonFileName);

    this.verifyResultJSON(typeName, jsonMessageContext, "StringHtml.html", smsTypes);
  }

  private void testJsonObject(final SmsRenderer.SmsTypes smsTypes) throws IOException, ParseException {
    assertNotNull(smsTypes);

    final String typeName = smsTypes.getName().toLowerCase();
    final String fileName = typeName + "Json.json";
    final String jsonMessageContext = this.getStringFromFile("/SmsRenderer/JSONData/" + fileName);

    final JSONObject jsonObject = this.getJsonObject(jsonMessageContext);
    assertNotNull(jsonObject);

    this.verifyResultJSON(typeName, "JsonObjectHtml.html", jsonObject, smsTypes);
  }

  private String getStringFromFile(final String fileName) throws IOException {
    assertNotNull(fileName);

    final String jsonMessageContext = this.removeCarriageReturn(this.readResourceContent(fileName));
    assertNotNull(jsonMessageContext);
    return jsonMessageContext;
  }

  private String readResourceContent(final String path) throws IOException {
    final InputStream inputStream = SmsRenderer.class.getResourceAsStream(path);
    final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    final BufferedReader reader = new BufferedReader(inputStreamReader);
    final StringBuffer sb = new StringBuffer();

    String content;
    while ((content = reader.readLine()) != null) {
      sb.append(content);
    }
    return sb.toString();
  }

  private String minifyHTML(final String text) {
    return (text == null) ? null : this.removeCarriageReturn(text.replaceAll("\\s{2,}", "").replaceAll("\n", "").replaceAll("\r", ""));
  }

  private String removeCarriageReturn(final String text) {
    return (text == null) ? null : text.replaceAll("\n", "").replaceAll("\r", "");
  }

  private JSONObject getJsonObject(final String jsonMessageContext) throws ParseException {
    assertNotNull(jsonMessageContext);

    final JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(jsonMessageContext);
    return jsonObject;
  }
}
