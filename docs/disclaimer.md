# Disclaimer Service

The Disclaimer Service is a component at the service layer of the BDK which aims to cover the Connections part of the [REST API documentation](https://developers.symphony.com/restapi/reference).
More precisely:
* [Get disclaimer](https://developers.symphony.com/restapi/reference/disclaimer)
* [List disclaimers](https://developers.symphony.com/restapi/reference/list-disclaimers)
* [List disclaimer Users](https://developers.symphony.com/restapi/reference/disclaimer-users)


## How to use
The central component for the Disclaimer Service is the `DisclaimerService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `disclaimers()` method:


```java
public class Example {
  public static final Long DISCLAIMER_ID = "MY_DISCLAIMER_ID";

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get a disclaimer details
    DisclaimerService disclaimers = bdk.disclaimers();
    Disclaimer disclaimer = disclaimers.getDisclaimer(DISCLAIMER_ID);
    log.info("Disclaimer details: " + disclaimer);
  }
}
