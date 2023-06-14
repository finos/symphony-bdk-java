---
layout: default
title: Extending the BDK
nav_order: 18
---

# Extension Model
> :bulb: since `2.6`

> :warning: The BDK Extension Mechanism is still an experimental feature, contracts might be subject to **breaking changes**
> in following versions.

## Overview
The BDK extension model consists of a simple concept: the `BdkExtension` API. Note, however, that `BdkExtension`
itself is just a marker interface.

The `BdkExtension` API is available through the module `:symphony-bdk-extension-api` but other modules might be required
depending on what your extension needs to use.

## Registering Extensions
Extensions are registered _programmatically_ via the `ExtensionService`:
```java
class ExtensionExample {

    public static void main(String[] args) {
        // using the ExtensionService
        final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
        bdk.extensions().register(MyBdkExtension.class);

        // or using the SymphonyBdkBuilder
        final SymphonyBdk bdk = SymphonyBdk.builder()
                .config(loadFromSymphonyDir("config.yaml"))
                .extension(MyBdkExtension.class)
                .build();
    }
}
```

### Registering Extensions in Spring Boot
To use your extension in the [BDK Spring Boot Starter](./spring-boot/core-starter.html), you simply need to register your
extension as a bean added to the application context. Note that your extension class must implement `BdkExtension` in order
to automatically be registered:
```java
@Configuration
public class MyBdkExtensionConfig {

  @Bean
  public MyBdkExtension myBdkExtension() {
    return new MyBdkExtension();
  }
}
```
This way, your extension will automatically be registered within the `ExtensionService`.

## Service Provider Extension
A _Service Provider_ extension is a specific type of extension loaded on demand when calling the
`ExtensionService#service(Class)` method.

To make your extension _Service Provider_, your extension definition class must implement the `BdkExtensionServiceProvider`
interface along with the `BdkExtension` marker interface:
```java
/**
 * The Service implementation class.
 */
public class MyBdkExtensionService implements BdkExtensionService {

    public void sayHello(String name) {
        System.out.println("Hello, %s!", name); // #noLog4Shell
    }
}
/**
 * The Extension definition class.
 */
public class MyBdkExtension implements BdkExtension, BdkExtensionServiceProvider<MyBdkExtensionService> {

    private final MyBdkExtensionService service = new MyBdkExtensionService();

    @Override
    public MyBdkExtensionService getService() {
        return this.service;
    }
}
/**
 * Usage example.
 */
class ExtensionExample {

    public static void main(String[] args) {
        final SymphonyBdk bdk = SymphonyBdk.builder()
                .config(loadFromSymphonyDir("config.yaml"))
                .extension(MyBdkExtension.class)
                .build();

        final MyBdkExtensionService service = bdk.extensions().service(MyBdkExtension.class);
        service.sayHello("Symphony");
    }
}
```

### Access your Extension's service in Spring Boot
In Spring Boot, your extension's service is _lazily_ initialized. It means that you must annotate your injected extension's service
field with the `@Lazy` annotation in addition to the `@Autowired` one:
```java
@Configuration
public class MyBdkExtensionConfig {

  @Bean
  public MyBdkExtension myBdkExtension() {
    return new MyBdkExtension();
  }
}

@RestController
@RequestMapping("/api")
public class ApiController {

    @Lazy // required, otherwise Spring Boot application startup will fail
    @Autowired
    private MyBdkExtensionService groupService;
}
```
> :bulb: Note that your IDE might show an error like "_Could not autowire. No beans of 'MyBdkExtensionService' type found_".
> To disable this warning you can annotate your class with `@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")`

## BDK Aware Extensions
The BDK Extension Model allows extensions to access to some core objects such as the configuration or the api clients.
Developers that wish to use these objects a free to implement a set of interfaces all suffixed with the `Aware` keyword.

### `BdkConfigAware`
The interface `com.symphony.bdk.core.config.extension.BdkConfigAware` allows extensions to read the BDK configuration:
```java
public class MyBdkExtension implements BdkExtension, BdkConfigAware {

    private BdkConfig config;

    @Override
    public void setConfiguration(BdkConfig config) {
        this.config = config;
    }
}
```

### `BdkApiClientFactoryAware`
The interface `com.symphony.bdk.core.extension.BdkApiClientFactoryAware` can be used by extensions that need to
use the `com.symphony.bdk.core.client.ApiClientFactory` class:
```java
public class MyBdkExtension implements BdkExtension, BdkApiClientFactoryAware {

    private ApiClientFactory apiClientFactory;

    @Override
    public void setApiClientFactory(ApiClientFactory apiClientFactory) {
        this.apiClientFactory = apiClientFactory;
    }
}
```

### `BdkAuthenticationAware`
The interface `com.symphony.bdk.core.extension.BdkAuthenticationAware` can be used by extensions that need to rely on the
service account authentication session (`com.symphony.bdk.core.auth.AuthSession`), which provides the `sessionToken` and
`keyManagerToken` that are used to call the Symphony's APIs:
```java
public class MyBdkExtension implements BdkExtension, BdkAuthenticationAware {

    private AuthSession authSession;

    @Override
    public void setAuthSession(AuthSession authSession) {
        this.authSession = authSession;
    }
}
```

### `BdkRetryBuilderAware`
The interface `com.symphony.bdk.core.extension.BdkRetryBuilderAware` allows extensions to leverage the internal BDK retry API
through the `com.symphony.bdk.core.retry.RetryWithRecoveryBuilder<?>` class:
```java
public class MyBdkExtension implements BdkExtension, BdkRetryBuilderAware {

    private RetryWithRecoveryBuilder<?> retryBuilder;

    @Override
    public void setRetryBuilder(RetryWithRecoveryBuilder<?> retryBuilder) {
        this.retryBuilder = retryBuilder;
    }
}
```
