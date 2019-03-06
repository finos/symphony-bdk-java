package ${package}.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class to load configurations of application.yaml
 */
@Configuration
@ConfigurationProperties
public class BotConfig {

  /**
   * This property is related to config.json file
   */
  private String botConfig;

  public String getBotConfig() {
    return botConfig;
  }

  public void setBotConfig(String botConfig) {
    this.botConfig = botConfig;
  }
}
