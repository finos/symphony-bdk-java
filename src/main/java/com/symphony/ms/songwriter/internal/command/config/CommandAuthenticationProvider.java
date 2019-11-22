package com.symphony.ms.songwriter.internal.command.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.symphony.ms.songwriter.internal.command.AuthenticatedCommandHandler;
import com.symphony.ms.songwriter.internal.command.AuthenticationProvider;

/**
 * Annotation used to define which {@link AuthenticationProvider}
 * implementation to use in each {@link AuthenticatedCommandHandler}.
 *
 * THIS ANNOTATION IS NOT REQUIRED IF ONLY ONE {@link AuthenticationProvider}
 * IS PROVIDED.
 *
 * @author Marcus Secato
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAuthenticationProvider {
  public String name();
}
