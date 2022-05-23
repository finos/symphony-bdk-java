# Connection Service

The Connection Service is a component at the service layer of the BDK which aims to cover the Connections part of the [REST API documentation](https://developers.symphony.com/restapi/reference).
More precisely:
* [Get connection](https://developers.symphony.com/restapi/reference#get-connection)
* [List connections](https://developers.symphony.com/restapi/reference#list-connections)
* [Create connection](https://developers.symphony.com/restapi/reference#create-connection)
* [Accept connection](https://developers.symphony.com/restapi/reference#accepted-connection)
* [Reject connection](https://developers.symphony.com/restapi/reference#reject-connection)
* [Remove connection](https://developers.symphony.com/restapi/reference#remove-connection)


## How to use
The central component for the Connection Service is the `ConnectionService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `connections()` method:


## How to use
The central component for the Connection Service is the `ConnectionService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `connections()` method:
```java
public class Example {
  public static final Long USER_ID = 123456789L;

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get connection status
    ConnectionService connections = bdk.connections();
    UserConnection connection = connections.getConnection(123L);
    log.info("Connection status: " + connection.getStatus());
  }
}
