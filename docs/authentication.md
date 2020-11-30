# Authentication
The Symphony BDK authentication API allows developers to authenticate their bots and apps using either RSA or
certificate authentication modes.

The following sections will explain you: 
- how to authenticate your bot service account
- how to authentication your extension application
- how to use OBO (On Behalf Of) authentication

## Bot authentication
In this section we will see how to authenticate a bot service account. You will notice that everything has to be done 
through your BDK `config.yaml`, making your code completely agnostic to authentication modes (RSA or certificate).

Only one of certificate or RSA authentication should be configured in one BDK `config.yaml`. If both of them are 
provided, an `AuthInitializationException` will be thrown when you try to authenticate to the bot service account.

### Bot authentication using RSA
> Read more about RSA authentication [here](https://developers.symphony.com/symphony-developer/docs/rsa-bot-authentication-workflow)

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
bot:
    username: bot-username
    privateKey:
      path: /path/to/rsa/private-key.pem
```

### Bot authentication using Client Certificate
> Read more about Client Certificate authentication [here](https://developers.symphony.com/symphony-developer/docs/bot-authentication-workflow-1)

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
bot:
    username: bot-username
    certificate:
      path: /path/to/certificate.p12
      password: YourCertificatePassword
```

### Bot authentication deep-dive
The code snippet below explains how to manually retrieve your bot authentication session. However, note that by default 
those operations are done behind the scene through the `SymphonyBdk` entry point.
```java
public class Example {

    public static void main(String[] args) throws Exception { 
        // create bdk entry point
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
        // at this point your bot is already authenticated
        // here's how to retrieve the authentication session
        final AuthSession botSession = bdk.botSession();
        log.info("sessionToken: {}", botSession.getSessionToken());
        log.info("keyManagerToken: {}", botSession.getKeyManagerToken());
        // if session has expired (e.g. an API endpoint returns 401), you can manually trigger a re-auth
        botSession.refresh();
    }
}
```

### Authentication using private key and certificate content
Instead of configuring the path of RSA private key or certificate in config file, you can also authenticate the bot and 
extension app by using directly the private key or certificate content. This feature is useful when either RSA private key 
or certificate are fetched from an external secrets storage. The code snippet below will give you an example showing 
how to set directly the private key content to the Bdk configuration for authenticating the bot.
```java
public class Example {

    public static void main(String[] args) throws Exception { 
        // Loading the configuration
        BdkConfig config = loadFromClasspath("/config.yaml");
        byte[] privateKeyContent = FileUtils.readFileToByteArray(new File("path/to/privatekey.pem"));
        config.getBot().getPrivateKey().setContent(privateKeyContent);
        // create bdk entry point
        final SymphonyBdk bdk = new SymphonyBdk(config);
        // at this point your bot is already authenticated
        // here's how to retrieve the authentication session
        final AuthSession botSession = bdk.botSession();
        log.info("sessionToken: {}", botSession.getSessionToken());
        log.info("keyManagerToken: {}", botSession.getKeyManagerToken());
        // if session has expired (e.g. an API endpoint returns 401), you can manually trigger a re-auth
        botSession.refresh();
    }
}
```

At the same time, only one of path and the content of private key or certificate are allowed to be configured. If both of
them are configured, an `AuthInitializationException` will be thrown.

### Multiple bot instances
By design, the `SymphonyBdk` object contains a single bot session. However, you might want to create an application that
has to handle multiple bot sessions, potentially using different authentication modes. This is possible by creating 
multiple instances of `SymphonyBdk` using different configurations:
```java
public class Example {

    public static void main(String[] args) throws Exception { 

        final SymphonyBdk bot1 = new SymphonyBdk(loadFromClasspath("/config-bot1.yaml"));
        final SymphonyBdk bot2 = new SymphonyBdk(loadFromClasspath("/config-bot2.yaml"));
        
        // bot2 creates an IM with bot1
        bot2.streams().create(bot1.botInfo().getId());
    }
}
```

## App authentication
Application authentication is completely optional but remains required if you want to implement the Circle Of trust 
or if you want to use OBO.

Only one of certificate or RSA authentication should be configured in one BDK `config.yaml`. If both of them are 
provided, an `AuthInitializationException` will be thrown when you try to authenticate to the extension application.

### App authentication using RSA

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
app:
    appId: app-id
    privateKey:
      path: /path/to/rsa/private-key.pem
```

### App Authentication using Client Certificate

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
app:
    appId: app-id
    certificate:
      path: /path/to/certificate.p12
      password: YourCertificatePassword
```

### Circle Of Trust
> Read more about Circle Of Trust [here](https://developers.symphony.com/extension/docs/application-authentication#section-application-authentication-sequence)

```java
public class Example {

  public static void main(String[] args) throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    final AppAuthSession appAuth = bdk.appAuthenticator().authenticateExtensionApp("appToken");

    final String ta = appAuth.getAppToken();
    final String ts = appAuth.getSymphonyToken();

    bdk.appAuthenticator().validateTokens(ta, ts);
  }
}
```

### OBO (On Behalf Of) authentication
> Read more about OBO authentication [here](https://developers.symphony.com/symphony-developer/docs/obo-overview)

The following example shows how to retrieve OBO sessions using `username` (type `String`) or `userId` (type `Long`)
and to call services which have OBO endpoints (users, streams and messages so far):
```java
public class Example {

  public static void main(String[] args) throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
    
    final AuthSession oboSessionUsername = bdk.obo("user.name");
    final AuthSession oboSessionUserId = bdk.obo(123456789L);
    
    // list streams OBO user "user.name"
    bdk.obo(oboSessionUsername).streams().listStreams(new StreamFilter());

    // or send a message OBO:
    Message message = Message.builder().content("<messageML>Hello, World</messageML>").build();
    bdk.obo(oboSessionUserId).messages().send("streamID", message);
  }
}
```
----
[Home :house:](./index.md)
