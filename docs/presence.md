# Presence Service

The Presence Service is a component at the service layer of the BDK which covers the Presence part of the [REST API documentation](https://developers.symphony.com/restapi/reference).
More precisely:
* [Get presence](https://developers.symphony.com/restapi/reference#get-presence)
* [Get All Presence](https://developers.symphony.com/restapi/reference#get-all-presence)
* [Get User Presence](https://developers.symphony.com/restapi/reference#user-presence-v3)
* [External Presence Interest](https://developers.symphony.com/restapi/reference#register-user-presence-interest)
* [Set Presence](https://developers.symphony.com/restapi/reference#set-presence)
* [Create Presence Feed](https://developers.symphony.com/restapi/reference#create-presence-feed)
* [Read Presence Feed](https://developers.symphony.com/restapi/reference#read-presence-feed)
* [Delete Presence Feed](https://developers.symphony.com/restapi/reference#delete-presence-feed)
* [Set Other User's Presence](https://developers.symphony.com/restapi/reference#set-user-presence)


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
