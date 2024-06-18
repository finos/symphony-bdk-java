---
layout: default
title: Disclaimer API
nav_order: 15
---

# Disclaimer API

The Disclaimer Service is a component at the service layer of the BDK which aims to cover the Disclaimers part of the [REST API documentation](https://developers.symphony.com/restapi/main/disclaimers).
More precisely:
* [Get disclaimer](https://developers.symphony.com/restapi/main/disclaimers/disclaimer)
* [List disclaimers](https://developers.symphony.com/restapi/main/disclaimers/list-disclaimers)
* [List disclaimer Users](https://developers.symphony.com/restapi/main/disclaimers/disclaimer-users)


## How to use
The central component for the Disclaimer Service is the `DisclaimerService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `disclaimers()` method:


```java
@Slf4j
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
```
