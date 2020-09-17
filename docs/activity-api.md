# Activity API

The Activity API is an abstraction built on top of the Datafeed's [_Real Time Events_](https://developers.symphony.com/restapi/docs/real-time-events). An Activity is basically a user interaction triggered from the chat.
Two different kinds of activities are supported by the BDK:
- **Command Activity**: triggered when a message is sent in an `IM`, `MIM` or `Chatroom`
- **Form Activity**: triggered when a user replies to an [_Elements_](https://developers.symphony.com/symphony-developer/docs/overview-of-symphony-elements) form message

## Activity Registry
The central component for activities is the [`ActivityRegistry`](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/activity/ActivityRegistry.java).
This component is used to either add or retrieve activities. It is accessible from the `SymphonyBdk` object:

```java
public class Example {

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
    // Access to the registry for activities
    final ActivityRegistry registry = bdk.activities();
  }
}
```

## Command Activity
A command activity is triggered when a message is sent in an `IM`, `MIM` or `Chatroom`. This is the most basic interaction 
between an end-user and the bot. Here are some command activity examples: 

- the bot is mentioned followed by a [_slash_](#slash-command) command:
```
$ @BotMention /buy
```
- a command with parameters, the bot is not mentioned:
```
$ /buy 1000 goog
```
- any message that contains 'hello' can be a command:
```
$ I want to say hello to the world
```

### How to create a Command Activity

```java
public class Example {

  public static void main(String[] args) throws Exception {
    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
    // register Hello Command within the registry
    bdk.activities().register(new HelloCommandActivity());
    // finally, start the datafeed loop
    bdk.datafeed().start();
  }
}

class HelloCommandActivity extends CommandActivity<CommandContext> {

  @Override
  protected ActivityMatcher<CommandContext> matcher() {
    return c -> c.getTextContent().contains("hello"); // (1)
  }

  @Override
  protected void onActivity(CommandContext context) {
    log.info("Hello command triggered by user {}", context.getInitiator().getUser().getDisplayName()); // (2)
  }

  @Override
  protected ActivityInfo info() {
    final ActivityInfo info = ActivityInfo.of(ActivityType.COMMAND); // (3)
    info.setName("Hello Command");
    return info;
  }
}
```
1. the [`ActivityMatcher`](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/activity/ActivityMatcher.java)
allows detecting if the activity logic has to be executed or not. In this case, it will execute `onActivity(CommandContext)`
each time a message that contains "hello" is sent in a stream where the bot is also a member
2. this is where the command logic has to be implemented
3. define activity information

### Slash Command
A _Slash_ command can be used to directly define a very simple bot command such as: 
```
$ @BotMention /command
$ /command
```

> :information_source: a Slash cannot have parameters

```java
public class Example {

  public static void main(String[] args) throws Exception {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    bdk.activities().register(new SlashCommand("/hello",    // (1)
                                               true,        // (2)
                                               context -> { // (3)

      log.info("Hello slash command triggered by user {}", context.getInitiator().getUser().getDisplayName());
    }));

    // finally, start the datafeed loop
    bdk.datafeed().start();
  }
}
```
1. `/hello` is the command name 
2. `true` means that the bot has to be mentioned
3. the command callback provides the `CommandContext` that allows to retrieve some information about the source of the 
event, or the event initiator (i.e. user that triggered the command)

## Form Activity
A form activity is triggered when an end-user reply or submit to an _Elements_ form. 

### How to create a Form Activity
For this example, we will assume that the following Elements form has been posted into a room: 
```xml
<messageML>
    <h2>Hello Form</h2>
    <form id="hello-form"> <!-- (1) -->

        <text-field name="name" placeholder="Enter a name here..."/> <!-- (2) -->

        <button name="submit" type="action">Submit</button> <!-- (3) -->
        <button type="reset">Reset Data</button>

    </form>
</messageML>
```
1. the formId is "**hello-form**"
2. the form has one unique `<text-field>` called "**name**"
3. the has one action button called "**submit**"

```java
public class Example {

  public static void main(String[] args) throws Exception {
    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
    // register Hello FormReply Activity within the registry
    bdk.activities().register(new HelloFormReplyActivity(bdk.messages()));
    // finally, start the datafeed loop
    bdk.datafeed().start();
  }
}

class HelloFormReplyActivity extends FormReplyActivity<FormReplyContext> {

  private final MessageService messageService;

  public HelloFormReplyActivity(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  protected ActivityMatcher<FormReplyContext> matcher() {
    return c -> "hello-form".equals(c.getFormId()) && "submit".equals(c.getFormValue("action")); // (1)
  }

  @Override
  protected void onActivity(FormReplyContext context) {
    final String message = "Hello, " + context.getFormValue("name") + "!"; // (2)
    this.messageService.send(context.getSourceEvent().getStream(), "<messageML>" + message + "</messageML>");
  }

  @Override
  protected ActivityInfo info() {
    final ActivityInfo info = ActivityInfo.of(ActivityType.FORM);
    info.setName("Hello Form Reply Activity");
    return info;
  }
}
```
1. The `ActivityMatcher` ensures that activity logic is triggered only when the form with `id` "**hello-form**" has been 
submitted from the action button "**submit**"
2. The activity context allows to directly retrieve form values. Here the "**name**" `<text-field>` value

----
[Home :house:](./index.md)
