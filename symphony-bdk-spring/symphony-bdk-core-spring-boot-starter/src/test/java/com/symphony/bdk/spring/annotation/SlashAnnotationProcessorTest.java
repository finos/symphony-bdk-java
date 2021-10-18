package com.symphony.bdk.spring.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.spring.slash.TestFormReplyActivity;
import com.symphony.bdk.spring.slash.TestSlashCommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * This test class ensures that methods annotated by {@link com.symphony.bdk.spring.annotation.Slash} annotation are
 * registered withing the {@link com.symphony.bdk.core.activity.ActivityRegistry}.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SlashAnnotationProcessorTest {

  @Autowired
  private ActivityRegistry activityRegistry;

  /**
   * Multiple classes in this test package create @Slash methods in different ways: from prototype scopes bean, from lazy beans...
   * We want here to ensure that those @Slash methods are correctly registered in the {@link ActivityRegistry}.
   *
   * @see com.symphony.bdk.spring.slash.TestSlashConfig
   * @see com.symphony.bdk.spring.slash.TestSlashCommand
   * @see com.symphony.bdk.spring.slash.TestFormReplyActivity
   */
  @Test
  void slashMethodShouldBeRegistered() {

    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(5);

    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(SlashCommand.class)));
    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(TestFormReplyActivity.class)));
  }

  @Test
  void testCreateSlashCommandCallback() throws Exception {
    final TestSlashCommand bean = spy(new TestSlashCommand());
    final Method publicMethod = bean.getClass().getDeclaredMethod("onTest", CommandContext.class);
    final Consumer<CommandContext> callback = SlashAnnotationProcessor.createSlashCommandCallback(bean, publicMethod);
    callback.accept(mock(CommandContext.class));
    verify(bean).onTest(any(CommandContext.class));
  }

  @Test
  void testExecuteCallbackWithError() throws Exception {
    final TestSlashCommand bean = spy(new TestSlashCommand());
    final Method publicMethod = bean.getClass().getDeclaredMethod("onErrorTest", CommandContext.class);
    final Consumer<CommandContext> callback = SlashAnnotationProcessor.createSlashCommandCallback(bean, publicMethod);
    callback.accept(mock(CommandContext.class));
    verify(bean, never()).onTest(any(CommandContext.class));
  }
}
