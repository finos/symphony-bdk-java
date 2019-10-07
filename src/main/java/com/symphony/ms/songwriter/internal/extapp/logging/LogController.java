package com.symphony.ms.songwriter.internal.extapp.logging;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/v1/log")
public class LogController {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

  private static final String APP_LOG_PREFIX = "[CLIENT LOG] ";

  /**
   * Provides an interface which logs a message
   * @param message The message to be logged
   */
  @PostMapping
  public ResponseEntity<String> logMessage(
      @RequestParam Optional<LogLevelEnum> level, @RequestBody String message) {
    String logMsg = APP_LOG_PREFIX + message;

    switch(level.orElse(defaultLevel())) {
      case DEBUG:
        LOGGER.debug(logMsg);
        break;
      case INFO:
        LOGGER.info(logMsg);
        break;
      case WARN:
        LOGGER.warn(logMsg);
        break;
      case ERROR:
        LOGGER.error(logMsg);
        break;
      default:
        LOGGER.info(logMsg);
        break;
    }

    return ResponseEntity.ok().build();
  }

  private LogLevelEnum defaultLevel() {
    return LogLevelEnum.INFO;
  }
}

