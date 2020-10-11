# Message API

The Message API aims to cover the Messages part of the [REST API documentation](https://developers.symphony.com/restapi/reference#messages-v4).
More precisely:
* [Get a message](https://developers.symphony.com/restapi/reference#get-message-v1)
* [Get messages](https://developers.symphony.com/restapi/reference#messages-v4)
* [Get message IDs by timestamp](https://developers.symphony.com/restapi/reference#get-message-ids-by-timestamp)
* [Send message](https://developers.symphony.com/restapi/reference#create-message-v4)
* [Import messages](https://developers.symphony.com/restapi/reference#import-message-v4)
* [Get attachment](https://developers.symphony.com/restapi/reference#attachment)
* [List attachments](https://developers.symphony.com/restapi/reference#list-attachments)
* [Get allowed attachment types](https://developers.symphony.com/restapi/reference#attachment-types)
* [Suppress message](https://developers.symphony.com/restapi/reference#suppress-message)
* [Get message status](https://developers.symphony.com/restapi/reference#message-status)
* [Get message receipts](https://developers.symphony.com/restapi/reference#list-message-receipts)
* [Get message relationships](https://developers.symphony.com/restapi/reference#message-metadata-relationship)

## How to use
The central component for the Message API is the `MessageService`.
It exposes all the services mentioned above and is accessible from the `SymphonyBdk` object by calling the `messages()` method:
```java
public class Example {
  public static final String STREAM_ID = "gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA";

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
    // send a regular message
    final V4Message regularMessage = bdk.message().send(STREAM_ID, Message.builder().content("<messageML>Hello, World!</messageML>").build());
    log.info("Message sent, id: " + regularMessage.getMessageId());
  }
}
```

## Using templates
The `MessageService` also allows you to send messages using templates. So far, the BDK supports two different template
engine implementations: 
- [FreeMarker](https://freemarker.apache.org/) (through dependency `com.symphony.platformsolutions:symphony-bdk-template-freemarker`)
- [Handlebars](https://github.com/jknack/handlebars.java) (through dependency `com.symphony.platformsolutions:symphony-bdk-template-handlebars`)

### How to send a message from a template
> In the code examples below, we will assume that FreeMarker as been selected as template engine implementation.
> See [how to select the template engine implementation](#select-your-template-engine-implementation).

First you need to define your message template file. Here `src/main/resources/templates/simple.ftl`:
```
<messageML>Hello, ${name}!</messageML>
```
you will be able to use it when sending message:
```java
public class Example {
  public static final String STREAM_ID = "gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA";

  public static void main(String[] args) {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    final V4Message regularMessage = bdk.message().send(streamId, "/templates/simple.ftl", Collections.singletonMap("name", "User"));
  }
}
```
The above will send the message `<messageML>Hello, User!</messageML>` as expected.

> Please note that the `MessageService` will try fetch template from different locations ordered by:
> 1. classpath
> 2. file system

It is also possible to get direct access to the `TemplateEngine` through the `MessageService`: 
```java
public class Example {

  public static void main(String[] args) {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // load template from classpath location
    final Template template = bdk.messages().templates().newTemplateFromClasspath("/complex-message.ftl");

    // process template with some vars and retrieve content
    // any POJO can also be processed by the template
    final String content = template.process(Collections.singletonMap("name", "Freemarker"));

    // display processed template content
    log.info(content);
  }
}
```

#### Select your template engine implementation
Developers are free to select the underlying template engine implementation. This can be done importing the right 
dependency in your classpath. 

With [Maven](./getting-started.md#maven-based-project): 
```xml
<dependencies>
        <dependency>
            <groupId>com.symphony.platformsolutions</groupId>
            <artifactId>symphony-bdk-template-freemarker</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- or -->
        <dependency>
            <groupId>com.symphony.platformsolutions</groupId>
            <artifactId>symphony-bdk-template-handlebars</artifactId>
            <scope>runtime</scope>
        </dependency>
</dependencies>
```
With [Gradle](./getting-started.md#gradle-based-project): 
```groovy
dependencies {
    runtimeOnly 'com.symphony.platformsolutions:symphony-bdk-template-freemarker'
    // or
    runtimeOnly 'com.symphony.platformsolutions:symphony-bdk-template-handlebars'
}
```
> :warning: If multiple implementations found in classpath, an exception is throw in order to help you to define which one
> your project really needs to use.

----
[Home :house:](./index.md)
