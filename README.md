# Symphony Bot Framework

This framework streamlines the bot and extension app creation process by abstracting away many of the complexities and required boilerplates. Through simple and intuitive extension points, developers can inject their own logic to handle bot commands, Symphony events, notifications coming from external systems and more.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
* JDK 1.8
* Maven 3.0.5+
* Service account configured in Symphony Admin portal
* Extension app setup in Symphony Admin portal (optional)

### Setting the service account
In order to register a sevice account (or Extension app) in Symphony Admin portal a RSA key pair is required. The bot application uses the private key while Symphony needs to know the public one. Once you have configured that (instructions not covered here), place the private key into /certs directory at the top level of the project structure.

In addition to the RSA keys, make sure the property botUsername (and appId) in src/main/resources/bot-config.json file matches the value configured in Symphony Admin portal.

### POD configuration
In src/main/resources/bot-config.json you will find configuration properties where you can specify the details of your POD. Fill out the following properties to make the framework to point to your POD.

* sessionAuthHost
* sessionAuthPort
* keyAuthHost
* keyAuthPort
* podHost
* podPort
* agentHost
* agentPort

### Running locally
The framework is built using the Spring Boot Framework and uses Maven to manage the dependencies.

1st Step - Install all of the project's dependencies
```
mvn clean install
```
2st Step - Run the framework using Maven and Spring Boot
```
mvn clean spring-boot:run
```
To run it in debug mode, provide the following parameters:
```
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```
Bind the IDE of your choice the the specified port (e.g. 5005).

### Testing commands
Sample commands are shipped with the framework as a way to assist developers to understand the mechanics of the framework. If framework is properly configured to point to your POD, create an IM or chat room with the bot (search it by the display name you configured in Symphony Admin portal).

All the sample commands require mentioning the bot (e.g. @MyBot), although you can specify any other pattern when creating your own commands.

**Hello command**

Simple hello world command.

&#9679; John Doe

@MyBot hello

&#9679; MyBot

Hello, **John Doe**


**Help command**

Static help message.

&#9679; John Doe

@MyBot help

&#9679; MyBot

Hi, how can I help you today?


**Link room command**

Returns a URL that you can use to receive notifications from external system into the given Symphony room. To test it, submit a HTTP POST request to the returned URL.

&#9679; John Doe

@MyBot link room

&#9679; MyBot

http://localhost:8080/myproject/notification/GhaWqOo6jRsHv5adBv4q73___pK2eM94dA


**Quote command**

Relies on the RestClient library offered by the framework to request quotes for foreigner currencies on a external system.

&#9679; John Doe

@MyBot /quote BRL

&#9679; MyBot

USD-BRL X-RATE

**3.99**<sub>BRL</sub>


**Login command**

Returns the HTTP header required to perform authenticated requests to external systems. Sample code includes two implementations of the AuthenticationProvider interface representing Basic and OAuth v2 authentication.

&#9679; John Doe

@MyBot login

&#9679; MyBot

**User authenticated**. Please add the following HTTP header to your requests:

```
Authorization: Basic am9obi5kb2VAc3ltcGhvbnkuY29tOnN0cm9uZ3Bhc3M=
```
