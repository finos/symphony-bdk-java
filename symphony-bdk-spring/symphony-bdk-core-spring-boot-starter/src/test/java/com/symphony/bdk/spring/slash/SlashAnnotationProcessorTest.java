package com.symphony.bdk.spring.slash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.SlashCommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This test class ensures that methods annotated by {@link com.symphony.bdk.spring.annotation.Slash} annotation are
 * registered withing the {@link com.symphony.bdk.core.activity.ActivityRegistry}.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SlashAnnotationProcessorTest {

  @Autowired
  private ActivityRegistry activityRegistry;

  @Autowired
  private ApplicationContext applicationContext;

  /**
   * Multiple classes in this test package create @Slash methods in different ways: from prototype scopes bean, from lazy beans...
   * We want here to ensure that those @Slash methods are correctly registered in the {@link ActivityRegistry}.
   *
   * @see TestSlashConfig
   * @see TestSlashCommand
   * @see TestFormReplyActivity
   */
  @Test
  void slashMethodShouldBeRegistered() {

    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(3);

    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(SlashCommand.class)));
    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(TestFormReplyActivity.class)));

    // for lazy beans, slash activity is added once bean initialized
    this.applicationContext.getBean("foobar-lazy");
    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(4);

    // verify that slash activity is not added twice in the registry for a lazy bean
    this.applicationContext.getBean("foobar-lazy");
    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(4);
  }
}
