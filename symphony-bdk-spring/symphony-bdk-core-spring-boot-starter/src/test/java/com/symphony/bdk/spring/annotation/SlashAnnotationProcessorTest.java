package com.symphony.bdk.spring.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.activity.parsing.Arguments;
import com.symphony.bdk.spring.slash.TestFormReplyActivity;
import com.symphony.bdk.spring.slash.TestSlashCommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    assertThat(this.activityRegistry.getActivityList().size()).isEqualTo(8);
    assertTrue(this.activityRegistry.getActivityList().stream().anyMatch(a -> a.getClass().equals(TestFormReplyActivity.class)));

    final List<SlashCommand> slashCommands = this.activityRegistry.getActivityList()
        .stream()
        .filter(a -> a.getClass().equals(SlashCommand.class))
        .map(a -> (SlashCommand) a)
        .collect(Collectors.toList());
    assertEquals(7, slashCommands.size());

    final List<String> expectedSlashCommandPatterns =
        Arrays.asList("/test", "/error-test", "/lazy-foo-bar", "/foo-bar", "/hello {arg}", "/hello {arg}",
            "/hello1 {arg} {@mention} {#hashtag} {$cashtag}", "/hello2 {arg} {@mention} {#hashtag} {$cashtag}");
    final Set<String> actualSlashCommandPatterns =
        slashCommands.stream().map(SlashCommand::getSlashCommandName).collect(Collectors.toSet());
    assertEquals(new HashSet<>(expectedSlashCommandPatterns), actualSlashCommandPatterns);
  }

  @Test
  void testCreateSlashCommandCallback() throws Exception {
    final TestSlashCommand bean = spy(new TestSlashCommand());
    final Method publicMethod = bean.getClass().getDeclaredMethod("onTest", CommandContext.class);
    final Consumer<CommandContext> callback = SlashAnnotationProcessor.createSlashCommandCallback(bean, publicMethod, Collections.emptyMap());
    callback.accept(mock(CommandContext.class));
    verify(bean).onTest(any(CommandContext.class));
  }

  @Test
  void testCreateSlashCommandCallbackWithParams() throws Exception {
    final TestSlashCommand bean = spy(new TestSlashCommand());
    final Method publicMethod = bean.getClass().getDeclaredMethod("withStringArgument", CommandContext.class, String.class);

    final String argumentName = "arg";
    final String argumentValue = "value";
    final Consumer<CommandContext> callback = SlashAnnotationProcessor.createSlashCommandCallback(bean, publicMethod,
        Collections.singletonMap(argumentName, 1));

    final CommandContext mock = mock(CommandContext.class);
    when(mock.getArguments()).thenReturn(new Arguments(Collections.singletonMap(argumentName, argumentValue)));

    callback.accept(mock);

    verify(bean).withStringArgument(any(CommandContext.class), eq(argumentValue));
  }

  @Test
  void testExecuteCallbackWithError() throws Exception {
    final TestSlashCommand bean = spy(new TestSlashCommand());
    final Method publicMethod = bean.getClass().getDeclaredMethod("onErrorTest", CommandContext.class);
    final Consumer<CommandContext> callback = SlashAnnotationProcessor.createSlashCommandCallback(bean, publicMethod,
        Collections.emptyMap());
    callback.accept(mock(CommandContext.class));
    verify(bean, never()).onTest(any(CommandContext.class));
  }
}
