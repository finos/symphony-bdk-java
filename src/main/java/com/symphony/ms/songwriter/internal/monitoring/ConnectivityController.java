package com.symphony.ms.songwriter.internal.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/connectivity")
public class ConnectivityController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectivityController.class);

  @GetMapping
  public ResponseEntity<?> getConnectivity() {
    LOGGER.trace("Connectivity check");
    return ResponseEntity.ok().build();
  }

}
