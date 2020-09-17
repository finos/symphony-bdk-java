# Datafeed

The Datafeed Service is a service used for handling the [_Real Time Events_](https://developers.symphony.com/restapi/docs/real-time-events). 
For each interaction of a user within the IM, MIM or Room chat, an event will be sent to the datafeed.
The bot can create a datafeed, list all created datafeeds or retrieve all the Real Time events within a datafeed through Datafeed API.
The Datafeed Service is a Core service built on top of the Datafeed API and provide a dedicated contract to bot developers to work with datafeed. 

## Datafeed Configuration

Datafeed Service can be configured by the datafeed field in the configuration file:

```yaml
datafeed:
  version: 'v1' # or 'v2'
  retry:
    maxAttempts: 6
    initialIntervalMillis: 2000
    multiplier: 1.5
    maxIntervalMillis: 10000
```

The minimal configuration for the datafeed service is the version of the datafeed which will be chosen to be use in the bdk.
If the bot developers want to use the datafeed version 2 in their bot, the version configuration have to be specified as `v2`.
Otherwise, the datafeed version 1 will be used by default.

Bot developers can also configure a dedicated retry mechanism which will be used only by the datafeed service.
Basically, the datafeed service retry configuration has the field same as the global retry configuration with the fields for implementing 
the exponential backoff mechanism.

## How to use
The central component for the contract between bot developers and  the Datafeed API is the `DatafeedService`.
This service is accessible from the `SymphonyBdk` object by calling the `datafeed()` method.
For instance:

```java
import com.symphony.bdk.core.SymphonyBdk;
public class Example {

  public static void main(String[] args) {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("config.yaml"));

    final DatafeedService datafeedService = bdk.datafeed();
  }
}
```

A more detailed example of the usage of the Datafeed service can be found [here](../symphony-bdk-examples/bdk-core-examples/src/main/java/com/symphony/bdk/examples/DatafeedExampleMain.java).

## Subscribe/Unsubscribe RealTimeEventListener

[RealTimeEventListener](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/service/datafeed/RealTimeEventListener.java) is an interface definition for a callback to be invoked when a real-time event is received from the datafeed.
This real-time event can be one of these following event types:

- Message Sent
- Messages Suppressed
- Symphony Elements Action
- Shared Wall Post
- IM/MIM Created
- Room Created
- Room Updated Message
- Room Deactivated Message
- Room Reactivated Message
- User Requested to Join Room
- User Joined Room
- User Left Room
- Room Member Promoted To Owner
- Room Member Demoted From Owner
- Connection Requested
- Connection Accepted

The Datafeed Service can subscribe/unsubscribe one or many `RealTimeEventListener` by calling `DatafeedService#subscribe` or
`DatafeedService#unsubscribe`. For instance:

```java
public class Example {
    
    public static void main(String[] args) {
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("config.yaml"));
        final RealTimeEventListener listener = new RealTimeEventListener() {
        @Override
            public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
              log.info("Message sent");
            }
        };
        // subscribe a listener
        bdk.datafeed().subscribe(listener);
        // unsubscribe a listener
        bdk.datafeed().unsubscribe(listener);
    }
}
```

## Read a datafeed

After subscribe a `RealTimeEventListener`, a bot can start the datafeed service by calling `DatafeedService#start()`.
This method will make the bot starting reading the `RealTimeEventListener` from the datafeed. Once the datafeed is started,
if bot developers call this method again, it will throw a `IllegalStateException` saying that the reading loop has started already.

Bot developers can also stop the Datafeed service anytime by calling the method `DatafeedService#stop()`.
After this method is called, if there is still any request from the bot to the datafeed, the bot will finish this request before really stop the Datafeed service.

For instance:
```java
public class Example {
    
    public static void main(String[] args) {
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("config.yaml"));
        final RealTimeEventListener listener = new RealTimeEventListener() {
        @Override
            public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
              log.info("Message sent");
            }
        };
        // subscribe a listener
        bdk.datafeed().subscribe(listener);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping Datafeed...");
            bdk.datafeed().stop();
        }));
    
        bdk.datafeed().start();
    }
}
```
----
[Home :house:](./index.md)
