# Configuration

The BDK configuration is one of the most basic feature of the BDK project which help
developers configure their own bot.

## Configuration structure

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

### Retry Configuration
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

### DatafeedConfiguration
The datafeed configuration will contain information about the datafeed service to be used by the bot:
- `version`: the version of datafeed service to be used. By default, the bot will use the datafeed v1
service. 
- `idFilePath`: the path to the file which will be used to persist a created datafeed id in case the 
datafeed service v1 is used.
- `retry`: the specific retry configuration can be used to override the global retry configuration. If no
retry configuration is defined, the global one will be used.

## Configuration format

Both of `JSON` and `YAML` formats are supported by BDK configuration. 

## Configuration usage

BDK configuration can be read:
- From a file: `BdkConfigLoader#loadFromFile` giving the path to the configuration file.
- From an InputStream: `BdkConfigLoader#loadFromInputStream` giving the input stream
of the configuration file.
- From a classpath: `BdkConfigLoader#loadFromClasspath` giving the classpath to the
configuration file.

The configuration file after being read will be represented by `BdkConfig` class which contains:

- The configuration for `pod`, `agent`, `keyManager`is represented by `BdkClientConfig`.
- The configuration for `bot` is represented by `BdkBotConfig`.
- The configuration for `app` is represented by `BdkExtAppConfig`.
- The configuration for `ssl` is represented by `BdkSslConfig`.

## Backward Compatibility with legacy configuration file  

The legacy configuration using by the Java SDK v1 is also supported in BDK Configuration 2.0.

This legacy configuration can be also read from a file, an inputstream, a classpath and automatically
translated to `BdkConfig` instance.
