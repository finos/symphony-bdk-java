package ${package}.bot.bootstrap;

import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ${package}.bot.config.BotConfig;
import ${package}.bot.listeners.ChatListener;
import services.DatafeedEventsService;

import java.util.concurrent.Executor;

@Component
public class BotBootstrap implements WebMvcConfigurer {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotBootstrap.class);

  private final BotConfig botConfig;

  public BotBootstrap(BotConfig botConfig) {
    this.botConfig = botConfig;
  }

  /**
   * Bean that loads the configurations of config.json file and set it in a SymConfig instance
   * @return {@link SymConfig}
   */
  @Bean
  public SymConfig symConfig() {
    LOGGER.info("Getting the configuration file on " + botConfig.getBotConfig() + " ...");
    SymConfigLoader configLoader = new SymConfigLoader();
    return configLoader.loadFromFile(botConfig.getBotConfig());
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
   * Bean that add the chat listener
   * @param symBotClient configuration from bot-config.json
   * @param chatListener IMListener instance
   * @return {@link DatafeedEventsService}
   */
  @Bean
  public DatafeedEventsService datafeedEventsService(SymBotClient symBotClient,
      ChatListener chatListener) {
    LOGGER.info("Getting datafeed events...");
    DatafeedEventsService datafeedEventsService = symBotClient.getDatafeedEventsService();

    LOGGER.info("Adding my IMListener...");
    datafeedEventsService.addIMListener(chatListener);
    datafeedEventsService.addRoomListener(chatListener);

    return datafeedEventsService;
  }

  /**
   * Bean to initiate chat listener
   * @return {@link ChatListener}
   */
  @Bean
  public ChatListener chatListener() {
    return new ChatListener();
  }

}
