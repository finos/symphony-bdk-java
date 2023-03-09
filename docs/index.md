# Symphony BDK Reference Documentation

This reference guide provides detailed information about the Symphony BDK. It provides a comprehensive documentation
for all features and abstractions made on top of the [Symphony REST API](https://developers.symphony.com/restapi/reference/introduction).

If you are just getting started with Symphony Bot developments, you may want to begin reading the
[Getting Started](./getting-started.md) guide.

The reference documentation consists of the following sections:

| Section                                  | Description                                                           |
|------------------------------------------|:---------------------------------------------------------------------:|
| [Getting Started](./getting-started.md)  | Introducing Symphony BDK for beginners                                |
| [Migration Guide](./migration.md)        | Guide to migrate to Symphony BDK 2.0                                  |
| [Configuration](./configuration.md)      | Configuration structure, formats, how to load from code               |
| [Authentication](./authentication.md)    | RSA or certificate authentication, OBO, extension app authentication  |
| [Datafeed Loop](datafeed.md)             | Receiving real time events                                            |
| [Fluent API](fluent-api.md)              | Java Fluent API usage                                                 |
| [Message API](message.md)                | Sending or searching messages, usage of templates                     |
| [Stream API](stream.md)                  | Create and manage streams                                             |
| [User API](user.md)                      | Manage users                                                          |
| [Presence API](presence.md)              | Manage user presence status                                           |
| [Application API](application.md)        | Managing applications                                                 |
| [Signal API](signal.md)                  | Creating and managing signals                                         |
| [Connection API](connection.md)          | Managing connections                                                  |
| [Disclaimer API](disclaimer.md)          | Managing disclaimers                                                  |
| [Health API](health.md)                  | Get health check status                                               |
| [Activity API](activity-api.md)          | The Activity Registry, creating custom activities                     |
| [Extending the BDK](extension.md)        | How to use or develop BDK extensions                                  |
| [Integration Test](test.md)              | How to write the integration tests for Symphony BDK Bot application   |  

### Spring Boot
Getting Started guides are also available for Spring Boot:
- [Core Starter](./spring-boot/core-starter.md)
- [App Starter](./spring-boot/app-starter.md)

### Technical Documentation
You can find an overview of the BDK Architecture [here](./tech/architecture.md).
