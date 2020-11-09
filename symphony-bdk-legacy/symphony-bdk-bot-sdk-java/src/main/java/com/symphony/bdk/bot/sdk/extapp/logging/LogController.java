package com.symphony.bdk.bot.sdk.extapp.logging;

import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.symphony.bdk.bot.sdk.symphony.ConfigClient;

/**
 * Exposes an endpoint through which extension applications could send logs to
 * be persisted along with server-side logs.
 *
 * @author msecato
 *
 */
@RestController
public class LogController {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

  private static final String APP_LOG_PREFIX = "[CLIENT LOG] ";
  private static final String LOG_PATH = "log";
  private String authPath;

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  private RequestMappingHandlerMapping handlerMapping;

  public LogController(ConfigClient configClient) {
    authPath = configClient.getExtAppAuthPath();
  }

  @PostConstruct
  public void init() throws NoSuchMethodException {
    registerRoute(authPath.concat(LOG_PATH));
  }

  /**
   * Provides an interface which logs a message
   * @param level the log level (DEBUG, INFO, WARN or ERROR)
   * @param message The message to be logged
   * @return the response
   */
  public ResponseEntity<String> logMessage(
      @RequestParam Optional<LogLevelEnum> level, @RequestBody String message) {
    String logMsg = APP_LOG_PREFIX + message;

    switch(level.orElse(defaultLevel())) {
      case DEBUG:
        LOGGER.debug(logMsg);
        break;
      case WARN:
        LOGGER.warn(logMsg);
        break;
      case ERROR:
        LOGGER.error(logMsg);
        break;
      case INFO:
      default:
        LOGGER.info(logMsg);
        break;
    }

    return ResponseEntity.ok().build();
  }

  private LogLevelEnum defaultLevel() {
    return LogLevelEnum.INFO;
  }

  private void registerRoute(String route) throws NoSuchMethodException {
    handlerMapping.registerMapping(RequestMappingInfo.paths(route)
        .methods(RequestMethod.POST).build(),
        this,
        LogController.class.getDeclaredMethod("logMessage",
            Optional.class,
            String.class));
  }
}

