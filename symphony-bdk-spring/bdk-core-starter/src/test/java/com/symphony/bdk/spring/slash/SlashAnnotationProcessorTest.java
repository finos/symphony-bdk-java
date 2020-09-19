package com.symphony.bdk.spring.slash;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.core.activity.AbstractActivity;
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
    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(1);
    final AbstractActivity<?, ?> activity = this.activityRegistry.getActivityList().get(0);
    assertThat(activity.getClass()).isEqualTo(SlashCommand.class);
  }
}
