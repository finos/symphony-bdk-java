package com.symphony.bdk.bot.sdk.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class MDCTaskDecoratorTest {

  private MDCTaskDecorator mdcTaskDecorator;
  private Map<String, String> emptyContextMap;
  private Map<String, String> fullContextMap;
  private Map<String, String> nullContextMap;
  private Runnable runnable;

  @Before
  public void init(){
    this.nullContextMap = null;

    this.emptyContextMap = new HashMap();

    this.fullContextMap = new HashMap();
    this.fullContextMap.put("key1", "value1");
    this.fullContextMap.put("key2", "value2");
    this.fullContextMap.put("key3", "value3");

    this.runnable = () -> {};
  }

  @After
  public void clearAndVerifyMDC() {
    MDC.clear();
    assertTrue(MDC.getCopyOfContextMap().isEmpty());
  }

  @Test
  public void testDecorateWithFullHashMap(){
    this.setMDCMapAndTest(this.fullContextMap);
  }

  @Test
  public void testDecorateWithEmptyHashMap(){
    this.setMDCMapAndTest(this.emptyContextMap);
  }

  @Test
  public void testDecorateWithNullHashMap() { this.setMDCMapAndTest(this.nullContextMap); }

  private void setMDCMapAndTest(Map<String, String> contextMap) {
    this.setMDCTaskDecoratorAndContextMap(contextMap);

    final Runnable decoratedRunnable = this.mdcTaskDecorator.decorate(this.runnable);
    this.testResultContextMap(contextMap);

    decoratedRunnable.run();
    this.setMDCTaskDecoratorAndContextMap(this.emptyContextMap);
    this.testResultContextMap(this.emptyContextMap);
  }

  private void testResultContextMap(Map<String, String> contextMap) {
    Map<String, String> resultMap;
    if(contextMap == null)
      resultMap = null;
    else
      resultMap = MDC.getCopyOfContextMap();

    assertEquals(resultMap, contextMap);
  }

  private void setMDCTaskDecoratorAndContextMap(Map<String, String> contextMap) {
    this.mdcTaskDecorator = new MDCTaskDecorator();
    if(contextMap == null)
      MDC.setContextMap(new HashMap<>());
    else
      MDC.setContextMap(contextMap);
  }
}
