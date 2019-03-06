package ${package}.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/healthz")
public class HealthController {

  @GetMapping
  public ResponseEntity health() {
    return ResponseEntity.ok().build();
  }

}
