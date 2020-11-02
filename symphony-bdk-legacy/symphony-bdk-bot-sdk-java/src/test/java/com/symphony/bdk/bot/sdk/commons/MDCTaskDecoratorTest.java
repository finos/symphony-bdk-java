package com.symphony.bdk.bot.sdk.commons;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MDCTaskDecoratorTest {

  private MDCTaskDecorator mdcTaskDecorator;
  private Map<String, String> emptyContextMap;
  private Map<String, String> fullContextMap;
  private Runnable runnable;

  @Before
  public void init(){
    this.emptyContextMap = new HashMap();

    this.fullContextMap = new HashMap();
    this.fullContextMap.put("key1", "value1");
    this.fullContextMap.put("key2", "value2");
    this.fullContextMap.put("key3", "value3");

    this.runnable = () -> {};
  }

  @Test
  public void testDecorateWithFullHashMap(){
    this.setMDCMapAndTest(this.fullContextMap);
  }

  @Test
  public void testDecorateWithEmptyHashMap(){
    this.setMDCMapAndTest(this.emptyContextMap);
  }

  private void setMDCMapAndTest(Map<String, String> contextMap) {
    this.setMDCTaskDecoratorAndContextMap(contextMap);

    this.mdcTaskDecorator.decorate(this.runnable);

    final Map<String, String> resultMap = MDC.getCopyOfContextMap();

    assertEquals(resultMap, contextMap);
  }

  private void setMDCTaskDecoratorAndContextMap(Map<String, String> contextMap) {
    this.mdcTaskDecorator = new MDCTaskDecorator();
    MDC.setContextMap(contextMap);
  }
}
