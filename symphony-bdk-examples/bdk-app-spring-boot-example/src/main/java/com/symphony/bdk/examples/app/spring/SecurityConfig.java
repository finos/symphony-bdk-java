package com.symphony.bdk.examples.app.spring;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // This configuration is not recommended in production setup
    http.authorizeRequests().anyRequest().permitAll();
    http.csrf().disable();
    http.headers().frameOptions().disable();
  }
}
