# Configuration

The BDK configuration is one of the most essential feature of the Symphony BDK which allows developers to configure 
their bot environment.

## Minimal configuration example
The minimal configuration file that can be provided look like:
```yaml
host: acme.symphony.com                                     # (1)

bot: 
    username: bot-username                                  # (2)
    privateKeyPath: /path/to/bot/rsa-private-key.pem        # (3)
```
1. hostname of your Symphony pod environment
2. your bot (or service account) username as configured in your pod admin console (https://acme.symphony.com/admin-console)
3. your bot RSA private key according to the RSA public key upload in your pod admin console (https://acme.symphony.com/admin-console)

## How to load configuration
The Symphony BDK provides a single way to configure your bot environment. 

```java
public class Example {
    
    public static void main(String[] args) {
      
      final BdkConfig config01 = BdkConfigLoader.loadFromFile("/absolute/path/to/config.yaml");             // (1)
      final BdkConfig config02 = BdkConfigLoader.loadFromClasspath("/config.yaml");                         // (2)

      final InputStream configInputStream = new FileInputStream(new File("/absolute/path/to/config.yaml"));
      final BdkConfig config03 = BdkConfigLoader.loadFromInputStream(configInputStream);                    // (3)

      final BdkConfig config04 = BdkConfigLoader.loadFromSymphonyDir("config.yaml");                        // (4)
  
      final BdkConfig config05 = new BdkConfig();                                                           // (5)
      config05.setHost("acme.symphony.com");
      config05.getBot().setUsername("bot-username");
      config05.getBot().setPrivateKeyPath("/path/to/bot/rsa-private-key.pem");
    }
}
```
1. Load configuration from a system location path
2. Load configuration from a classpath location
3. Load configuration from an [`InputStream`](https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html)
4. Load configuration from the Symphony directory. The Symphony directory is located under your `${user.home}/.symphony` 
    folder. It can be useful when you don't want to share your own Symphony credentials within your project codebase
5. Last but not least, you can obviously define your configuration object as a [POJO](https://en.wikipedia.org/wiki/Plain_old_Java_object) 
    and load it from any external system

## Full configuration example
```yaml
scheme: https
host: localhost.symphony.com
port: 8443

pod:
  host: dev.symphony.com
  port: 443

agent:
  context: agent

keyManager:
  host: dev-key.symphony.com
  port: 8444

sessionAuth:
  host: dev-session.symphony.com
  port: 8444

bot:
  username: bot-name
  privateKeyPath: /path/to/bot/rsa-private-key.pem
  certificatePath: /path/to/bot-certificate.p12
  certificatePassword: changeit

ssl:
  trustStorePath: /path/to/all_symphony_certs_truststore
  trustStorePassword: changeit

app:
  appId: app-id
  privateKeyPath: path/to/private-key.pem

datafeed:
  version: v1
  retry:
    maxAttempts: 6
    initialIntervalMillis: 2000
    multiplier: 1.5
    maxIntervalMillis: 10000

retry:
  maxAttempts: 6
  initialIntervalMillis: 2000
  multiplier: 1.5
  maxIntervalMillis: 10000
```

### Configuration structure

The BDK configuration now includes the following properties:
- The BDK configuration can contain the global properties for `host`, `port`, `context` and `scheme`. 
These global properties can be used by the client configuration by default or can be override if
user specify the dedicated `host`, `port`, `context`, `scheme` inside the client configuration.
- `pod` contains information like host, port, scheme, context, proxy... of the pod on which 
the service account using by the bot is created.
- `agent` contains information like host, port, scheme, context, proxy... of the agent which 
the bot connects to.
- `keyManager` contains information like host, port, scheme, context, proxy... of the key 
manager which manages the key token of the bot.
- `bot` contains information about the bot like the username, the private key or 
the certificate for authenticating the service account on pod.
- `app` contains information about the extension app that the bot will use like 
the appId, the private key or the certificate for authenticating the extension app.
- `ssl` contains trustStore and trustStore password for SSL communication.
- `datafeed` contains information of the datafeed service to be used by the bot.
- `retry` contains information for retry mechanism to be used by the bot.

#### Retry Configuration
The retry mechanism used by the bot will be configured by these following properties:
- `maxAttempts`: maximum number of retry attempts that the bot is able to make.
- `multiplier`: after each attempt, the interval between two attempts will be multiplied by 
this factor. (Exponential backoff strategy)
- `initialIntervalMillis`: the initial interval between two attempts.
- `maxIntervalMillis`: the limit of the interval between two attempts. For example: if the 
current interval is 1000 millis, multiplier is 2.0 and the maxIntervalMillis is 1500 millis,
then the interval for next retry will be 1500 millis.

Each bot will have a global retry configuration to be used in every services with the following
default value:
- `maxAttempts`: 10
- `initialIntervalMillis`: 500
- `multiplier`: 2
- `maxIntervalMillis`: 300000 (5 mins)

This global retry configuration can be override by each service. We can define a specific retry 
configuration inside service configuration to override the global one.

#### DatafeedConfiguration
The datafeed configuration will contain information about the datafeed service to be used by the bot:
- `version`: the version of datafeed service to be used. By default, the bot will use the datafeed v1
service. 
- `idFilePath`: the path to the file which will be used to persist a created datafeed id in case the 
datafeed service v1 is used.
- `retry`: the specific retry configuration can be used to override the global retry configuration. If no
retry configuration is defined, the global one will be used.

## Configuration format
Both of `JSON` and `YAML` formats are supported by BDK configuration. Using `JSON`, a minimal configuration file would 
look like: 
```json
{
  "host": "acme.symphony.com",
  "bot": { 
    "username": "bot-username",
    "privateKeyPath": "/path/to/bot/rsa-private-key.pem"
  }
}
``` 
Reading a `JSON` configuration file is completely transparent: 
```java
public class Example {
    
    public static void main(String[] args) {
      
      final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.json");
    }
}
```

## Backward Compatibility with legacy configuration file (experimental) 

The legacy configuration using by the Java SDK v1 is also supported in BDK Configuration 2.0.

This legacy configuration can be also read from a file, an inputstream, a classpath and automatically
translated to `BdkConfig` instance.

----
[Home :house:](./index.md)
