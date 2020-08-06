# Configuration

The BDK configuration is one of the most basic feature of the BDK project which help
developers configure their own bot.

## Configuration structure

The BDK configuration now includes the following properties:
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