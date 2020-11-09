package com.symphony.bdk.bot.sdk.feature;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class FeatureManagerTest {

  private FeatureManager featureManager;

  @Before
  public void initFeatureManager(){

    this.featureManager = new FeatureManager();
    assertNotNull(this.featureManager);

    this.featureManager.setCommandFeedback("Test command feedback");
    this.featureManager.setTransactionIdOnError("Test transaction id on error");
    this.featureManager.setEventUnexpectedErrorMessage("Event unexpected error message");
    this.featureManager.setPublicRoomAllowed(true);
    this.featureManager.setPublicRoomNotAllowedMessage("Not allowed message");
    this.featureManager.setPublicRoomNotAllowedTemplate("Not allowed template");

    final Map templateMap = new HashMap();
    templateMap.put("Key1", "Value1");
    templateMap.put("Key2", "Value2");
    templateMap.put("Key3", "Value3");
    this.featureManager.setPublicRoomNotAllowedTemplateMap(templateMap);
  }

  @Test
  public void testGetCommandFeedback(){
    assertEquals("Test command feedback", this.featureManager.getCommandFeedback());
  }

  @Test
  public void testSetCommandFeedback(){
    this.featureManager.setCommandFeedback("Test command feedback 2");
    assertEquals("Test command feedback 2", this.featureManager.getCommandFeedback());
  }

  @Test
  public void testGetTransactionIdOnError(){
    assertEquals("Test transaction id on error", this.featureManager.getTransactionIdOnError());
  }

  @Test
  public void testSetTransactionIdOnError(){
    this.featureManager.setTransactionIdOnError("Test transaction id on error 2");
    assertEquals("Test transaction id on error 2", this.featureManager.getTransactionIdOnError());
  }

  @Test
  public void testGetEventUnexpectedErrorMessage(){
    assertEquals("Event unexpected error message", this.featureManager.getEventUnexpectedErrorMessage());
  }

  @Test
  public void testSetEventUnexpectedErrorMessage(){
    this.featureManager.setEventUnexpectedErrorMessage("Event unexpected error message 2");
    assertEquals("Event unexpected error message 2", this.featureManager.getEventUnexpectedErrorMessage());
  }

  @Test
  public void testGetIsPublicRoomAllowed(){
    assertTrue(this.featureManager.isPublicRoomAllowed());
  }

  @Test
  public void testSetIsPublicRoomAllowed(){
    this.featureManager.setPublicRoomAllowed(false);
    assertFalse(this.featureManager.isPublicRoomAllowed());
  }

  @Test
  public void testGetPublicRoomNotAllowedMessage(){
    assertEquals("Not allowed message", this.featureManager.getPublicRoomNotAllowedMessage());
  }

  @Test
  public void testSetPublicRoomNotAllowedMessage(){
    this.featureManager.setPublicRoomNotAllowedMessage("Not allowed message 2");
    assertEquals("Not allowed message 2", this.featureManager.getPublicRoomNotAllowedMessage());
  }

  @Test
  public void testGetPublicRoomNotAllowedTemplate(){
    assertEquals("Not allowed template", this.featureManager.getPublicRoomNotAllowedTemplate());
  }

  @Test
  public void testSetPublicRoomNotAllowedTemplate(){
    this.featureManager.setPublicRoomNotAllowedTemplate("Not allowed template 2");
    assertEquals("Not allowed template 2", this.featureManager.getPublicRoomNotAllowedTemplate());
  }

  @Test
  public void testGetPublicRoomNotAllowedTemplateMap(){
    final Map myTemplateMap = new HashMap();
    myTemplateMap.put("Key1", "Value1");
    myTemplateMap.put("Key2", "Value2");
    myTemplateMap.put("Key3", "Value3");

    assertEquals(myTemplateMap, this.featureManager.getPublicRoomNotAllowedTemplateMap());
  }

  @Test
  public void testSetPublicRoomNotAllowedTemplateMap(){
    final Map myTemplateMap = new HashMap();
    myTemplateMap.put("newKey1", "newValue1");
    myTemplateMap.put("newKey2", "newValue2");
    myTemplateMap.put("newKey3", "newValue3");

    this.featureManager.setPublicRoomNotAllowedTemplateMap(myTemplateMap);

    assertEquals(myTemplateMap, this.featureManager.getPublicRoomNotAllowedTemplateMap());
  }

  @Test
  public void testIsCommandFeedbackEnabled(){
    assertFalse(this.featureManager.isCommandFeedbackEnabled());

    this.featureManager.setCommandFeedback("enabled");

    assertTrue(this.featureManager.isCommandFeedbackEnabled());
  }

  @Test
  public void testIsTransactionIdOnErrorEnabled(){
    assertFalse(this.featureManager.isTransactionIdOnErrorEnabled());

    this.featureManager.setTransactionIdOnError("enabled");

    assertTrue(this.featureManager.isTransactionIdOnErrorEnabled());
  }

  @Test
  public void testUnexpectedErrorResponse(){

    String expectedResult = null;

    this.featureManager.setCommandFeedback("not enabled");
    this.featureManager.setEventUnexpectedErrorMessage(null);
    this.featureManager.setTransactionIdOnError("not enabled");
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("not enabled");
    this.featureManager.setEventUnexpectedErrorMessage(null);
    this.featureManager.setTransactionIdOnError("enabled");
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("not enabled");
    this.featureManager.setEventUnexpectedErrorMessage("message");
    this.featureManager.setTransactionIdOnError("not enabled");
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("not enabled");
    this.featureManager.setEventUnexpectedErrorMessage("message");
    this.featureManager.setTransactionIdOnError("enabled");
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("enabled");
    this.featureManager.setEventUnexpectedErrorMessage(null);
    this.featureManager.setTransactionIdOnError("not enabled");
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("enabled");
    this.featureManager.setEventUnexpectedErrorMessage(null);
    this.featureManager.setTransactionIdOnError("enabled");
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("enabled");
    this.featureManager.setEventUnexpectedErrorMessage("message");
    this.featureManager.setTransactionIdOnError("not enabled");
    expectedResult = this.featureManager.getEventUnexpectedErrorMessage();
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());

    this.featureManager.setCommandFeedback("enabled");
    this.featureManager.setEventUnexpectedErrorMessage("message");
    this.featureManager.setTransactionIdOnError("enabled");
    expectedResult = this.featureManager.getEventUnexpectedErrorMessage() + " (code=" + MDC.get("transactionId") + ")";
    assertEquals(expectedResult, this.featureManager.unexpectedErrorResponse());
  }
}
