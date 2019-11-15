package com.symphony.ms.songwriter.internal.symphony.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import authentication.SymBotRSAAuth;
import authentication.SymExtensionAppRSAAuth;
import authentication.jwt.AuthenticationFilter;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import configuration.SymLoadBalancedConfig;
import exceptions.AuthenticationException;

@Configuration
public class SymphonyConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(SymphonyConfig.class);

  private final SymphonyProps symphonyProps;

  public SymphonyConfig(SymphonyProps symphonyProps) {
    this.symphonyProps = symphonyProps;
  }

  /**
   * Bean that loads the configurations of bot-config.json file and set it in a
   * SymConfig instance
   *
   * @return {@link SymConfig}
   */
  @Bean
  public SymConfig symConfig() {
    LOGGER.info("Loading configuration from {}", symphonyProps.getBotConfig());
    return SymConfigLoader.loadFromFile(symphonyProps.getBotConfig());
  }

  /**
   * Bean that loads the configurations of lb-config.json file and set it in a
   * SymLoadBalancedConfig instance
   * @return {@link SymLoadBalancedConfig}
   */
  @Bean
  public SymLoadBalancedConfig symLbConfig() {
    if (symphonyProps.getLbConfig() != null) {
      LOGGER.info("Loading load balancer configuration from {} ", symphonyProps.getLbConfig());
      return SymConfigLoader.loadLoadBalancerFromFile(symphonyProps.getLbConfig());
    }
    return new SymLoadBalancedConfig();
  }

  /**
   * Bean that authenticates the bot into a POD using the certificates
   * @param symConfig configuration from bot-config.json
   * @return {@link SymBotRSAAuth}
   */
  @Bean
  public SymBotRSAAuth symBotRSAAuth(SymConfig symConfig) throws AuthenticationException {
    LOGGER.info("Authenticating user...");
    SymBotRSAAuth botAuth = new SymBotRSAAuth(symConfig);
    botAuth.authenticate();
    return botAuth;
  }

  /**
   * Bean that creates a bot client to access datafeed
   * @param symConfig configuration from bot-config.json
   * @param symBotAuth bean of authentication
   * @return {@link SymBotClient}
   */
  @Bean
  public SymBotClient symBotClient(SymConfig symConfig,
      SymBotRSAAuth symBotAuth, SymLoadBalancedConfig symLbConfig) {
    LOGGER.info("Initializing bot client...");
    if (symLbConfig.getLoadBalancing() == null) {
      return SymBotClient.initBot(symConfig, symBotAuth);
    }
    return SymBotClient.initBot(symConfig, symBotAuth, symLbConfig);
  }

  /**
   * Bean that authenticates the extension app into a POD using the RSA keys
   * @param symConfig configuration from bot-config.json
   * @return {@link SymExtensionAppRSAAuth}
   */
  @Bean
  public SymExtensionAppRSAAuth symExtensionAppRSAAuth(SymConfig symConfig) {
    LOGGER.info("Authenticating extension app...");
    return new SymExtensionAppRSAAuth(symConfig);
  }

  /**
   * Adding a request filter to intercept and validate authorization header
   * @param symExtensionAppRSAAuth - extension app data
   * @param symConfig - configuration from bot-config.json
   * @return {@link AuthenticationFilter}
   */
  @Bean
  public FilterRegistrationBean<AuthenticationFilter> addAuthenticationFilter(
      SymExtensionAppRSAAuth symExtensionAppRSAAuth, SymConfig symConfig) {
    FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
    AuthenticationFilter authenticationFilter =
        new AuthenticationFilter(symExtensionAppRSAAuth, symConfig);
    registrationBean.setFilter(authenticationFilter);
    registrationBean.addUrlPatterns("*");
    registrationBean.setOrder(1);
    return registrationBean;
  }

}
