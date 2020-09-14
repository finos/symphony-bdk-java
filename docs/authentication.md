# Authentication

## Bot authentication

To be updated...

## OBO authenticating using SymphonyBdk

On Behalf Of (OBO) is a pattern that enables developers to perform operations on behalf of a Symphony end-user. [`Getting started with OBO](https://developers.symphony.com/restapi/docs/get-started-with-obo)

With a [`SymphonyBdk`](../symphony-bdk-core/src/main/java/com/symphony/bdk/core/SymphonyBdk.java) instance, we can easily authenticate the bot in OBO mode:

```java
public class Example {
    
    public static void main(String[] args) {
        // Initialize the BDK entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("config.yaml"));
        // OBO authentication
        AuthSession oboSession = bdk.obo("user.name");
        // Running service in Obo mode
        final List<V2UserDetail> userDetails = bdk.users().listUsersDetail(new UserFilter(), oboSession);
    }
}

```
