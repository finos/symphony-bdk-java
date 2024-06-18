---
layout: default
title: Presence API
nav_order: 11
---

# Presence API

The Presence Service is a component at the service layer of the BDK which covers the Presence part of the [REST API documentation](https://developers.symphony.com/restapi/main/presence).
More precisely:
* [Get presence](https://developers.symphony.com/restapi/main/presence/get-presence)
* [Get All Presence](https://developers.symphony.com/restapi/main/presence/get-all-presence)
* [Get User Presence](https://developers.symphony.com/restapi/main/presence/user-presence-v3)
* [External Presence Interest](https://developers.symphony.com/restapi/main/presence/register-user-presence-interest)
* [Set Presence](https://developers.symphony.com/restapi/main/presence/set-presence)
* [Create Presence Feed](https://developers.symphony.com/restapi/deprecated-endpoints/create-presence-feed) :warning: **Deprecated**
* [Read Presence Feed](https://developers.symphony.com/restapi/deprecated-endpoints/read-presence-feed) :warning: **Deprecated**
* [Delete Presence Feed](https://developers.symphony.com/restapi/deprecated-endpoints/delete-presence-feed) :warning: **Deprecated**
* [Set Other User's Presence](https://developers.symphony.com/restapi/main/presence/set-user-presence)


## How to use
The central component for the Presence Service is the `PresenceService` class, it exposes the service APIs endpoints mentioned above.
The service is accessible from the`SymphonyBdk` object by calling the `presences()` method:

```java
@Slf4j
public class Example {
  public static final Long USER_ID = 123456789L;

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get presence details
    PresenceService presences = bdk.presences();
    V2Presence userPresence = presences.getUserPresence(USER_ID, true);
    log.info("User presence: " + userPresence);
  }
}
```
