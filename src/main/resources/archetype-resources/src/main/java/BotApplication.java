package ${package};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "${package}")
@EnableSwagger2
@EnableWebMvc
@EnableAsync
public class BotApplication {

  public static void main(String[] args) {
    new SpringApplication(BotApplication.class).run(args);
  }
}
