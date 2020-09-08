# Activity API

The Activity API is an abstraction built on top of the Datafeed's [_Real Time Events_](https://developers.symphony.com/restapi/docs/real-time-events). An Activity is basically a user interaction triggered from the chat.
Two different kinds of activities are supported by the BDK:
- **Command Activity**: triggered when a message is sent in an `IM`, `MIM` or `Chatroom`
- **Form Activity**: triggered when a user replies to an [_Elements_](https://developers.symphony.com/symphony-developer/docs/overview-of-symphony-elements) form message

## Activity Registry
The main component for activities is certainly the [`ActivityRegistry`](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/activity/ActivityRegistry.java).
This central component is used to either adding or retrieving activities. 

## Command Activity
A command activity is triggered when a message is sent in an `IM`, `MIM` or `Chatroom`. 

### How to create a Command Activity

```java
public class CommandActivityExample {

  public static void main(String[] args) {
    // TODO
  }
}
```

### Slash Command

#### How to create a Slash Command

```java
public class SlashCommandExample {

  public static void main(String[] args) {
    // TODO
  }
}
```

## Form Activity
A form activity is triggered when an end-user reply or submit to an _Elements_ form. 

### How to create a Form Activity

```java
public class FormActivityExample {

  public static void main(String[] args) {
    // TODO
  }
}
```
