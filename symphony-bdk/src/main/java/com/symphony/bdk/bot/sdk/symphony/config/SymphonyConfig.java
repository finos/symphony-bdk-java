package com.symphony.bdk.bot.sdk.symphony.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.symphony.bdk.bot.sdk.symphony.authentication.CertificateExtensionAppAuthenticator;
import com.symphony.bdk.bot.sdk.symphony.authentication.ExtensionAppAuthenticator;
import com.symphony.bdk.bot.sdk.symphony.authentication.RSAExtensionAppAuthenticator;

import authentication.ISymAuth;
import authentication.SymBotAuth;
import authentication.SymBotRSAAuth;
import authentication.extensionapp.InMemoryTokensRepository;
import authentication.extensionapp.TokensRepository;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import configuration.SymLoadBalancedConfig;
import exceptions.AuthenticationException;

/**
 * Initializes all Symphony components required by bots and extension apps
 *
 * @author Marcus Secato
 *
 */
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
   *
   * @return {@link SymLoadBalancedConfig}
   */
  @Bean
  public SymLoadBalancedConfig symLbConfig() {
    if (symphonyProps.getLbConfig() != null) {
      LOGGER.info("Loading load balancer configuration from {} ",
          symphonyProps.getLbConfig());
      return SymConfigLoader.loadLoadBalancerFromFile(
          symphonyProps.getLbConfig());
    }
    return new SymLoadBalancedConfig();
  }

  /**
   * Bean that creates a bot client to access datafeed
   *
   * @param symConfig configuration from bot-config.json
   * @param symLbConfig the load balancer configuration from lb-config.json
   * @return {@link SymBotClient}
   * @throws AuthenticationException if authentication fails
   */
  @Bean
  public SymBotClient symBotClient(SymConfig symConfig,
      SymLoadBalancedConfig symLbConfig) throws AuthenticationException {
    LOGGER.info("Initializing bot client...");
    ISymAuth symBotAuth = getSymBotAuth(symConfig);
    symBotAuth.authenticate();

    if (symLbConfig.getLoadBalancing() == null) {
      return SymBotClient.initBot(symConfig, symBotAuth);
    }
    return SymBotClient.initBot(symConfig, symBotAuth, symLbConfig);
  }

  /**
   * Creates a simple in-memory tokens cache to be used in extension app
   * authentication process.
   *
   * @return the tokens repository
   */
  @Bean
  @ConditionalOnMissingBean
  public TokensRepository tokensRepository() {
    return new InMemoryTokensRepository();
  }

  /**
   * Registers an extension app authenticator bean
   *
   * @param symConfig configuration from bot-config.json
   * @param tokensRepository tokens repository
   * @return the authenticator
   */
  @Bean
  public ExtensionAppAuthenticator extensionAppAuthenticator(SymConfig symConfig,
      TokensRepository tokensRepository) {
    if (!StringUtils.isEmpty(symConfig.getAppPrivateKeyName())
        && !StringUtils.isEmpty(symConfig.getAppPrivateKeyPath())) {
      return new RSAExtensionAppAuthenticator(symConfig, tokensRepository);
    } else {
      return new CertificateExtensionAppAuthenticator(symConfig, tokensRepository);
    }
  }

  private ISymAuth getSymBotAuth(SymConfig symConfig) {
    if (!StringUtils.isEmpty(symConfig.getBotPrivateKeyName())
        && !StringUtils.isEmpty(symConfig.getBotPrivateKeyPath())) {
      return new SymBotRSAAuth(symConfig);
    } else {
      return new SymBotAuth(symConfig);
    }
  }

}
