# API Change management

You can check our existing change management policy for our HTTP API [here](https://developers.symphony.com/restapi/docs/api-change-management).

Rationale of our change management policy is to be backward compatible on existing features
(classes, methods or [configuration](../configuration.md) fields) whenever possible.

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
