package com.symphony.bdk.core.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a service method is called in OBO mode.
 * @see <a href=" https://developers.symphony.com/restapi/docs/get-started-with-obo">Get Started With OBO</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OboEnabled {

}
