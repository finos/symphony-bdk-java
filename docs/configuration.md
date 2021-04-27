# Configuration

The BDK configuration is one of the most essential feature of the Symphony BDK which allows developers to configure 
their bot environment.

## Minimal configuration example
The minimal configuration file that can be provided look like:
```yaml
host: acme.symphony.com                                     # (1)

bot: 
    username: bot-username                                  # (2)
    privateKey:
      path: /path/to/bot/rsa-private-key.pem                # (3)
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
      config05.getBot().getPrivateKey().setPath("/path/to/bot/rsa-private-key.pem");
    }
}
```
1. Load configuration from a system location path
2. Load configuration from a classpath location
3. Load configuration from an [`InputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html)
4. Load configuration from the Symphony directory. The Symphony directory is located under your `${user.home}/.symphony` 
    folder. It can be useful when you don't want to share your own Symphony credentials within your project codebase
5. Last but not least, you can obviously define your configuration object as a [POJO](https://en.wikipedia.org/wiki/Plain_old_Java_object) 
    and load it from any external system

## Full configuration example
```yaml
scheme: https
host: localhost.symphony.com
port: 8443
connectionTimeout: 15000
readTimeout: 60000
connectionPoolMax: 20
connectionPoolPerRoute: 20
defaultHeaders:
  Connection: Keep-Alive
  Keep-Alive: timeout=5, max=1000
    
proxy:
  host: proxy.symphony.com
  port: 1234
  username: proxyuser
  password: proxypassword

pod:
  host: dev.symphony.com
  port: 443

agent:
  loadBalancing:
    mode: roundRobin
    stickiness: false
    nodes:
      - host: agent1.symphony.com
        port: 7443
        context: app/
      - host: agent2.symphony.com

keyManager:
  host: dev-key.symphony.com
  port: 8444
  defaultHeaders:
    Connection: Keep-Alive
    Keep-Alive: close
        
sessionAuth:
  host: dev-session.symphony.com
  port: 8444

bot:
  username: bot-name
  privateKey:
    path: /path/to/bot/rsa-private-key.pem
  certificate:
    path: /path/to/bot-certificate.p12
    password: changeit

ssl:
  trustStore:
    path: /path/to/all_symphony_certs_truststore
    password: changeit

app:
  appId: app-id
  privateKey:
    path: path/to/private-key.pem

datafeed:
  version: v1
  retry:
    maxAttempts: 6
    initialIntervalMillis: 2000
    multiplier: 1.5
    maxIntervalMillis: 10000

retry:
  maxAttempts: 6 # set '-1' for an infinite number of attempts, default value is '10'
  initialIntervalMillis: 2000
  multiplier: 1.5
  maxIntervalMillis: 10000
```

### Configuration structure

The BDK configuration now includes the following properties:
- The BDK configuration can contain the global properties for `host`, `port`, `context`, `scheme` and the following connection parameters: `connectionTimeout`, `readTimeout`, `connectionPoolMax`, `connectionPoolPerRoute`.
These global properties can be used by the client configuration by default or can be overridden if
user specify the dedicated `host`, `port`, `context`, `scheme` or custom connection parameters inside the client configuration. Please note that connection parameters are optional, `connectionPoolMax`, `connectionPoolPerRoute` are used only by Jersey2 connection implementation and in general default values (they are in the example file, but you can avoid specifying them explicitly if you don't want change them) fit most use cases.
- `proxy` contains proxy related information. This field is optional.
If set, it will use the provided `host` (mandatory), `port` (mandatory), `username` and `password`.
It can be overridden in each of the `pod`, `agent`, `keyManager` and `sessionAuth` fields.
- `pod` contains information like host, port, scheme, context, proxy... of the pod on which 
the service account using by the bot is created.
- `agent` contains information like host, port, scheme, context, proxy... of the agent which 
the bot connects to. It can also contain a `loadBalancing` field: if defined,
it should not any field among scheme, host, port, context.
- `keyManager` contains information like host, port, scheme, context, proxy... of the key 
manager which manages the key token of the bot.
- `bot` contains information about the bot like the username, the private key or 
the certificate for authenticating the service account on pod.
- `app` contains information about the extension app that the bot will use like 
the appId, the private key or the certificate for authenticating the extension app.
- `ssl` contains trustStore and trustStore password for SSL communication.
- `datafeed` contains information of the datafeed service to be used by the bot.
- `retry` contains information for retry mechanism to be used by the bot.

Although not recommended for RSA private keys, you can specify absolute paths to classpath resources for the following fields:
- `bot.privateKey.path`, `bot.certificate.path`
- `app.privateKey.path`, `app.certificate.path`
- `ssl.trustStore.path`

Only absolute paths to classpath resources are supported (i.e. paths beginning with `/`).

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

#### Agent load-balancing configuration
The `agent.loadBalancing` part of the configuration contains the information in order to load balance calls to the agent if wanted.
None of the fields `scheme`, `host`, `port`, `context` should be set if field `loadBalancing` is defined.
Fields inside `loadBalancing` are:
- `mode`: mandatory, can be `external`, `roundRobin` or `random`.
- `stickiness`: optional boolean, default value is true.
- `nodes`: mandatory and must contain at least one element. List items must have at least `host` field put and can contain the following other fields: `scheme`, `port`, `context`.

`roundRobin` and `random` modes mean calls to the agent are load balanced across all `nodes`, respectively in a round robin and random fashion.
`external` mode means each time we want to pick a new agent host, we make a call to the endpoint
[/v1/info](https://developers.symphony.com/restapi/reference#agent-info-v1) on the first node provided in `nodes`.
The actual agent URL is taken from the field `serverFqdn` in the response body.

When `stickiness` is set to true, it means one picks a given agent and makes all calls to the same agent node.
Otherwise, when `stickiness` is set to false, one picks a new agent node each time a call is made.

When using datafeed services, calls will always be sticky, regardless of the `stickiness` value.

### Proxy configuration
A proxy can be configured at root level or in `pod`, `agent`, `keyManager` or `sessionAuth`.
If a `proxy` field is defined at global level and in one of these fields, it will be overridden based on the endpoints called.
Fields inside `proxy` are:
* `host`: mandatory, host of the proxy, can be a dns name or an IP address, e.g. "proxy.symphony.com" or "10.12.34.45".
* `port`: mandatory, port of the proxy, must be a strictly positive integer.
* `username` and `password`: optional, basic authentication credentials for the proxy.

## Configuration format
Both of `JSON` and `YAML` formats are supported by BDK configuration. Using `JSON`, a minimal configuration file would 
look like: 
```json
{
  "host": "acme.symphony.com",
  "bot": { 
    "username": "bot-username",
    "privateKey": {
      "path": "/path/to/bot/rsa-private-key.pem"
    }
  }
}
``` 

### Field interpolation using Java system properties or environment variables
In both formats, you can use Java system properties and system environment variables as field values. For instance, `${user.home}` in any field will be
replaced by the actual value of Java system property `user.home`. Likewise for `$HOME`, mapping the environment variable `HOME`. If a property is not defined, no interpolation will be done
and string will be left as is. If the same key is defined both as a Java system property and an environment variable, the Java property value will take precedence.
A default value can be provided after `:-`, for instance `${property.name:-defaultValue}`.
Therefore, the following is a valid configuration file:

```json
{
  "host": "${subdomain:-acme}.symphony.com",
  "bot": {
    "username": "bot-username",
    "privateKey": {
      "path": "${HOME}/rsa-private-key.pem"
    }
  }
}
```
Please mind that if you want to escape the `$` sign, `$${value}` will be replaced by `${value}`.
And as the matching of environment variables is done after Java system properties, if you have a system property with **value** `${value}` and en environment variable with **key** `value`, it will substitute the value of the environment variable


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
