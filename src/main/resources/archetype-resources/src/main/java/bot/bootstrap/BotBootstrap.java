package ${package}.bot.bootstrap;

import ${package}.bot.config.BotConfig;
import ${package}.bot.config.CryptoParameters;
import ${package}.bot.config.ProxyConfig;
import ${package}.bot.listeners.ChatListener;
import ${package}.logging.LoggingAspect;
import ${package}.logging.MDCRequestListener;
import ${package}.logging.MDCTaskDecorator;
import ${package}.logging.MDCUserInfoFilter;

import authentication.SymBotRSAAuth;
import authentication.SymExtensionAppRSAAuth;
import authentication.jwt.AuthenticationFilter;
import clients.ISymClient;
import clients.SymBotClient;
import clients.symphony.api.HealthcheckClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import services.DatafeedEventsService;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.Executor;

@Component
public class BotBootstrap {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotBootstrap.class);

  private final BotConfig botConfig;
  private final CryptoParameters cryptoParameters;

  public BotBootstrap(BotConfig botConfig, CryptoParameters cryptoParameters) {
    this.botConfig = botConfig;
    this.cryptoParameters = cryptoParameters;
  }

  /**
   * Bean that loads the configurations of config.json file and set it in a SymConfig instance
   * @return {@link SymConfig}
   */
  @Bean
  public SymConfig symConfig() {
    LOGGER.info("Getting the configuration file on ", botConfig.getBotConfig(), " ...");
    return SymConfigLoader.loadFromFile(botConfig.getBotConfig());
  }

  /**
   * Bean that creates a bot client to access data feed
   * @param symConfig configuration from bot-config.json
   * @param symBotAuth bean of authentication
   * @return {@link SymBotClient}
   */
  @Bean
  public SymBotClient symBotClient(SymConfig symConfig, SymBotRSAAuth symBotAuth) {
    LOGGER.info("Creating the bot client...");
    return SymBotClient.initBot(symConfig, symBotAuth);
  }

  /**
   * Bean that authenticates the bot into a POD using the certificates
   * @param symConfig configuration from bot-config.json
   * @return {@link SymBotRSAAuth}
   */
  @Bean
  public SymBotRSAAuth symBotRSAAuth(SymConfig symConfig) {
    LOGGER.info("Authenticating user...");
    SymBotRSAAuth botAuth = new SymBotRSAAuth(symConfig);
    botAuth.authenticate();
    return botAuth;
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
   * Bean that make the health check in the agent
   * @param iSymClient configuration from bot-config.json
   * @return {@link HealthcheckClient}
   */
  @Bean
  public HealthcheckClient healthcheckClient(ISymClient iSymClient) {
    return new HealthcheckClient(iSymClient);
  }

  /**
   * Bean that adds the chat listener
   * @param symBotClient configuration from bot-config.json
   * @return {@link DatafeedEventsService}
   */
  @Bean
  public DatafeedEventsService datafeedEventsService(SymBotClient symBotClient,
      ChatListener chatListener) {
    LOGGER.info("Getting datafeed events...");
    DatafeedEventsService datafeedEventsService = symBotClient.getDatafeedEventsService();
    datafeedEventsService.addIMListener(chatListener);
    datafeedEventsService.addRoomListener(chatListener);
    return datafeedEventsService;
  }

  /**
   * Adding a request filter to intercept and validate authorization header
   * @param symExtensionAppRSAAuth - extension app data
   * @param symConfig - configuration from bot-config.json
   * @return {@link AuthenticationFilter}
   */
  @Bean
  public FilterRegistrationBean addAuthenticationFilter(
      SymExtensionAppRSAAuth symExtensionAppRSAAuth, SymConfig symConfig) {
    FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
    AuthenticationFilter authenticationFilter =
        new AuthenticationFilter(symExtensionAppRSAAuth, symConfig);
    registrationBean.setFilter(authenticationFilter);
    registrationBean.addUrlPatterns("*");
    registrationBean.setOrder(1);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<MDCUserInfoFilter> addMDCUserInfoFilter() {
    FilterRegistrationBean<MDCUserInfoFilter> registrationBean = new FilterRegistrationBean<>();
    MDCUserInfoFilter mdcPostFilter = new MDCUserInfoFilter();
    registrationBean.setFilter(mdcPostFilter);
    registrationBean.addUrlPatterns("*");
    registrationBean.setOrder(2);
    return registrationBean;
  }

  /**
   * Creates a ThreadPoolExecutor to use more than a thread.
   * @return {@link Executor}
   */
  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("MyApp-");
    executor.setTaskDecorator(new MDCTaskDecorator());
    executor.initialize();
    return executor;
  }

  @Bean
  public MDCRequestListener requestListener() {
    return new MDCRequestListener();
  }

  @Bean
  public LoggingAspect loggingAspect() {
    return new LoggingAspect();
  }

  /**
   * Adds a proxy based on configuration
   * @param proxyConfig Proxy config
   * @return {@link Proxy}
   */
  @Bean
  @ConditionalOnProperty(value = "proxy.enabled", havingValue = "true")
  public Proxy proxy(ProxyConfig proxyConfig) {
    return new Proxy(Proxy.Type.HTTP,
        new InetSocketAddress(proxyConfig.getAddress(), proxyConfig.getPort()));
  }

  /**
   * If proxy is enabled, configures {@link RestTemplate} to use it
   * @param proxy Proxy to be applied to RestTemplate
   * @return {@link RestTemplate}
   */
  @Bean
  @ConditionalOnBean(name = "proxy")
  public RestTemplate restTemplateWithProxy(Proxy proxy) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setProxy(proxy);
    return new RestTemplate(requestFactory);
  }

  /**
   * If proxy is not enabled or not configured, creates the RestTemplate with default configuration
   * @return {@link RestTemplate}
   */
  @Bean
  @ConditionalOnProperty(value = "proxy.enabled",
      havingValue = "false",
      matchIfMissing = true)
  public RestTemplate restTemplateWithoutProxy() {
    return new RestTemplate();
  }
}