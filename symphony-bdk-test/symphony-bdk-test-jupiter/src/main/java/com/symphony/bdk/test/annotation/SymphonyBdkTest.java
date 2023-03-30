package com.symphony.bdk.test.annotation;

import com.symphony.bdk.test.junit.jupiter.SymphonyBdkExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MockitoExtension.class)
@ExtendWith(SymphonyBdkExtension.class)
@Target(ElementType.TYPE)
public @interface SymphonyBdkTest {

  long botId() default 1L;

  String botName() default "bdk-bot";

  String botDisplayName() default "BDK Bot";
}
