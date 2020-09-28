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

### Bot authentication using RSA
> Read more about RSA authentication [here](https://developers.symphony.com/symphony-developer/docs/rsa-bot-authentication-workflow)

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
bot:
    username: bot-username
    privateKeyPath: /path/to/rsa/private-key.pem
```

### Bot authentication using Client Certificate
> Read more about Client Certificate authentication [here](https://developers.symphony.com/symphony-developer/docs/bot-authentication-workflow-1)

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
bot:
    username: bot-username
    certificatePath: /path/to/certificate.p12
    certificatePassword: YourCertificatePassword
```

### Configuration priority
If you configure both private key and certificate within the same `config.yaml`, note that the certificate will have
a higher priority over the RSA configuration.

Configuring both RSA and certificate authentication isn't recommended. 

### Bot authentication deep-dive
The code snippet below explains how to manually retrieve your bot authentication session. However, note that by default 
those operations are done behind the scene through the `SymphpnyBdk` entry point.
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

### Multiple bot instances
By design, the `SymphonyBdk` object contains a single bot session. However, you might want to create an application that
has to handle multiple bot sessions, potentially using different authentication modes. This is possible by creating 
multiple instances of `SymphpnyBdk` using different configurations:
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

### App authentication using RSA

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
app:
    appId: app-id
    privateKeyPath: /path/to/rsa/private-key.pem
```

### App Authentication using Client Certificate

Required `config.yaml` setup: 
```yaml
host: acme.symphony.com
app:
    appId: app-id
    certificatePath: /path/to/certificate.p12
    certificatePassword: YourCertificatePassword
```

### Circle Of Trust
> Read more about OBO authentication [here](https://developers.symphony.com/extension/docs/application-authentication#section-application-authentication-sequence)

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

The following example shows how to retrieve OBO sessions using `username` (type `String`) or `userId` (type `Long`):
```java
public class Example {

  public static void main(String[] args) throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
    
    final AuthSession oboSessionUsername = bdk.obo("user.name");
    final AuthSession oboSessionUserId = bdk.obo(123456789L);
  }
}
```
----
[Home :house:](./index.md)
