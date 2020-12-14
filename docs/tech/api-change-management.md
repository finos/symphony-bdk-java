# API Change management

Rationale of our change management policy is to be backward compatible on existing features
(classes, methods or [configuration](../configuration.md) fields) whenever possible.

In case of a non-backward compatible change:
* we will mark the configuration field,
class or method as deprecated in a given `x.y.z` version.
* Version `x.(y + 1).0` will start logging a `WARN` message if the class, method or configuration field is called.
It will be possible for developers to ignore those warnings through JVM parameter `bdk.warning.mode=none`.
* Deprecated items will be then removed in `x.(y + 2).0` version *only* if a replacement has been
documented and is available.

Deprecations and subsequent class, method or field removal will all be documented in
[release notes](https://github.com/SymphonyPlatformSolutions/symphony-api-client-java/releases).
