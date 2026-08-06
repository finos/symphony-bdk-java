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

### `BdkAware`
The interface `com.symphony.bdk.core.extension.BdkAware` gives an extension access to the fully constructed
`SymphonyBdk` instance. `setBdk(SymphonyBdk)` is called *before* `BdkExtensionLifecycle#onBdkStarted()`
(see [Extension Lifecycle](#extension-lifecycle)), so the reference is guaranteed to be available when the
lifecycle callback fires:
```java
public class MyBdkExtension implements BdkExtension, BdkAware {

    private SymphonyBdk bdk;

    @Override
    public void setBdk(SymphonyBdk bdk) {
        this.bdk = bdk;
    }
}
```
> :warning: Earlier versions provided a `BdkConfigAware` interface to read the BDK configuration. It has been
> **removed**. To consume configuration, use typed [Extension Configuration](#typed-extension-configuration)
> via `BdkExtensionConfigAware<C>` instead.

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

## Extension Lifecycle
An extension can receive startup and shutdown callbacks by implementing
`com.symphony.bdk.extension.BdkExtensionLifecycle`. Both methods have default no-op implementations, so you only
override the ones you need:
```java
public class MyBdkExtension implements BdkExtension, BdkExtensionLifecycle {

    @Override
    public void onBdkStarted() {
        // called once the SymphonyBdk instance is fully constructed
        // e.g. start background workers, open connections
    }

    @Override
    public void onBdkStopped() {
        // called when the JVM shutdown hook fires
        // e.g. release resources, flush buffers
    }
}
```
- `onBdkStarted()` is invoked after `SymphonyBdk` is fully built and after any `BdkAware#setBdk(...)` injection.
- `onBdkStopped()` is invoked from a JVM shutdown hook registered by the BDK.

## Typed Extension Configuration
Extensions can declare their own configuration block in `bdk-config.yaml` under `bdk.extensions.<key>` and receive
it as a typed object by implementing `com.symphony.bdk.extension.BdkExtensionConfigAware<C>`.

Given the following configuration:
```yaml
# bdk-config.yaml
host: acme.symphony.com
bot:
  username: bot-user
  privateKey:
    path: /path/to/rsa/privatekey.pem

extensions:
  myExtension:            # <-- matches getConfigKey()
    apiUrl: https://internal.acme.com/api
    timeoutMillis: 5000
```
the extension declares its config key and class:
```java
public class MyBdkExtension implements BdkExtension, BdkExtensionConfigAware<MyBdkExtension.Config> {

    @Getter
    @Setter
    public static class Config {
        private String apiUrl;
        private int timeoutMillis;
    }

    private Config config;

    @Override
    public String getConfigKey() {
        return "myExtension";
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public void setExtensionConfig(Config config) {
        this.config = config;
    }
}
```
The BDK deserializes the `bdk.extensions.myExtension` block into a `Config` instance and calls
`setExtensionConfig(...)` *before* `onBdkStarted()`. If the key is absent or the block cannot be deserialized into
`C`, a `BdkExtensionException` is thrown at startup.

## Overriding Core Behavior
Some extensions need to change *how* the BDK talks to Symphony rather than just consuming BDK services. Two
Service Provider Interfaces (SPIs) allow an extension to fully replace a core interaction.

> :warning: Capability-providing extensions **must** be pre-registered via `SymphonyBdkBuilder.extension(Class)` so
> the capability can be wired into the relevant service at construction time. If registered post-construction with
> `bdk.extensions().register(...)`, a warning is logged and the capability has **no effect**.

### Message Sender Override
An extension implementing `com.symphony.bdk.core.extension.BdkMessageSenderOverrideProvider` supplies a
`MessageSenderOverride` that replaces **all** agent-facing message operations in `MessageService`. While an
override is active, the agent `MessagesApi` is never called for those operations — the public `MessageService` API
is unchanged. A single override instance handles both bot-context and OBO-context calls: on each call,
`MessageService` passes the `AuthSession` it is currently operating under as the first parameter — the bot session
for a bot-context `MessageService`, or the OBO session for a `MessageService` obtained via `SymphonyBdk.obo(...)`,
`OboServices.messages()`, or `MessageService.obo(...)`. Implementations route bot vs. OBO behavior from this
parameter, keeping the instance stateless and safe under concurrent bot/OBO use.
```java
public class MyBdkExtension
    implements BdkExtension, BdkMessageSenderOverrideProvider {

    private final MessageSenderOverride override = new MyMessageSenderOverride();

    @Override
    public MessageSenderOverride getMessageSenderOverride() {
        return this.override;
    }
}

public class MyMessageSenderOverride implements MessageSenderOverride {

    @Override
    public V4Message send(AuthSession session, String streamId, Message message) throws Exception { /* ... */ }

    @Override
    public V4Message update(AuthSession session, String streamId, String messageId, Message content)
        throws Exception { /* ... */ }

    @Override
    public V4MessageBlastResponse blast(AuthSession session, List<String> streamIds, Message message)
        throws Exception { /* ... */ }

    @Override
    public List<V4ImportResponse> importMessages(AuthSession session, List<V4ImportedMessage> messages)
        throws Exception { /* ... */ }

    @Override
    public MessageSuppressionResponse suppressMessage(AuthSession session, String messageId) throws Exception { /* ... */ }

    @Override
    public byte[] getAttachment(AuthSession session, String streamId, String messageId, String attachmentId)
        throws Exception { /* ... */ }
}
```
> :bulb: If more than one registered extension provides a `MessageSenderOverride`, the first one registered is used
> and a warning is logged.

### Message Retriever Override
An extension implementing `com.symphony.bdk.core.extension.BdkMessageRetrieverOverrideProvider` supplies a
`MessageRetrieverOverride` that replaces **all** agent-facing message *read* operations in `MessageService` —
`listMessages`, `searchMessages`, `searchMessagesSemantic` and `getMessage`. While an override is active, the agent
`MessagesApi` is never called for those operations — the public `MessageService` API is unchanged. A single override
instance handles both bot-context and OBO-context calls, routing from the `AuthSession` passed as the first
parameter of each method — the same per-call session-passing scheme as `MessageSenderOverride` above. Pod-backed
reads (`getMessageStatus`, `listAttachments`, `listMessageReceipts`, `getMessageRelationships`, `getAttachmentTypes`)
are unaffected.

`MessageRetrieverOverride` is independent from `MessageSenderOverride`: an extension may provide one, the other, or
both — for example to route reads through a local cache while sending normally through the agent.
```java
public class MyBdkExtension
    implements BdkExtension, BdkMessageRetrieverOverrideProvider {

    private final MessageRetrieverOverride override = new MyMessageRetrieverOverride();

    @Override
    public MessageRetrieverOverride getMessageRetrieverOverride() {
        return this.override;
    }
}

public class MyMessageRetrieverOverride implements MessageRetrieverOverride {

    @Override
    public List<V4Message> listMessages(AuthSession session, String streamId, Instant since, Instant until,
        PaginationAttribute pagination) throws Exception { /* ... */ }

    @Override
    public List<V4Message> searchMessages(AuthSession session, MessageSearchQuery query,
        PaginationAttribute pagination, SortDir sortDir) throws Exception { /* ... */ }

    @Override
    public List<V4Message> searchMessagesSemantic(AuthSession session, String query, String streamId,
        PaginationAttribute pagination) throws Exception { /* ... */ }

    @Override
    public V4Message getMessage(AuthSession session, String messageId) throws Exception { /* ... */ }
}
```
> :bulb: If more than one registered extension provides a `MessageRetrieverOverride`, the first one registered is
> used and a warning is logged.

> :bulb: To fully replace agent-facing message handling (both directions), implement `BdkMessageSenderOverrideProvider`
> and `BdkMessageRetrieverOverrideProvider` on the same extension class, backed by one shared object that implements
> both `MessageSenderOverride` and `MessageRetrieverOverride`.

### Datafeed Event Source
An extension implementing `com.symphony.bdk.core.extension.BdkDatafeedEventSourceProvider` supplies a
`DatafeedEventSource` that replaces the entire datafeed read/ack cycle in `DatafeedLoopV2`. The source is
**stateless** — there is no persistent datafeed ID. The loop starts with a `null` ackId on the first iteration and
threads the ackId returned by `ackEvents(...)` into the next `readEvents(...)` call. Event dispatch and the retry
policy are unchanged.
```java
public class MyBdkExtension
    implements BdkExtension, BdkDatafeedEventSourceProvider {

    private final DatafeedEventSource source = new MyDatafeedEventSource();

    @Override
    public DatafeedEventSource getDatafeedEventSource() {
        return this.source;
    }
}

public class MyDatafeedEventSource implements DatafeedEventSource {

    @Override
    public List<V4Event> readEvents(String ackId) throws Exception {
        // return already-decrypted events; subject to the standard read retry policy
    }

    @Override
    public String ackEvents(List<V4Event> events) throws Exception {
        // return the ackId to pass into the next readEvents(...) call
    }
}
```

### Wiring a capability-providing extension
```java
class ExtensionExample {

    public static void main(String[] args) {
        final SymphonyBdk bdk = SymphonyBdk.builder()
                .config(loadFromSymphonyDir("config.yaml"))
                .extension(MyBdkExtension.class) // pre-registered before services are built
                .build();
    }
}
```
