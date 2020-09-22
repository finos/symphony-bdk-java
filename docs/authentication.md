# Authentication
The Symphony API supports 2 different types of authentication for bots and apps: 
- authentication using an RSA Public/Private Key Pair
- authentication using Client Certificate

This documentation describes how to configure and use those different types of authentication.

## Bot authentication
This section will explain how to set up your configuration file in order to use either RSA or certificate-based 
authentication.

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
    certificatePath: /path/to/certificate.cer
    certificatePassword: YourCertificatePassword
```

### Authentication deep-dive
The code snippet below explains how to manually retrieve your bot authentication session. However, note that in most of 
the cases those operations are done behind the scene through the `SymphpnyBdk` entry point.
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

## App authentication

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
    certificatePath: /path/to/certificate.cer
    certificatePassword: YourCertificatePassword
```

### OBO (On Behalf Of) authentication
> Read more about OBO authentication [here](https://developers.symphony.com/symphony-developer/docs/obo-overview)


----
[Home :house:](./index.md)
