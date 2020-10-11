package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * Base class for example creation. Config is loaded from ~/.symphony/config.yaml.
 */
@Slf4j
public abstract class BdkExample {

  protected abstract void run(SymphonyBdk bdk) throws Exception;

  public static void run(Class<? extends BdkExample> clz) {
    try {
      final BdkExample example = clz.newInstance();
      final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
      example.run(bdk);
    } catch (Exception e) {
      log.error("Cannot run example {}", clz, e);
    }
  }

  protected static InputStream loadAttachment(String path) {
    return ComplexMessageExample.class.getResourceAsStream(path);
  }
}
