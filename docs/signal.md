# Signal Service

The Signal Service is a component at the service layer of the BDK which aims to cover the Signal part of the [REST API documentation](https://developers.symphony.com/restapi/reference).
More precisely:
* [List signals](https://developers.symphony.com/restapi/reference#list-signals)
* [Get a signal](https://developers.symphony.com/restapi/reference#get-signal)
* [Create a signal](https://developers.symphony.com/restapi/reference#create-signal)
* [Update a signal](https://developers.symphony.com/restapi/reference#update-signal)
* [Delete a signal](https://developers.symphony.com/restapi/reference#delete-signal)
* [Subscribe Signal](https://developers.symphony.com/restapi/reference#subscribe-signal)
* [Unsubscribe Signal](https://developers.symphony.com/restapi/reference#unsubscribe-signal)
* [Subscribers](https://developers.symphony.com/restapi/reference#subscribers)


## How to use
The central component for the Signal Service is the `SignalService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `signals()` method:

```java
public class Example {
  public static final String SIGNAL_ID = "MY_SIGNAL_ID";

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get signal details
    SignalService signals = bdk.signals();
    Signal signal = signals.getSignal(SIGNAL_ID);
    log.info("Signal details: " + signal);
  }
}
