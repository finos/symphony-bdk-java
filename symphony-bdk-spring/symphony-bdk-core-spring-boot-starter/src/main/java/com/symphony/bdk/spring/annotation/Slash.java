package com.symphony.bdk.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows developers to easily define {@link com.symphony.bdk.core.activity.command.SlashCommand}. To be
 * used, this annotation has to be put on a method with a single {@link com.symphony.bdk.core.activity.command.CommandContext}
 * parameter.
 * <p>
 *   Example:
 *   <pre>
 *     &#64;Slash("/hello")
 *     public void onHello(CommandContext context) {
 *        // process command
 *     }
 *   </pre>
 * </p>
 * <p>
 *   Note: Annotated method has to be <code>public</code> and must be part of a bean.
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Slash {

  /**
   * Pattern of the {@link com.symphony.bdk.core.activity.command.SlashCommand}.
   */
  String value();

  /**
   * True if bot needs to be mentioned to trigger the command, false otherwise.
   */
  boolean mentionBot() default true;

  /**
   * Description of the {@link com.symphony.bdk.core.activity.command.SlashCommand}
   */
  String description() default "";

  boolean asynchronous() default false;
}
