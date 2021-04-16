# How to run multiple bot instances?

## Multiple instances reading the same datafeed v2

If multiple instances read the same datafeed (v2) one only will receive an event. If the instance fails to process the
event, it will be re-queued and dispatched to another instance.

The [InjectorBot](./src/main/java/com/symphony/bdk/examples/df2/InjectorBot.java)
and [ReaderBot](./src/main/java/com/symphony/bdk/examples/df2/ReaderBot.java) demo this behavior.

They also make use of Hazelcast to provide a distributed cached to ensure that in the case of an event being slowly
processed it does not get processed by another instance.

## Kafka
This example aims to demonstrate how to make your bot scalable and highly available. For that,
[docker-compose.yml](./docker-compose.yml) creates 1 bot producer and 2 bot consumers. 

The producer is the bot that handles the Datafeed Real Time Events and pushes then into a Kafka topic.
See [RealTimeEventsKafkaProducer.java](./src/main/java/com/symphony/bdk/examples/kafka/producer/RealTimeEventsKafkaProducer.java).

The consumers are only listening to the same Kafka topic, and reply in the chat with there assigned consumer id.
See [RealTimeEventsKafkaConsumer.java](./src/main/java/com/symphony/bdk/examples/kafka/consumer/RealTimeEventsKafkaConsumer.java).

### Prerequisites
- [Docker](https://www.docker.com/)  
- [Gradle](https://gradle.org/)

### How to run
In order to run this example, execute the following commands: 
```shell
export BOT_HOST=develop2.symphony.com 
export BOT_USERNAME=changeit
export BOT_PK_PATH=/path/to/bot/privatekey.pem 
gradle build && docker-compose up --build
```
Once your bot instances successfully started, you should observe:
```
5:40:35pm User:
> hello 
5:40:37pm Bot:
> Consumer 'bot-consumer-01' received : hello
5:40:38pm User:
> hello again
5:40:40pm Bot:
> Consumer 'bot-consumer-02' received : hello again
5:50:15 User:
> How are you? 
5:50:16 Bot:
> Consumer 'bot-consumer-01' received : How are you?
6:01:22pm User:
> Are you sure you are alone?
6:01:23pm Bot:
> Consumer 'bot-consumer-02' received : Are you sure you are alone?
```
