---
title: Home
layout: home
nav_order: 1
---

# Symphony BDK Reference Documentation

{% hint style="info" %}

### BDK version 3.0: Major change !
 
 The newly introduced BDK version 3.0 relies on Java 17 and SpringBoot 3. This is a major change that allows Symphony to continue to propose the latest security fixes following the end of support of Spring Boot 2 and also to keep up with the latest evolutions of Java.
 
 For the next 6 months Symphony will provide critical security fixes for BDK 2.0 where possible (since Spring gives no guarantees for their packages).
 
 Please consider migrating your Bots in the coming months to benefit from the latest features and support. 

{% endhint %}

{% hint style="warning" %}
 As detailed above, the BDK version 2.0 will stop being supported by Symphony on August 15.
{% endhint %}

This reference guide provides detailed information about the Symphony BDK. It provides a comprehensive documentation
for all features and abstractions made on top of the [Symphony REST API](https://developers.symphony.com/restapi/reference/introduction).

If you are just getting started with Symphony Bot developments, you may want to begin reading the
[Getting Started](./getting-started.html) guide.

The reference documentation consists of the following sections:

| Section                                  | Description                                                           |
|------------------------------------------|:---------------------------------------------------------------------:|
| [Getting Started](./getting-started.html)  | Introducing Symphony BDK for beginners                                |
| [Migration Guide](./migration.html)        | Guide to migrate to Symphony BDK 2.0                                  |
| [Configuration](./configuration.html)      | Configuration structure, formats, how to load from code               |
| [Authentication](./authentication.html)    | RSA or certificate authentication, OBO, extension app authentication  |
| [Datafeed Loop](datafeed.html)             | Receiving real time events                                            |
| [Fluent API](fluent-api.html)              | Java Fluent API usage                                                 |
| [Message API](message.html)                | Sending or searching messages, usage of templates                     |
| [Stream API](stream.html)                  | Create and manage streams                                             |
| [User API](user.html)                      | Manage users                                                          |
| [Presence API](presence.html)              | Manage user presence status                                           |
| [Application API](application.html)        | Managing applications                                                 |
| [Signal API](signal.html)                  | Creating and managing signals                                         |
| [Connection API](connection.html)          | Managing connections                                                  |
| [Disclaimer API](disclaimer.html)          | Managing disclaimers                                                  |
| [Health API](health.html)                  | Get health check status                                               |
| [Activity API](activity-api.html)          | The Activity Registry, creating custom activities                     |
| [Extending the BDK](extension.html)        | How to use or develop BDK extensions                                  |
| [Integration Test](test.html)              | How to write the integration tests for Symphony BDK Bot application   |

### Spring Boot
Getting Started guides are also available for Spring Boot:
- [Core Starter](./spring-boot/core-starter.html)
- [App Starter](./spring-boot/app-starter.html)

### Technical Documentation
You can find an overview of the BDK Architecture [here](./tech/architecture.html).
