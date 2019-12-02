package com.symphony.ms.songwriter.internal.symphony.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Loads Symphony-related configurations from properties files
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
@Component
@ConfigurationProperties
public class SymphonyProps {

  /**
   * This property is related to bot-config.json file
   */
  private String botConfig;

  /**
   * This property is related to lb-config.json file
   */
  private String lbConfig;

}
