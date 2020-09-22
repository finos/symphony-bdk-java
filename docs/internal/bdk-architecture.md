# BDK2.0 - Project Architecture

The BDK project is composed in a set of different modules. The approach consists in having one module per BDK 
"layer".

## The layers

The BDK is divided in 3 different layers: 
- `core` that contains the minimal set of classes to configure, authenticate and use 
the main APIs
- `advanced` that contains additional features on top of the core module such as the
command API, the template API or even an NLP integration
- `framework` that provides connectors (or starters) for the main Java frameworks
such as [SpringBoot](https://spring.io/projects/spring-boot), 
[MicroProfile](https://projects.eclipse.org/projects/technology.microprofile), 
[Micronaut](https://micronaut.io/) or [Quarkus](https://quarkus.io/)

### Core
