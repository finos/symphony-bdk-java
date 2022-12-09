# BDK Extension App Spring Boot Starter
The Symphony BDK for Java provides a _Starter_ module that aims to ease extension app backend developments within a
[Spring Boot](https://spring.io/projects/spring-boot) application.
 
## Features
- Configure extension app through `application.yaml`
- Application health info through Spring Boot standard actuator endpoint
- Expose a REST API to perform [Circle of Trust authentication](https://docs.developers.symphony.com/building-extension-applications-on-symphony/app-authentication/circle-of-trust-authentication).

## Installation

The following listing shows the `pom.xml` file that has to be created when using Maven:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>bdk-app-spring-boot</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bdk-app-spring-boot</name>
    
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.finos.symphony.bdk</groupId>
                <artifactId>symphony-bdk-bom</artifactId>
                <version>2.1.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.finos.symphony.bdk</groupId>
            <artifactId>symphony-bdk-app-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.finos.symphony.bdk</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>2.3.4.RELEASE</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```
The following listing shows the `build.gradle` file that has to be created when using Gradle:
```groovy
plugins {
    id 'java-library'
    id 'org.springframework.boot' version '2.3.4.RELEASE'
}

dependencies {
    implementation platform('org.finos.symphony.bdk:symphony-bdk-bom:2.1.0')
    
    implementation 'org.finos.symphony.bdk:symphony-bdk-app-spring-boot-starter'
}
```

## Create a Simple Backend for Extension Application
As a first step, you have to initialize your environment through the Spring Boot `src/main/resources/application.yaml` file: 
```yaml
bdk:
    host: acme.symphony.com
    bot:
      username: bot-username
      privateKey:
        path: /path/to/rsa/privatekey.pem
      
    app:
        appId: app-id
        privateKey:
          path: /path/tp/rsa/app_privatekey.pem

bdk-app:
    auth:
      enabled: true # activate the CircleOfTrust endpoints (default is true)
      jwtCookie:
        enabled: true # activate the jwt cookie storage (default is false)
        sameSite: Strict # same site configuration to restrict the jwt cookie from cross-site domain (default is Strict)
        expireIn: 1d # jwt cookie duration (default value is 1d, see https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config-conversion-duration) 
    cors: # enable Cross-Origin Resource Sharing (CORS) communication
      "[/**]": # url mapping
        allowed-origins: "*" # list of allowed origins path pattern that be specific origins,
        allow-credentials: false # Access-Control-Allow-Credentials response header for CORS request
        allowed-methods: ["POST", "GET"] # list of HTTP methods to allow
        allowed-headers: "*" # list of headers that a request can list as allowed (multiple values allowed by using ["header-name-1", "header-name-2"])
        exposed-headers: ["header-name-1", "header-name-2"] # list of response headers that a response can have and can be exposed, the value "*" is not allowed for this field.
    tracing:
        enabled: true # activate the tracing filter
        urlPatterns:  # Add URL patterns that the tracing filter will be registered against
          - /api/*
logging:
    level:
        com.symphony: debug # in development mode, it is strongly recommended to set the BDK logging level at DEBUG
``` 
> You can notice here that the `bdk` property inherits from the [`BdkConfig`](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-config/latest/com/symphony/bdk/core/config/model/BdkConfig.html) class.

As required by Spring Boot, you have to create an `src/main/java/com/example/bot/ExtAppSpringApplication.java` class:
```java
@SpringBootApplication
public class ExtAppSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExtAppSpringApplication.class, args);
    }
}
```

## Circle of Trust

By configuring the property `bdk-app.auth.enabled=true`, the Application backend will provide Apis for performing
the [Circle of Trust](https://docs.developers.symphony.com/building-extension-applications-on-symphony/app-authentication/circle-of-trust-authentication) of Symphony:
[Circle of Trust API](https://editor.swagger.io/?url=https://raw.githubusercontent.com/SymphonyPlatformSolutions/symphony-api-client-java/master/docs/spring-boot/circle-of-trust.yaml)


## Application health status
The starter exposes the core components health status through the actuator endpoint from Spring Boot, for instance,
http://localhost:8080/${root_path}/actuator/health/, which gives a global application health status, or
http://localhost:8080/${root_path}/actuator/health/symphonyBdk, which specifically gives the Symphony components
health status only.

By default, the components health details are not exposed by Spring Boot. In order to see the details, the following
configuration must set in the `application.xml`

```
management:
  endpoint:
    web:
      exposure:
        include: 'symphonyBdk'
    health:
      show-details: "ALWAYS"
```      
