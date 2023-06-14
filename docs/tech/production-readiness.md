---
layout: default
title: Production Readiness
parent: Tech Appendix
nav_order: 2
---

# Production Readiness
Production readiness documentation for BDK-based applications.

## Logging
The Symphony BDK sets up your logger [MDC](http://logback.qos.ch/manual/mdc.html) (Mapped Diagnostic Context) with a value
called `X-Trace-Id` (random alphanumeric string of 6 characters). This value is send as header of every request made to
the Symphony API. This is especially useful for cross-applications debugging, assuming that the `X-Trace-Id` value is
also present in your application logs.

As you are obviously free to use your preferred logging technology, the next sections will help you to print the
`X-Trace-Id` using either [logback](http://logback.qos.ch/) or [Log4j2](https://logging.apache.org/log4j/2.x/).

### Logback
For [logback](http://logback.qos.ch/), you will print the `X-Trace-Id` value by adding `%X{X-Trace-Id}` to your log pattern:
```xml
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %X{X-Trace-Id} %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="debug">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

### Log4j2
For [Log4j2](https://logging.apache.org/log4j/2.x/), you will print the `X-Trace-Id` value by adding `%X{X-Trace-Id}` to
your log pattern:
```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %X{X-Trace-Id} %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

----
[Home :house:](../index.html)
