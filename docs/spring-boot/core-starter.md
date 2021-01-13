# BDK Core Spring Boot Starter
The Symphony BDK for Java provides a _Starter_ module that aims to ease bot developments within a 
[Spring Boot](https://spring.io/projects/spring-boot) application. 

## Features
- Configure bot environment through `application.yaml`
- Subscribe to Real Time Events from anywhere
- Provide injectable services
- Ease activities creation
- Provide `@Slash` annotation to register a [slash command](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core/latest/com/symphony/bdk/core/activity/command/SlashCommand.html)

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
                <groupId>com.symphony.platformsolutions</groupId>
                <artifactId>symphony-bdk-bom</artifactId>
                <version>1.3.2.BETA</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>com.symphony.platformsolutions</groupId>
            <artifactId>symphony-bdk-core-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
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
    implementation platform('com.symphony.platformsolutions:symphony-bdk-bom:2.0.0')
    
    implementation 'com.symphony.platformsolutions:symphony-bdk-core-spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter'
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
> You can notice here that the `bdk` property inherits from the [`BdkConfig`](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core/latest/com/symphony/bdk/core/config/model/BdkConfig.html) class.

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
  public void onMessageSent(RealTimeEvent<V4MessageSent> event) {
    this.messageService.send(event.getSource().getMessage().getStream(), "<messageML>Hello!</messageML>");
  }
}
``` 

You can finally run your Spring Boot application and verify that your bot always replies with `Hello!`. 

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
- have `com.symphony.bdk.spring.events.RealTimeEvent<T>` parameter

Here's the list of Real Time Events you can subscribe:
```java
@Component
public class RealTimeEvents {

  @EventListener
  public void onMessageSent(RealTimeEvent<V4MessageSent> event) {}

  @EventListener
  public void onSharedPost(RealTimeEvent<V4SharedPost> event) {}

  @EventListener
  public void onInstantMessageCreated(RealTimeEvent<V4InstantMessageCreated> event) {}

  @EventListener
  public void onRoomCreated(RealTimeEvent<V4RoomCreated> event) {}

  @EventListener
  public void onRoomUpdated(RealTimeEvent<V4RoomUpdated> event) {}

  @EventListener
  public void onRoomDeactivated(RealTimeEvent<V4RoomDeactivated> event) {}

  @EventListener
  public void onRoomReactivated(RealTimeEvent<V4RoomReactivated> event) {}

  @EventListener
  public void onUserRequestedToJoinRoom(RealTimeEvent<V4UserRequestedToJoinRoom> event) {}

  @EventListener
  public void onUserJoinedRoom(RealTimeEvent<V4UserJoinedRoom> event) {}

  @EventListener
  public void onUserLeftRoom(RealTimeEvent<V4UserLeftRoom> event) {}

  @EventListener
  public void onRoomMemberPromotedToOwner(RealTimeEvent<V4RoomMemberPromotedToOwner> event) {}

  @EventListener
  public void onRoomMemberDemotedFromOwner(RealTimeEvent<V4RoomMemberDemotedFromOwner> event) {}

  @EventListener
  public void onConnectionRequested(RealTimeEvent<V4ConnectionRequested> event) {}

  @EventListener
  public void onConnectionAccepted(RealTimeEvent<V4ConnectionAccepted> event) {}

  @EventListener
  public void onMessageSuppressed(RealTimeEvent<V4MessageSuppressed> event) {}

  @EventListener
  public void onSymphonyElementsAction(RealTimeEvent<V4SymphonyElementsAction> event) {}
}
```

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
    log.info("On /hello command");
  }

  @Slash(value = "/hello", mentionBot = false)
  public void onHelloNoMention(CommandContext commandContext) {
    log.info("On /hello command (bot has not been mentioned)");
  }
}
```
By default, the `@Slash` annotation is configured to require bot mention in order to trigger the command. You can override
this value using `@Slash#mentionBot` annotation parameter. 

## Activities
> For more details about activities, please read the [Activity API reference documentation](../activity-api.md)

Any service or component class that extends [`FormReplyActivity`](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core/latest/com/symphony/bdk/core/activity/form/FormReplyActivity.html) 
or [`CommandActivity`](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core/latest/com/symphony/bdk/core/activity/command/CommandActivity.html) 
will be automatically registered within the [ActivityRegistry](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core/latest/com/symphony/bdk/core/activity/ActivityRegistry.html).

### Example of a `CommandActivity` in Spring Boot
The following example has been described in section [Activity API documentation](../activity-api.md#how-to-create-a-command-activity).
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

----
[Home :house:](../index.md)
