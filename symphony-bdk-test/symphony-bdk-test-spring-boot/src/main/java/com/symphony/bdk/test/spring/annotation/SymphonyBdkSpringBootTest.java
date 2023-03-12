package com.symphony.bdk.test.spring.annotation;

import com.symphony.bdk.spring.config.BdkActivityConfig;
import com.symphony.bdk.spring.config.BdkCommonFeedConfig;
import com.symphony.bdk.test.SymphonyBdkSpringTestConfig;

import org.apiguardian.api.API;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SymphonyBdkSpringTestConfig.class, BdkCommonFeedConfig.class, BdkActivityConfig.class})
@Tag("integration-test")
@ActiveProfiles("integration-test")
@ExtendWith(MockitoExtension.class)
@API(status = API.Status.EXPERIMENTAL)
public @interface SymphonyBdkSpringBootTest {

  @AliasFor(annotation = SpringBootTest.class, attribute = "properties")
  String[] properties() default {"bot.id=1", "bot.username=bdk-bot", "bot.display-name=BDK Bot"};
}
