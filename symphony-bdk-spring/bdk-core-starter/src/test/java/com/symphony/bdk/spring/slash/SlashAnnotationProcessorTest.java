package com.symphony.bdk.spring.slash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.SlashCommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

  @Test
  void slashMethodShouldBeRegistered() {

    // 2 activities should be registered: slash cmd and form reply
    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(2);

    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(SlashCommand.class)));
    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(TestFormReplyActivity.class)));
  }
}
