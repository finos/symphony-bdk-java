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
It exposes all the services mentioned above and is accessible from the `SymphonyBdk` object by calling the `messages()` method.
For instance:

```java
import com.symphony.bdk.core.MessageService;

public class Example {
  public static final String STREAM_ID = "gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA";

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
    // Get the MessageService object
    final MessageService messageService = bdk.message();

    //send a regular message
    final V4Message regularMessage = messageService.send(STREAM_ID, "<messageML>Hello, World!</messageML>");
    System.out.println("Message sent, id: " + regularMessage.getMessageId());
  }
}
```

A more detailed example with all exposed services can be found [here](../symphony-bdk-examples/bdk-core-examples/src/main/java/com/symphony/bdk/examples/MessageExampleMain.java).

## Send messages with templates
The Message service also allows you to send messages using templates. So far, we only support [FreeMarker templates](https://freemarker.apache.org/),
but we may add support for other template engines.
For instance, if you have the following template file:
```
<messageML>${message}</messageML>
```
you will be able to use it when sending message:
```java
final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
final V4Message regularMessage = bdk().message().send(streamId, "path/to/template.ftl", Collections.singletonMap("message", "Hello!"));
```
The above will send the message `<messageML>Hello!</messageML>` as expected.
Please check the [FreeMarker documentation](https://freemarker.apache.org/docs/pgui_quickstart_createdatamodel.html)
to know more about the data model you can use in templates and about the corresponding object structure you need to pass as parameter.

The template name passed as parameter can be the name of a built-in template
(only ["simpleMML"](../symphony-bdk-template/symphony-bdk-template-freemarker/src/main/resources/com/symphony/bdk/template/freemarker/simpleMML.ftl)
available so far), the path to a template file or a URL to a template file.
The templates will be looked for in this order:
* in built-in templates
* in the classpath
* in the file system
* lastly as a URL
