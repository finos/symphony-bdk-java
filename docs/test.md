---
layout: default
title: Integration Tests
nav_order: 19
---

# Symphony BDK Test

This guide provides information to Symphony BDK Bot developers about how using Symphony BDK Test to build the
integration tests for the Bots.

> **Note**: This module is still under construction, currently it only supports SpringBoot based Bots, we are going to
> provide the same support for java based Bots very soon.

## SpringBoot based Bots Integration tests

To create an integration test, all developer needs is to add the annotation `@SymphonyBdkSpringBootTest` on top of the
test class, when the test is launched, a SpringBoot application context is going to be loaded, all BDK services are
being injected in this application context.

Developer can then `@Autowired` these BDK services beans whenever they are needed in the test, please note that these
services beans are simply **Mockito** mocked objects, they have to be stubbed just like the way how we do in a simple
JUnit test.

The annotation `@SymphonyBdkSpringBootTest` comes with a properties array attribute, which allows to define the Bot
information, such as `bot.id`, `bot.username`, and `bot.display-name`, the additional SpringBoot Bot application
properties can also be provided in this array (Another option is to use a YAML configuration file, please see the next
paragraph).

The test annotated with `@SymphonyBdkSpringBootTest` is automatically marked under SpringBoot `integration-test`
profile, so developer can

- opt to use a `application-integration-test.yaml` file in the test class path to customise the Bot application
  configurations if needed.
- run only the integration tests separately from other tests.

Here below is a quick integration test example, for a more complete example, please take a look at
the `SampleSpringAppIntegrationTest.java`in the [_
symphony-bdk-examples/bdk-spring-boot-example_](https://github.com/finos/symphony-bdk-java/tree/main/symphony-bdk-examples/bdk-spring-boot-example/src/test/java/com/symphony/bdk/examples/spring)
module.

```java

@SymphonyBdkSpringBootTest(properties = {"bot.id=1", "bot.username=my-bot", "bot.display-name=my bot"})
public class SampleSpringAppIntegrationTest {
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

In this example, the test is expecting to have a replied message sent back the room, from where a `/echo` slash command
message has received.

**Step 1**. We first stub the injected mocked `messageService`,

**Step 2**. then inject the `/echo` command message through `SymphonyBdkTestUtils.java`,

**Step 3**. at the end we verify that a replied message has been sent back to the room through BDK `messsageService`.

### Inheritance

The `@SymphonyBdkSpringBootTest` annotation is inheritable. Developers may have one parent test class with this
annotation, so that the child test classes will inherit the annotation and its properties.

### Utils
The `SymphonyBdkTestUtils.java` is a very handy helper class allowing to inject Symphony events to the DataFeed, so that
the registered activities and slash commands should react on these received events.

This util class comes with six methods so far,

1. `void pushMessageToDF(V4User initiator, V4Stream stream, String message)`, to inject a simple string message to
   Datafeed, the util class will construct the final MessageML message content.
2. `void pushMessageToDF(V4User initiator, V4Stream stream, String message, UserV2 botInfo)`, to especially inject a
   slash command message to Datafeed, which needs started with a bot mention, so the util class will construct the final
   slash command message with the provided bot user object.
3. ` void pushMessageToDF(V4User initiator, V4Message message)`, to inject a complex message, developers can construct
   an entire complex `V4Message` object and inject it to Datafeed.
4. `void pushElementActionToDF(V4User initiator, V4SymphonyElementsAction elementAction)`, to inject a for element to
   Datafeed.
5. `void pushUserJoinedEventToDF(V4User initiator, V4UserJoinedRoom userJoinedRoom)`, to inject a `UserJoinedRoomEvent`
   to Datafeed.
6. finally a generic method `void pushEventToDataFeed(V4Event event)`, developer can inject any Datafeed events through
   this method, do not forget to precise the event type by using `V4EventType` enum class, such like

```java

void test(){
    ...
    ...
    pushEventToDataFeed(new V4Event()
        .initiator(new V4Initiator().user(initiator))
        .payload(new V4Payload().symphonyElementsAction(
            new V4SymphonyElementsAction().formId("gif-category-form")
                .formMessageId("form-message-id")
                .formValues(values)
                .stream(stream)))
        .type(V4EventType.SYMPHONYELEMENTSACTION.name()));
}
```

----
[Home :house:](./index.html)

