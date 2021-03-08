# Datafeed

The datafeed service is a service used for handling the [_Real Time Events_](https://developers.symphony.com/restapi/docs/real-time-events). 
When a user makes an interaction within the IM, MIM or Room chat like sending a message, joining or leaving a room chat..., 
when a connection request is sent, when a wall post is published or when a user replies an Symphony element, an event will be sent to the datafeed.
The bot can create a datafeed, list all created datafeeds or retrieve all the Real Time Events within a datafeed through datafeed API.
The datafeed service is a core service built on top of the Datafeed API and provide a dedicated contract to bot developers to work with datafeed. 

For more advanced interactions between users and bots, you can also read [Activity API](../docs/activity-api.md).

## How to use
The central component for the contract between bot developers and  the Datafeed API is the `DatafeedService`.
This service is accessible from the `SymphonyBdk` object by calling the `datafeed()` method.
For instance:

```java
public class Example {

    public static void main(String[] args) { 
        // create bdk entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
        
        // create listener to be subscribed
        final RealTimeEventListener listener = new RealTimeEventListener() {
            @Override
            public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
                log.info("Message sent");
            }
        };

        // subscribe a listener
        bdk.datafeed().subscribe(listener);
       
        // start reading the datafeed 
        bdk.datafeed().start(); 
    }
}
```

A more detailed example of the usage of the Datafeed service can be found [here](../symphony-bdk-examples/bdk-core-examples/src/main/java/com/symphony/bdk/examples/DatafeedExampleMain.java).

## Datafeed Configuration

Datafeed Service can be configured by the datafeed field in the configuration file:

```yaml
datafeed:
  version: 'v1' # specify datafeed version 'v1' or 'v2'
  retry:
    maxAttempts: 6 # maximum number of retry attempts
    initialIntervalMillis: 2000 # initial interval between two attempts
    multiplier: 1.5 # interval multiplier after each attempt
    maxIntervalMillis: 10000 # limit of the interval between two attempts
```

The minimal configuration for the datafeed service is the version of the datafeed which will be chosen to be use in the BDK.
For the moment, not all the customers have the datafeed version 2 available on their systems, that's why bot developers are able to
choose the datafeed version that they wish to use on their bot. If the bot developers want to use the datafeed version 2 in their bot, 
the version configuration have to be specified as `v2`.
Otherwise, the datafeed version 1 will be used by default.

Bot developers can also configure a dedicated retry mechanism which will be used only by the datafeed service.
Basically, the datafeed service retry configuration has the field same as the global retry configuration with the fields for implementing 
the exponential backoff mechanism.

### Infinite retries
By default, Datafeed retry is configured to have an infinite number of attempts. This is equivalent to: 
```yaml
datafeed:
  retry:
    maxAttempts: -1 # infinite number of attemps
    initialIntervalMillis: 2000
    multiplier: 1.5
    maxIntervalMillis: 10000
```

## Subscribe/Unsubscribe RealTimeEventListener

[RealTimeEventListener](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/service/datafeed/RealTimeEventListener.html) is an interface definition for a callback to be invoked when a real-time event is received from the datafeed.
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

The datafeed Service can subscribe/unsubscribe one or many `RealTimeEventListener` by calling `DatafeedService#subscribe` or
`DatafeedService#unsubscribe`. For instance:

```
// subscribe a listener
bdk.datafeed().subscribe(listener);

// unsubscribe a listener
bdk.datafeed().unsubscribe(listener);
```

## Read a datafeed

After subscribe a `RealTimeEventListener`, a bot can start the datafeed service by calling `DatafeedService#start()`.
This method will allow the bot to start reading the `RealTimeEventListener` from the datafeed. Once the datafeed is started,
if bot developers call this method again, it will throw a `IllegalStateException` saying that the reading loop has started already.

Bot developers can also stop the datafeed service anytime by calling the method `DatafeedService#stop()`.
After this method is called, if there is still any request from the bot to the datafeed, the bot will finish this request before really stop the datafeed service.

For instance:
```
// start the datafeed service
bdk.datafeed().start();

//stop the datafeed service
bdk.datafeed.stop();
```
----
[Home :house:](./index.md)
