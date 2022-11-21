# Datafeed
> :warning: The datafeed 1 service will be fully replaced by the datafeed 2 service in the future.
> Please consider using datafeed 2.
>
> For more information on the timeline as well as on the benefits of datafeed 2, please reach out to your Technical
> Account Manager or to our [developer documentation](https://docs.developers.symphony.com/building-bots-on-symphony/datafeed).

The datafeed loop is a service used for handling the [_Real Time
Events_](https://docs.developers.symphony.com/building-bots-on-symphony/datafeed/real-time-events). When a user makes an interaction within the IM,
MIM or Room chat like sending a message, joining or leaving a room chat..., when a connection request is sent, when a
wall post is published or when a user replies an Symphony element, an event will be sent to the datafeed. The bot can
create a datafeed, list all created datafeeds or retrieve all the Real Time Events within a datafeed through datafeed
API. The datafeed loop is a core service built on top of the Datafeed API and provide a dedicated contract to bot
developers to work with datafeed.

For more advanced interactions between users and bots, you can also read [Activity API](./activity-api.md).

## How to use

The central component for the contract between bot developers and the Datafeed API is the `DatafeedLoop`. This service
is accessible from the `SymphonyBdk` object by calling the `datafeed()` method. For instance:

```java
@Slf4j
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

A more detailed example of the usage of the Datafeed service can be
found [here](../symphony-bdk-examples/bdk-core-examples/src/main/java/com/symphony/bdk/examples/DatafeedExampleMain.java).

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

The minimal configuration for the datafeed service is the version of the datafeed which will be chosen to be use in the
BDK. For the moment, not all the customers have the datafeed version 2 available on their systems, that's why bot
developers are able to choose the datafeed version that they wish to use on their bot. If the bot developers want to use
the datafeed version 2 in their bot, the version configuration have to be specified as `v2`. Otherwise, the datafeed
version 1 will be used by default.

Bot developers can also configure a dedicated retry mechanism which will be used only by the datafeed service.
Basically, the datafeed service retry configuration has the field same as the global retry configuration with the fields
for implementing the exponential backoff mechanism.

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

[RealTimeEventListener](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/service/datafeed/RealTimeEventListener.html)
is an interface definition for a callback to be invoked when a real-time event is received from the datafeed. This
real-time event can be one of these following event types:

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

The datafeed Service can subscribe/unsubscribe one or many `RealTimeEventListener` by
calling `DatafeedLoop#subscribe` or `DatafeedLoop#unsubscribe`. For instance:

```
// subscribe a listener
bdk.datafeed().subscribe(listener);

// unsubscribe a listener
bdk.datafeed().unsubscribe(listener);
```

## Read a datafeed

After subscribe a `RealTimeEventListener`, a bot can start the datafeed service by calling `DatafeedService#start()`.
This method will allow the bot to start reading the `RealTimeEventListener` from the datafeed. Once the datafeed is
started, if bot developers call this method again, it will throw a `IllegalStateException` saying that the reading loop
has started already.

Bot developers can also stop the datafeed service anytime by calling the method `DatafeedService#stop()`. After this
method is called, if there is still any request from the bot to the datafeed, the bot will finish this request before
really stop the datafeed service.

For instance:

```
// start the datafeed service
bdk.datafeed().start();

//stop the datafeed service
bdk.datafeed.stop();
```

# Datahose
> :warning: Please note that Datahose is available as beta and will remain as beta until further notice.

Datahose is very similar to datafeed: it enables a bot to receive [_Real Time
Events_](https://docs.developers.symphony.com/building-bots-on-symphony/datafeed/real-time-events) with the main
difference that *all* events of the pod are received. The datahose loop is a core service built on top of the events API
and provide a dedicated contract to bot developers to work with datahose. This is compatible with agent version 22.5 onwards.

The [Activity API](./activity-api.md) is not meant to be used with datahose.

## How to use
The central component for the contract between bot developers and the Datafeed API is the `DatahoseLoop`. This service
is accessible from the `SymphonyBdk` object by calling the `datahose()` method. For instance:

```java
@Slf4j
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
        bdk.datahose().subscribe(listener);

        // start reading the datahose
        bdk.datahose().start();
    }
}
```

An example of the usage of the Datahose service can be
found [here](../symphony-bdk-examples/bdk-core-examples/src/main/java/com/symphony/bdk/examples/DatahoseExampleMain.java).

## Datahose Configuration

Datahose Service can be configured by the datafeed field in the configuration file:

```yaml
datahose:
    tag: fancyTag # optional tag that will be used when creating or reusing a datahose feed
    eventTypes: # mandatory field, events you want to receive
        - INSTANTMESSAGECREATED
        - ROOMCREATED
    retry: # optional
        maxAttempts: 6 # maximum number of retry attempts
        initialIntervalMillis: 2000 # initial interval between two attempts
        multiplier: 1.5 # interval multiplier after each attempt
        maxIntervalMillis: 10000 # limit of the interval between two attempts
```

The minimal configuration for the datahose service is the `eventTypes` field. It should contain at least one value
chosen among [_Real Time Events_](https://docs.developers.symphony.com/building-bots-on-symphony/datafeed/real-time-events)
list and that `MESSAGESENT`, `MESSAGESUPPRESSED` and `SYMPHONYELEMENTSACTION` values can be set only if the ceservice is
properly configured and running in your Symphony agent.

The `tag` field is optional and is used when creating and reusing datahose feeds. If you have several instances of the
same bot and want them to use the same datahose feed (so that events are spread over bot instances),
all instances should have the same tag value (or no tag field).

Bot developers can also configure a dedicated retry mechanism which will be used only by the datahose service.
Basically, the datahose service retry configuration has the field same as the global retry configuration with the fields
for implementing the exponential backoff mechanism.

### Infinite retries

By default, like datafeed, datahose retry is configured to have an infinite number of attempts. This is equivalent to:

```yaml
datafeed:
    retry:
        maxAttempts: -1 # infinite number of attemps
        initialIntervalMillis: 2000
        multiplier: 1.5
        maxIntervalMillis: 10000
```

## Subscribe/Unsubscribe RealTimeEventListener

The datahose loop uses the [RealTimeEventListener](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core/latest/com/symphony/bdk/core/service/datafeed/RealTimeEventListener.html)
like in the datafeed loop. Due to technical limitations, datahose loop only receives a subset of all real time events:

- Message Sent
- Symphony Elements Action
- IM/MIM Created
- Room Created
- Room Updated Message
- Room Deactivated Message
- Room Reactivated Message

The datahose Service can subscribe/unsubscribe one or many `RealTimeEventListener` by
calling `DatahoseLoop#subscribe` or `DatahoseLoop#unsubscribe`. For instance:

```
// subscribe a listener
bdk.datahose().subscribe(listener);

// unsubscribe a listener
bdk.datahose().unsubscribe(listener);
```

# Best practices

## Event handling

It is recommended for bot's developer to make their listeners idempotent if possible or to deal with duplicated events.
When running multiple instances of a bot, this could happen if the event is slowly processed in one instance and gets
re-queued and dispatched to another instance. It can also happen that the user types a bot's command twice by mistake.

Symphony also does not provide any ordering guarantees. While most of the time the events will be received in order, if
a user is very fast you might receive the second message he typed. In a busy room, events could be flowing in and out in
a non-chronological order.

Received events should be processed quickly (in less than 30 seconds) to avoid them being re-queued in datafeed. If the
business logic of the listener leads to long operations then it is recommended handle them in a separate thread to avoid
blocking the datafeed loop. To help you detect this situation, warning logs will be printed if the event processing time
exceeds 30 seconds.

Before shutting down a bot's instance, you want to make sure that the datafeed loop is properly stopped and that the bot
has stopped processing events. The Spring Boot starter that starts the datafeed loop automatically also registers a
bean _destroy method_ to support that. If you are not using the starter, a shutdown hook can be used:

```
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    bdk.datafeed().stop();
    bdk.datahose().stop();
}));
```

Stopping the datafeed and/or datahose loops might take a while (if the loop is currently waiting for new events, up to 30 seconds).

## Error handling

The datafeed/datahose loop once started will keep running until the bot is stopped. So it will catch all the exceptions
raised by listeners or activities to prevent the loop from stopping. However, if the processing of an event failed, and
if nothing specific is done by the listener to store it in a database or a queue, an event could be lost and never
processed by the bot.

The BDK provides a way to re-queue events if needed through the `EventException` that can be raised from listeners. In
that case the datafeed/datahose loop current execution for the currently received events will stop (other listeners will
not be called), and the events will be re-queued in datafeed. The datafeed loop will resume its execution and will after
some delays receive non-processed events (30s by default).

This feature is not available for datafeed v1. When the datafeed/datahose loop executes it can receive several events at
once and will dispatch them to all the subscribed listeners. Therefore, you should be careful about no processing an
event twice. This can be achieved by maintaining a short time lived cache of the already processed events.

## Running multiple instances of a bot (DF v2 and datahose only)

An example using datafeed v2 is provided in
[bdk-multi-instances-example](../symphony-bdk-examples/bdk-multi-instances-example) module.

With datafeed v2, it is possible to run multiple instances of a bot. Each instance will receive events in turn. The
examples also makes use of Hazelcast to keep a distributed cache of already processed events and avoid replying to a
message twice.

The logic to avoid handling an event twice is tied to the bot and its logic so the BDK makes no assumption about it and
lets you manage it freely.

The same applies to datahose. To enable this behavior, make sure you have the same `datahose.tag` value
(or no `tag` field) in the configuration of all your bot instances.

----
[Home :house:](./index.md)
