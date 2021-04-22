# How to run multiple bot instances?

## Multiple instances reading the same datafeed v2

If multiple instances read the same datafeed (v2) one only will receive an event. If the instance fails to process the
event, it will be re-queued and dispatched to another instance.

The [InjectorBot](./src/main/java/com/symphony/bdk/examples/df2/InjectorBot.java)
and [ReaderBot](./src/main/java/com/symphony/bdk/examples/df2/ReaderBot.java) demo this behavior.

They also make use of Hazelcast to provide a distributed cached to ensure that in the case of an event being slowly
processed it does not get processed by another instance.
