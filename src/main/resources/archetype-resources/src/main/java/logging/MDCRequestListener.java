package ${package}.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

@Component
public class MDCRequestListener implements ServletRequestListener {
  protected static final Logger LOGGER = LoggerFactory.getLogger(
      ${package}.logging.MDCRequestListener.class);

  public void requestInitialized(ServletRequestEvent event) {
    MDC.put("TransactionId", String.valueOf(UUID.randomUUID()));
    LOGGER.debug("REQUEST INITIALIZED");
  }

  public void requestDestroyed(ServletRequestEvent event) {
    MDC.clear();
    LOGGER.debug("REQUEST DESTROYED");
  }
}
