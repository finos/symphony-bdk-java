# Fluent API

The Fluent API is the most basic component of the BDK. This component provides the developers very quickly an entry point 
to discover all the features of the BDK, helps them to easily understand how to make a bot interacting with the 
Symphony platform.

## SymphonyBdk

The heart of the Fluent API is the [`SymphonyBdk`](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/SymphonyBdk.java).
This component is an entry point for a developer to go through all the features of the BDK. A `SymphonyBdk` object is 
built from the information extracting from the BDK configuration file.

```java
public class Example {
    
    public static void main(String[] args) {
        //Init the BDK entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
    }
}
```


## Using BDK services from SymphonyBdk

Once the `SymphonyBdk` instance is created, the bot is automatically authenticated and all the BDK services will be available 
for developers to use.

```java
public class Example {
    
    public static void main(String[] args) {
        // Init the BDK entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
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
- Message Service: `bdk.message()`
- Datafeed Service: `bdk.datafeed()`


## OBO authenticating using SymphonyBdk

On Behalf Of (OBO) is a pattern that enables developers to perform operations on behalf of a Symphony end-user. [`Getting started with OBO](https://developers.symphony.com/restapi/docs/get-started-with-obo)
With a SymphonyBdk instance, we can easily authenticate the bot in OBO mode:

```java
public class Example {
    
    public static void main(String[] args) {
        // Init the BDK entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
        // OBO authentication
        AuthSession oboSession = bdk.obo("user.name");
        // Running service in Obo mode
        final List<V2UserDetail> userDetails = bdk.users().listUsersDetail(new UserFilter(), oboSession);
    }
}

```
