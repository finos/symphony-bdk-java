---
layout: default
title: Spring Boot Core Starter
parent: Spring Boot Starters
nav_order: 1
---

# BDK Core Spring Boot Starter
The Symphony BDK for Java provides a _Starter_ module that aims to ease bot developments within a
[Spring Boot](https://spring.io/projects/spring-boot) application.

## Features
- Configure bot environment through `application.yaml`
- Subscribe to Real Time Events from anywhere
- Provide injectable services
- Ease activities creation
- Provide `@Slash` annotation to register a [slash command](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/activity/command/SlashCommand.html)

## Installation

The following listing shows the `pom.xml` file that has to be created when using Maven:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>bdk-core-spring-boot</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bdk-core-spring-boot</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.finos.symphony.bdk</groupId>
                <artifactId>symphony-bdk-bom</artifactId>
                <version>2.12.0-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.finos.symphony.bdk</groupId>
            <artifactId>symphony-bdk-core-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        // integration test dependency
        <dependency>
            <groupId>org.finos.symphony.bdk</groupId>
            <artifactId>symphony-bdk-test-spring-boot</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>2.3.4.RELEASE</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```
The following listing shows the `build.gradle` file that has to be created when using Gradle:
```groovy
plugins {
    id 'java-library'
    id 'org.springframework.boot' version '2.3.4.RELEASE'
}

dependencies {
    implementation platform('org.finos.symphony.bdk:symphony-bdk-bom:2.12.0-SNAPSHOT')

    implementation 'org.finos.symphony.bdk:symphony-bdk-core-spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter'

    // integration test dependency
    testImplementation 'org.finos.symphony.bdk:symphony-bdk-test-spring-boot'
}
```

## Create a Simple Bot Application
As a first step, you have to initialize your bot environment through the Spring Boot `src/main/resources/application.yaml` file:
```yaml
bdk:
    host: acme.symphony.com
    bot:
      username: bot-username
      privateKey:
        path: /path/to/rsa/privatekey.pem

logging:
  level:
    com.symphony: debug # in development mode, it is strongly recommended to set the BDK logging level at DEBUG
```
> You can notice here that the `bdk` property inherits from the [`BdkConfig`](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-config/latest/com/symphony/bdk/core/config/model/BdkConfig.html) class.

As required by Spring Boot, you have to create an `src/main/java/com/example/bot/BotApplication.java` class:
```java
@SpringBootApplication
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Now you can create a component for a simple bot application, as the following listing (from `src/main/java/com/example/bot/HelloBot.java`)
shows:
```java
@Component
public class HelloBot {

  @Autowired
  private MessageService messageService;

  @EventListener
  public void onMessageSent(RealTimeEvent<? extends V4MessageSent> event) {
    log.info("event was triggered at {}", ((EventPayload) event.getSource()).getEventTimestamp());
    this.messageService.send(event.getSource().getMessage().getStream(), "Hello!");
  }
}
``` 

You can finally run your Spring Boot application and verify that your bot always replies with `Hello!`. It also worth noting
that the event timestamp is only accessible from `EventPayload` type, you need simply cast the source event to it, and
call `getEventTimestamp()` method to read the value, as you can see from the example here.

### OBO (On behalf of) usecases
It is possible to run an application with no bot service account configured in order to accommodate OBO usecases only.
For instance the following configuration is valid:
```yaml
bdk:
    host: acme.symphony.com
    app:
      appId: app-id
      privateKey:
        path: /path/to/rsa/privatekey.pem
```

This will cause all features related to the datafeed loop such as Real Time Events, activities, slash commands, etc. to be deactivated.
However, service beans with OBO-enabled endpoints will be available and can be used as following:
```java
@Component
public class OboUsecase {

  @Autowired
  private MessageService messageService;

  @Autowired
  private OboAuthenticator oboAuthenticator;

  public void doStuff() {
      final AuthSession oboSession = oboAuthenticator.authenticateByUsername("user.name");
      final V4Message message = messageService.obo(oboSession).send("stream.id", "Hello from OBO"); // works

      messageService.send("stream.id", "Hello world"); // fails with an IllegalStateException
  }
}
```

Any attempt to use a non-OBO service endpoint will fail with an IllegalStateException.

## Subscribe to Real Time Events
The Core Starter uses [Spring Events](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationEventPublisher.html)
to deliver Real Time Events.

You can subscribe to any Real Time Event from anywhere in your application by creating a handler method that has to
respect two conditions:
- be annotated with [@EventListener](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/EventListener.html)
- have `com.symphony.bdk.spring.events.RealTimeEvent<? extends T>` parameter

The listener methods will be called with events from the [datafeed loop](../datafeed.html#datafeed) or the
[datahose loop](../datafeed.html#datahose) (or both) depending on your configuration:
```yaml
bdk:
    datafeed:
        enabled: true # optional, defaults to true
    datahose:
        enabled: true # optional, defaults to false
```
If both datafeed and datahose are enabled, application will fail at startup. So please make sure datafeed is disabled
when using datahose.

Here's the list of Real Time Events you can subscribe:
```java
@Component
public class RealTimeEvents {

  @EventListener
  public void onMessageSent(RealTimeEvent<? extends V4MessageSent> event) {}

  @EventListener
  public void onSharedPost(RealTimeEvent<? extends V4SharedPost> event) {}

  @EventListener
  public void onInstantMessageCreated(RealTimeEvent<? extends V4InstantMessageCreated> event) {}

  @EventListener
  public void onRoomCreated(RealTimeEvent<? extends V4RoomCreated> event) {}

  @EventListener
  public void onRoomUpdated(RealTimeEvent<? extends V4RoomUpdated> event) {}

  @EventListener
  public void onRoomDeactivated(RealTimeEvent<? extends V4RoomDeactivated> event) {}

  @EventListener
  public void onRoomReactivated(RealTimeEvent<? extends V4RoomReactivated> event) {}

  @EventListener
  public void onUserRequestedToJoinRoom(RealTimeEvent<? extends V4UserRequestedToJoinRoom> event) {}

  @EventListener
  public void onUserJoinedRoom(RealTimeEvent<? extends V4UserJoinedRoom> event) {}

  @EventListener
  public void onUserLeftRoom(RealTimeEvent<? extends V4UserLeftRoom> event) {}

  @EventListener
  public void onRoomMemberPromotedToOwner(RealTimeEvent<? extends V4RoomMemberPromotedToOwner> event) {}

  @EventListener
  public void onRoomMemberDemotedFromOwner(RealTimeEvent<? extends V4RoomMemberDemotedFromOwner> event) {}

  @EventListener
  public void onConnectionRequested(RealTimeEvent<? extends V4ConnectionRequested> event) {}

  @EventListener
  public void onConnectionAccepted(RealTimeEvent<? extends V4ConnectionAccepted> event) {}

  @EventListener
  public void onMessageSuppressed(RealTimeEvent<? extends V4MessageSuppressed> event) {}

  @EventListener
  public void onSymphonyElementsAction(RealTimeEvent<? extends V4SymphonyElementsAction> event) {}
}
```

By default, the RealTimeEvents are going to be processed asynchronously in the listeners, in case this is not the preferred behavior, one
can deactivate it by updating the application.yaml file as
```yaml
bdk:
    datafeed:
        event:
            async: false # optional, defaults to true
```
The same applies for `bdk.datahose` configuration.

## Inject Services
The Core Starter injects services within the Spring application context:
```java
@Service
public class CoreServices {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private StreamService streamService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DatafeedService datafeedService;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private ActivityRegistry activityRegistry;
}
```

Unlike subscribing to real time events, using slash commands or activities, solely injecting services does not require
the datafeed loop to run. If you want to disable the datafeed loop, you can update your `application.yaml` file as follows:
```yaml
bdk:
    datafeed:
        enabled: false
```

:warning: Disabling the datafeed loop will prevent the use of real time event listeners, of slash commands and activities.

## Slash Command
You can easily register a slash command using the `@Slash` annotation. Note that the `CommandContext` is mandatory to
successfully register your command. If not defined, a `warn` message will appear in your application log. Note also that
only beans with scope **singleton** will be scanned.

```java
@Component
public class SlashHello {

  @Slash("/hello")
  public void onHello(CommandContext commandContext) {
    log.info("On /hello command sent at {}", commandContext.getEventTimestamp());
  }

  @Slash(value = "/hello", mentionBot = false)
  public void onHelloNoMention(CommandContext commandContext) {
    log.info("On /hello command (bot has not been mentioned)");
  }
}
```
By default, the `@Slash` annotation is configured to require bot mention in order to trigger the command. You can override
this value using `@Slash#mentionBot` annotation parameter.

You can also use slash commands with arguments. To do so, the field `value` of the `@Slash` annotation must have a valid
format as explained in the [Activity API section](../activity-api.html#slash-command-pattern-format).
If the slash command pattern is valid, you will have to specify all slash arguments as method parameter with the same name and type.
If slash command pattern or method signature is incorrect, a `warn` message will appear in your application log and
the slash command will not be registered. Note that the event timestamp is accessible from the `commandContext` using
`getEventTimestamp()` method.

For instance:
```java
@Component
public class SlashHello {

  @Slash("/hello {arg") // will not be registered: invalid pattern
  public void onHelloInvalidPattern(CommandContext commandContext, String arg) {
    log.info("On /hello command");
  }

  @Slash("/hello {arg1}{arg2}") // will not be registered: invalid pattern
  public void onHelloInvalidPatternTwoArgs(CommandContext commandContext, String arg1, String arg2) {
    log.info("On /hello command");
  }

  @Slash("/hello {arg1} {arg2}") // will be registered: valid pattern and valid signature
  public void onHelloValidPatternTwoArgs(CommandContext commandContext, String arg1, String arg2) {
    log.info("On /hello command");
  }

  @Slash("/hello {arg1} {arg2}") // will not be registered: valid pattern but missing argument
  public void onHelloValidPatternTwoArgs(CommandContext commandContext, String arg1) {
    log.info("On /hello command");
  }

  @Slash("/hello {arg1} {@arg2}") // will not be registered: valid pattern but mismatching type for arg2
  public void onHelloValidPatternTwoArgs(CommandContext commandContext, String arg1, String arg2) {
    log.info("On /hello command");
  }

  @Slash("/hello {arg1} {@arg2} {#arg3} {$arg4}") // will be registered: valid pattern and correct signature
  public void onHelloValidPatternTwoArgs(CommandContext commandContext, String arg1, Mention arg2, Hashtag arg3, Cashtag arg4) {
    log.info("On /hello command");
  }
}
```

:information_source: Slash commands are not registered to the datahose loop even when enabled.

## Asynchronous slash Command
By default, `@Slash` annotation is configured to be synchronous. If the process takes time, the next incoming commands
will be blocked and enqueued till the process is released. If this is a concern, Slash command can be configured to be
asynchronous by setting the `async` option to the annotation.

```java
@Slf4j
@Component
public class AsyncActivity  {

  @Autowired
  private MessageService messageService;

  @Slash(value = "/async", asynchronous = true)
  public void async(CommandContext context) throws InterruptedException {
    this.messageService.send(context.getStreamId(),
            "I will simulate a heavy process that takes time but this should not block next commands");

    sleep(30000);

    this.messageService.send(context.getStreamId(), "Heavy async process is done");
  }
}
```

## Activities
> For more details about activities, please read the [Activity API reference documentation](../activity-api.html)

Any service or component class that extends [`FormReplyActivity`](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/activity/form/FormReplyActivity.html)
or [`CommandActivity`](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/activity/command/CommandActivity.html)
will be automatically registered within the [ActivityRegistry](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/activity/ActivityRegistry.html).

:information_source: Activities are not registered to the datahose loop even when enabled.

### Example of a `CommandActivity` in Spring Boot
The following example has been described in section [Activity API documentation](../activity-api.html#how-to-create-a-command-activity).
Note here that with Spring Boot you simply have to annotate your `CommandActivity` class with `@Component` to make it
automatically registered in the `ActivityRegistry`,
```java
@Slf4j
@Component
public class HelloCommandActivity extends CommandActivity<CommandContext> {

  @Override
  protected ActivityMatcher<CommandContext> matcher() {
    return c -> c.getTextContent().contains("hello");
  }

  @Override
  protected void onActivity(CommandContext context) {
    log.info("Hello command triggered by user {}", context.getInitiator().getUser().getDisplayName());
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.COMMAND).name("Hello Command");
  }
}
```

### Example of a `FormReplyActivity` in Spring Boot
The following example demonstrates how to send an Elements form on `@BotMention /gif` slash command. The Elements form
located in `src/main/resources/templates/gif.ftl` contains:
```xml
<messageML>
    <h2>Gif Generator</h2>
    <form id="gif-category-form">

        <text-field name="category" placeholder="Enter a Gif category..."/>

        <button name="submit" type="action">Submit</button>
        <button type="reset">Reset Data</button>

    </form>
</messageML>
```

```java
@Slf4j
@Component
public class GifFormActivity extends FormReplyActivity<FormReplyContext> {

  @Autowired
  private MessageService messageService;

  @Slash("/gif")
  public void displayGifForm(CommandContext context) throws TemplateException {
    final Template template = bdk.messages().templates().newTemplateFromClasspath("/templates/gif.ftl");
    this.messageService.send(context.getStreamId(), Message.builder().template(template, Collections.emptyMap()));
  }

  @Override
  public ActivityMatcher<FormReplyContext> matcher() {
    return context -> "gif-category-form".equals(context.getFormId())
        && "submit".equals(context.getFormValue("action"))
        && StringUtils.isNotEmpty(context.getFormValue("category"));
  }

  @Override
  public void onActivity(FormReplyContext context) {
    log.info("Gif category is \"{}\"", context.getFormValue("category"));
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.FORM)
        .name("Gif Display category form command")
        .description("\"Form handler for the Gif Category form\"");
  }
}
```

# Integration Test

You can then create the integration test to guarantee the Bot application is working as design,
like you can see in the example below. For more details, please refer to [test module](../test.html).

```java
@SymphonyBdkSpringBootTest(properties = {"bot.id=1", "bot.username=my-bot", "bot.display-name=my bot"})
public class SimpleSpringAppIntegrationTest {
    private final V4User initiator = new V4User().displayName("user").userId(2L);
    private final V4Stream stream = new V4Stream().streamId("my-room");

    @Test
    void echo_command_replyWithMessage(@Autowired MessageService messageService, @Autowired UserV2 botInfo) {
        // (1)  given
        when(messageService.send(anyString(), any(Message.class))).thenReturn(mock(V4Message.class));

        // (2)  when
        pushMessageToDF(initiator, stream, "/echo arg", botInfo);

        // (3)  then
        verify(messageService).send(eq("my-room"), eq("Received argument: arg"));
    }
}
```

----
[Home :house:](../index.html)
