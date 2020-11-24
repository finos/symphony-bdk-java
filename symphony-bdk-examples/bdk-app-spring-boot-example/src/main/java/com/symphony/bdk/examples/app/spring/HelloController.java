package com.symphony.bdk.examples.app.spring;

import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

  private static final Logger log = LoggerFactory.getLogger(HelloController.class);

  private final StreamService streamService;

  public HelloController(StreamService streamService) {
    this.streamService = streamService;
  }

  /**
   * Dummy controller method used to check {@link com.symphony.bdk.app.spring.filter.TracingFilter} behaviour.
   */
  @GetMapping
  public String hello() {
    log.info("hello");
    final List<StreamAttributes> streams = this.streamService.listStreams(new StreamFilter());
    log.info("{} streams found.", streams.size());
    return streams.size() + " streams found.";
  }
}
