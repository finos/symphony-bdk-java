# Fluent API

The Fluent API is the most basic feature of the BDK. This component provides the developers very quickly an entry point 
to discover all others features of the BDK, helps them to easily understand how to make a bot interacting with the 
Symphony platform.

## SymphonyBdk

The heart of the Fluent API is the [`SymphonyBdk`](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/SymphonyBdk.java).
This component is an entry point for a developer to go through all the features of the BDK. A `SymphonyBdk` object is 
built from the information extracted from the BDK configuration file.

```java
public class Example {
    
    public static void main(String[] args) {
        // Initialize the BDK entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("config.yaml"));
    }
}
```


## Using BDK services from SymphonyBdk

Once the `SymphonyBdk` instance is created, the bot is automatically authenticated and all the BDK services will be available 
for developers to use.

```java
public class Example {
    
    public static void main(String[] args) {
        // Initialize the BDK entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("config.yaml"));
        // Using users service
        final List<V2UserDetail> userDetails = bdk.users().listUsersDetail(new UserFilter());
        // Using datafeed service
        bdk.datafeed().start();
    }
}
```

Developers can use the services provided by the BDK by calling the method with the name of the service. For the moment, the services
that is available in BDK is:

- Users Service: `bdk.users()`
- Streams Service: `bdk.streams()`
- Message Service: `bdk.messages()`
- Datafeed Service: `bdk.datafeed()`
- Activities Registry: `bdk.activities()`

----
[Home :house:](./index.md)
