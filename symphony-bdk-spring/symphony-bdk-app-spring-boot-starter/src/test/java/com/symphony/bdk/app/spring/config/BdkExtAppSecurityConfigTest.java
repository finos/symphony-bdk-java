package com.symphony.bdk.app.spring.config;

import static org.assertj.core.api.Assertions.assertThat;
import com.symphony.bdk.app.spring.SymphonyBdkAppAutoConfiguration;
import com.symphony.bdk.app.spring.SymphonyBdkMockedConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

public class BdkExtAppSecurityConfigTest {
  @Test
  void corsFilters() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk-app.cors.[/**].allowed-origins=*",
            "bdk-app.cors.[/**].allow-credentials=false",
            "bdk-app.cors.[/**].allowed-methods=POST,GET",
            "bdk-app.cors.[/**].allowed-headers=*",
            "bdk-app.cors.[/**].exposed-headers=header-name-1,header-name-2",
            "bdk-app.cors.[/magic/**].allowed-origins=magic",
            "bdk-app.cors.[/magic/**].allow-credentials=false",
            "bdk-app.cors.[/magic/**].allowed-methods=POST,GET,DELETE",
            "bdk-app.cors.[/magic/**].allowed-headers=magic",
            "bdk-app.cors.[/magic/**].exposed-headers=magic-1,magic-2"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAppAutoConfiguration.class));

    contextRunner.run(context -> {
      List<CorsFilter> corsFilters = (List<CorsFilter>) context.getBean("corsFilters");
      assertThat(corsFilters).hasSize(2);
    });
  }
}
