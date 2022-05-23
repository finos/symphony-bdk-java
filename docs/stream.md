# Stream Service

The Stream Service is a component at the service layer of the BDK which aims to cover the Streams part of the [REST API documentation](https://developers.symphony.com/restapi/reference#messages-v4).
More precisely:
* [Get stream information](https://developers.symphony.com/restapi/reference#stream-info-v2)
* [Add a member to an existing room](https://developers.symphony.com/restapi/reference#add-member)
* [Remove a member from a room](https://developers.symphony.com/restapi/reference#remove-member)
* [Share third-party content](https://developers.symphony.com/restapi/reference#share-v3)
* [Promote user to room owner](https://developers.symphony.com/restapi/reference#promote-owner)
* [Demote owner to room participant](https://developers.symphony.com/restapi/reference#demote-owner)
* [Create IM or MIM](https://developers.symphony.com/restapi/reference#create-im-or-mim)
* [Create IM or MIM non-inclusive](https://developers.symphony.com/restapi/reference#create-im-or-mim-admin)
* [Create room](https://developers.symphony.com/restapi/reference#create-room-v3)
* [Search for rooms](https://developers.symphony.com/restapi/reference#search-rooms-v3)
* [Get room information](https://developers.symphony.com/restapi/reference#room-info-v3)
* [Deactivate or reactivate a room](https://developers.symphony.com/restapi/reference#de-or-re-activate-room)
* [Update a room](https://developers.symphony.com/restapi/reference#update-room-v3)
* [List streams](https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2)
* [List user streams](https://developers.symphony.com/restapi/reference#list-user-streams)
* [List stream members](https://developers.symphony.com/restapi/reference#stream-members)
* [List room members](https://developers.symphony.com/restapi/reference#room-members)


## How to use
The central component for the Message Service is the `StreamService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `streams()` method:
```java
public class Example {
  public static final String STREAM_ID = "MY_STRING_UD";

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get stream details
    StreamService streams = bdk.streams();
    V2StreamAttributes stream = streams.getStream(STREAM_ID);
    log.info("Stream details: " + stream);
  }
}
