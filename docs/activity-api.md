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
    return new ActivityInfo().type(ActivityType.COMMAND).name("Hello Command"); // (3)
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
$ /command argument
$ /command @mentionArgument
```

#### Slash command pattern format
The slash command can be a simple static pattern without arguments, like `/command` or `/command help`.
You may specify one or more arguments by enclosing them with braces like `{myArgument}`.
The string inside the braces must have at least one character and must not have whitespaces.
If there are some arguments, each argument is mandatory in order for the slash command to be triggered. For instance:
* for command pattern `/command {arg}`:
  * `/command` won't match
  * `command help` will match
  * `command help me` won't match

Arguments can be of several types:
* `{wordArgument}` will match a regular word only (won't match a mention, a cashtag or a hashtag)
* `{@mentionArgument}` will match a mention only
* `{#hastagArgument}` will match a hashtag only
* `{$cashtagArgument}` will match a cashtag only

In the slash command definition, each argument must be separated by a whitespace to be valid. For instance:
* `{arg1} {@arg2}` is valid
* `{arg1}{arg2}` is invalid

When a slash command matches, arguments can be retrieved thanks to the `getArguments()` method in the `CommandContext` class.

```java
public class Example {

  public static void main(String[] args) throws Exception {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    bdk.activities().register(SlashCommand.slash("/hello",    // (1)
                                                 true,        // (2)
                                                 context -> { // (3)

      log.info("Hello slash command triggered by user {}", context.getInitiator().getUser().getDisplayName());
    }));

    bdk.activities().register(SlashCommand.slash("/hello {@mention}", true, context -> {
      Mention mention = context.getArguments().getAsMention("mention"); // must be the same name as put in the slash command pattern
      log.info("Hello slash command triggered by user {} and mentioning {}", context.getInitiator().getUser().getDisplayName(),
              mention.getUserDisplayName());
    }));

    // finally, start the datafeed loop
    bdk.datafeed().start();
  }
}
```
1. `/hello` is the command pattern
2. `true` means that the bot has to be mentioned
3. the command callback provides the `CommandContext` that allows to retrieve some information about the source of the 
event, or the event initiator (i.e. user that triggered the command)

### Help Command

_Help_ command is a BDK built-in command which will list out all the commands registered in the `ActivityRegistry` of the BDK by:
```
$ @BotMention /help
``` 
The help command can be instantiated by passing an `ActivityRegistry` and `MessageService` instances to the constructor,
 then added manually to the BDK activity registry:
```java
public class Example {

  public static void main(String[] args) throws Exception {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    bdk.activities().register(SlashCommand.slash("/hello",    
                                                 true,        
                                                 context -> { 

      log.info("Hello slash command triggered by user {}", context.getInitiator().getUser().getDisplayName());
    }));
    
    bdk.activities().register(new HelpCommand(bdk.activities(), bdk.messages()));

    // finally, start the datafeed loop
    bdk.datafeed().start();
  }
}
```

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
    return new ActivityInfo().type(ActivityType.FORM).name("Hello Form Reply Activity");
  }
}
```
1. The `ActivityMatcher` ensures that activity logic is triggered only when the form with `id` "**hello-form**" has been 
submitted from the action button "**submit**"
2. The activity context allows to directly retrieve form values. Here the "**name**" `<text-field>` value

----
[Home :house:](./index.md)
