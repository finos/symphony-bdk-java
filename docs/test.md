# Symphony BDK Test

This guide provides information to Symphony BDK Bot developers about how using Symphony BDK Test to build the
integration tests for the Bots.

## Java BDK Bots Integration tests

To create an integration test for a Java BDK Bot application, developer needs to add annotation `@SymphonyBdkTest` on top
of the test class. This annotation has three properties `botId`, `botName`, and `botDisplayName`, developer can choose 
appropriate values per requirement, otherwise the default values are used.

```java
long botId() default 1L;

String botName() default "bdk-bot";

String botDisplayName() default "BDK Bot";
```

The annotation automatically brings the `Mockito` as well as `SymphonyBdkExtension` Junit extensions in the test, which will
initialise a `SymphonyBdk` mock instance, through `SymphonyBdk`, developer can get all BDK services, please remember all
services are simply  **Mockito** mocked objects. Take the simple example below,

```java

@SymphonyBdkTest
public class SampleBdkIntegrationTest {
    private final V4User initiator = new V4User().displayName("user").userId(2L);
    private final V4Stream stream = new V4Stream().streamId("my-room");

    private SymphonyBdk bdk;

    @Test
    @DisplayName("Reply upon received gif category form reply, inject bdk as property")
    void gif_form_replyWithMessage() {

        bdk.activities().register(new GifFormActivity(bdk.messages()));
        // given
        when(bdk.messages().send(anyString(), anyString())).thenReturn(mock(V4Message.class));

        // when
        Map<String, Object> values = new HashMap<>();
        values.put("action", "submit");
        values.put("category", "bdk");
        pushEventToDataFeed(new V4Event().id("id").timestamp(Instant.now().toEpochMilli())
                                         .initiator(new V4Initiator().user(initiator))
                                         .payload(new V4Payload().symphonyElementsAction(
                                                 new V4SymphonyElementsAction().formId("gif-category-form")
                                                                               .formMessageId("form-message-id")
                                                                               .formValues(values)
                                                                               .stream(stream)))
                                         .type(SymphonyBdkTestUtils.V4EventType.SYMPHONYELEMENTSACTION.name()));

        // then
        verify(bdk.messages()).send(eq("my-room"), contains("Gif category is \"bdk\""));
    }
}
```

in this example, the goal is to validate the `GifFormActivity`, it is expecting to have a message sent back to the room,
when a `gif-category-form` reply has received.

**Step 1**. As shown, a `SymphonyBdk` mock instance is inject as test class property.

**Step 2**. Register the being tested form activity through BDK activity service.

**Step 3**. Stub the injected mocked bdk messages service,

**Step 4**. Inject the form reply event through `SymphonyBdkTestUtils.java`,

**Step 5**. at the end we verify that a replied message has been sent back to the room through bdk messages service.


The `SymphonyBdk` instance can also be injected as test method argument, like shown in the example below.

```java

@SymphonyBdkTest
public class SampleBdkIntegrationTest {
    private final V4User initiator = new V4User().displayName("user").userId(2L);
    private final V4Stream stream = new V4Stream().streamId("my-room");

    @Test
    @DisplayName("Reply echo slash command, inject bdk as parameter")
    void testEchoSlashCommand(SymphonyBdk bdk) {
        final SlashCommand slashCommand = SlashCommand.slash("/echo {argument}", 
                false, 
                context -> bdk.messages()
                          .send(context.getStreamId(),
                                  String.format(
                                          "Received argument: %s",
                                          context.getArguments()
                                                 .get("argument"))),
                "echo slash command");
        bdk.activities().register(slashCommand);

        // given
        when(bdk.messages().send(anyString(), any(Message.class))).thenReturn(mock(V4Message.class));

        // when
        pushMessageToDF(initiator, stream, "/echo arg");

        // then
        verify(bdk.messages()).send(eq("my-room"), contains("Received argument: arg"));
    }
```

## SpringBoot based Bots Integration tests

To create an integration test for an SpringBoot based Bot application, developer needs to add the
annotation `@SymphonyBdkSpringBootTest` on top of the test class, when the test is launched, a SpringBoot application
context is going to be loaded, all BDK services are being injected in this application context.

Developer can then `@Autowired` these BDK services beans whenever they are needed in the test, please note that these
services beans are simply **Mockito** mocked objects, they have to be stubbed just like the way how we do in a simple
JUnit test.

The annotation `@SymphonyBdkSpringBootTest` comes with a properties array attribute, which allows to define the Bot
information, such as `bot.id`, `bot.username`, and `bot.display-name`, the additional SpringBoot Bot application
properties can also be provided in this array (Another option is to use a YAML configuration file, please see the next
paragraph). By default, this array value is `"bot.id=1", "bot.username=bdk-bot", "bot.display-name=BDK Bot"`.

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
    void echo_command_replyWithMessage(
            @Autowired
            MessageService messageService,
            @Autowired
            UserV2 botInfo) {
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

## Symphony Bdk Test Utils

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
[Home :house:](./index.md)
 
