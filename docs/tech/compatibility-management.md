# BDK compatibility management

You can check our existing change management policy for our HTTP API [here](https://developers.symphony.com/restapi/docs/api-change-management).

## API change management

Using features supported by new APIs will require using recent versions of the BDK, the table below shows which product version of the APIs is supported by the BDK:

| Product Version    | Supported by BDK |
|--------------------|------------------|
| 20.9 and before    | 2.0.0            |

The supported version of SBE and Agent corresponding to the product version of APIs can be found in 
[Agent compatibilities](https://developers.symphony.com/restapi/docs/agent-compatibilities).

Rationale of our change management policy is to be backward compatible on existing features
(classes, methods or [configuration](../configuration.md) fields) whenever possible.

## Deprecations and breaking changes

For keeping the backward compatibility of the BDK, the BDK api classes will be generated from the xx-api-public-deprecated.yaml 
file instead of the normal one. By doing this way, the generated APIs which are deprecated will not be completely removed.
They are only annotated with `@Deprecated`. 

Then, the BDK services which are using these deprecated APIs will be annotated with `@Deprecated` as well.
Then the bot developers will be warned that these services are now deprecated and will be removed in the future without having any breaking changes.

In case of a non-backward compatible change:
* we will mark the configuration field,
class or method as deprecated in a given `x.y.z` version.
* Version `x.(y + 1).0` will start logging a `WARN` message if the class, method or configuration field is called.
  Since deprecation messages are logged using a dedicated `com.symphony.bdk.deprecation` logger, logs can be disabled
  by setting its log level to `ERROR`. See [log4j.properties](../../symphony-bdk-examples/bdk-core-examples/src/main/resources/log4j.properties)
  or [logback.xml](../../symphony-bdk-examples/bdk-core-examples/src/main/resources/logback.xml) on how to configure this.
* Deprecated items will be then removed in `x.(y + 2).0` version *only* if a replacement has been
documented and is available.

Deprecations and subsequent class, method or field removal will all be documented in
[release notes](https://github.com/SymphonyPlatformSolutions/symphony-api-client-java/releases).

## BDK services

BDK services is a layer in BDK which are the wrappers of all the REST api endpoints provided by SBE and Agent. 
These services provide bot developers a more friendly way to interact with SBE and Agent. 
Developers now no more need to precisely create a HTTP call to the endpoint which is now under 
mission of the BDK services layer.

The BDK services relies on the many APIs classes which are generated automatically by Swagger 
OpenAPI from several swagger specs files describing all the endpoints provided by SBE/Agent. 
[Symphony-api-spec](https://github.com/symphonyoss/symphony-api-spec)

## Symphony-api-spec

Symphony-api-spec is a project that contains the specifications of all the endpoints exposed 
by SBE and Agent. 
These specifications are divided into 4 major parts: agent, pod, authenticator, login.

Each part contains 2 files: 

* xx-api-public.yaml : contains the information of the endpoints which are currently active on SBE/Agent.

* xx-api-public-deprecated.yaml : a superset of the xx-api-public.yaml which contain the information of all the endpoints which are both active and deprecated on SBE/Agent.

The Api classes in BDK will be generated from these specifications and then be used in BDK services and 
BDK authenticator (a special BDK service).

## BDK compatibility based on the version of Symphony-api-spec
Because the BDK used the Symphony-api-spec for generating the Api classes, then the compatibility between 
BDK and SBE/Agent will depend on the version of Symphony-api-spec that the BDK is using and the compatibility between 
the specs and the SBE/Agent.

From the official version 2.0.0 of the BDK, we start using the version branch of the Symphony-api-spec 
(20.9 for the BDK 2.0.0), it means that the version of the BDK will compatible with the product version 20.9.0.
