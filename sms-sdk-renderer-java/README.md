# sms-sdk-renderer-java

Java SDK renders symphony messages using precompiled Handlebars templates.

### Install using maven
```xml
<dependency>
    <groupId>com.symphony.platformsolutions</groupId>
    <artifactId>sms-sdk-renderer-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

## User Guide

Now, there are several message templates that you can choose:

| Name         | Description                                         |
| ------------ | --------------------------------------------------- |
| SIMPLE       | Renders a message in simple format                  |
| ALERT        | Renders a message formatted as an alert             |
| INFORMATION  | Renders a general information messages              |
| NOTIFICATION | Renders a message formatted as a notification       |
| TABLE        | Renders a collection of objects in the table format |
| LIST         | Renders a list of values                            |

### How to use

* Import the sdk:
```
import services.SmsRenderer;
```
* Create a message object as a String, for the ALERT template:
```
String messageContext = "{\"title\":\"Message Title\",\"content\":\"Message content\"}";
```
Or, as a JSONObject:
```
JSONObject messageContext = new JSONObject();

messageContext.put("title", "Message Title");
messageContext.put("content", "Message content");
messageContext.put("description", "Message description");
```

#### In the bot

* In the code, compile your message using the command:
```
String compiledTemplate = SmsRenderer.renderInBot(messageContext, SmsRenderer.SmsTypes.ALERT);
```
* Send the message with Symphony API SDK

### SDK API

Template type names are accessible by `SmsRenderer.smsTypes` enum, like so:
```
SmsRenderer.smsTypes.SIMPLE;
```
Possible values are `SIMPLE, ALERT, INFORMATION, NOTIFICATION, TABLE, LIST`.

To get the compiled template in `MessageML` format, use the functions:

| Syntax                    | Parameters               | Where to use          |
| ------------------------- | ------------------------ | --------------------- |
| SmsRenderer.renderInBot() | messageData, messageType | Bot                   |

The complete list of message data object properties can be seen in the there:

### Message Context Examples

SIMPLE
```
{
  title: 'Simple Title',
  content: 'This is a simple message'
};
```
ALERT
```
{
  title: 'Alert Title',
  content: 'This is a danger alert'
};
```
INFORMATION
```
{
  title: 'Informaiton Title',
  content: 'This is a information message',
  description: 'Information message description'
};
```
NOTIFICATION
```
{
  title: 'My Title',
  
  // OPTIONAL - used to render alert syle notification
  alert: true,
  
  // Content can be a smiple text
  content: 'My content',
  // or an object that is rendered in </card>
  content: {
    header: 'Content header',
    body: 'Content body'
  },
  
  description: 'My description',
  comment: {
    body: 'My comments'
  },
  assignee: {
    displayName: 'John Smith'
  },
  showStatusBar: true,
  type: {
    name: 'message type'
  },
  priority: {
    name: 'message priority'
  },
  status: {
    name: 'message status'
  },
  labels: [
    {
      text: 'label1'
    },
    {
      text: 'label2'
    }
  ]
};
```
LIST
```
{
  title: 'Phones List',
  
  // Content can be a list of strings
  content: [
    "iPhone", "Samsung Galaxy", "Google Pixel 3"
  ]
  // or a list of objects, header is rendered in bold
  content: [
    {
        header: "iPhone",
        body: " is an Apple device"
    }
  ]
};
```
TABLE
```
[
  { "Manufacturer": "Apple", "Phone": "iPhone", "Operating System": "iOS" },
  { "Manufacturer": "Samsung", "Phone": "Galaxy", "Operating System": "Android" },
  { "Manufacturer": "Google", "Phone": "Google Pixel 3", "Operating System": "Android" }
];
```
