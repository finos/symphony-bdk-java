# Getting Started with Symphony BDK for Java

This guide provides detailed information for beginners who want to bootstrap their first Symphony BDK project 
in Java. Two different approaches will be detailed here:
- using the Symphony Generator
- from scratch

## Starting with Symphony Generator
> This section requires `npm` ([Node Package Manager](https://www.npmjs.com/)) to be installed on your local machine as a prerequisite

For all Symphony BDK applications, you should start with the [Symphony Generator](https://github.com/SymphonyPlatformSolutions/generator-symphony).
The Symphony Generator offers a fast way to bootstrap your Symphony BDK project in several languages, including Java:
```
npm i -g generator-symphony
yo symphony 2.0
```

## Creating your project _from scratch_
This section will help you to understand how to create your bot application from scratch.

### Maven-based project
If you want to use [Maven](https://maven.apache.org/) as build system, you have to configure your root `pom.xml` as such:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>symphony-bdk</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>symphony-bdk</name>
    <description>Demo project for Symphony BDK</description>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.symphony.platformsolutions</groupId>
                <artifactId>symphony-bdk-bom</artifactId>
                <version>1.3.2.BETA</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- Core dependencies -->
        <dependency>
            <groupId>com.symphony.platformsolutions</groupId>
            <artifactId>symphony-bdk-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.symphony.platformsolutions</groupId>
            <artifactId>symphony-bdk-http-jersey2</artifactId> <!-- or symphony-bdk-http-webclient -->
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.symphony.platformsolutions</groupId>
            <artifactId>symphony-bdk-template-freemarker</artifactId>  <!-- or symphony-bdk-http-handlebars -->
            <scope>runtime</scope>
        </dependency>
        <!-- Logger Configuration -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```
### Gradle-based project
If you want to use [Gradle](https://gradle.org/) as build system, you have to configure your root `build.gradle` as such:
```groovy
plugins {
    id 'java-library'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {

    // import a BOM
    implementation platform('com.symphony.platformsolutions:symphony-bdk-bom:1.3.2.BETA')

    // define dependencies without versions
    implementation 'com.symphony.platformsolutions:symphony-bdk-core'
    runtimeOnly 'com.symphony.platformsolutions:symphony-bdk-http-jersey2' //  or symphony-bdk-http-webclient
    runtimeOnly 'com.symphony.platformsolutions:symphony-bdk-template-freemarker' // or symphony-bdk-http-handlebars

    // logger configuration
    implementation 'org.slf4j:slf4j-api'
    runtimeOnly 'ch.qos.logback:logback-classic'
}
```

### Create configuration file
Before implementing any code, you need to create your `src/main/resources/config.yaml` configuration file according 
to your Symphony environment:
```yaml
host: acme.symphony.com                                     # your own pod host name

bot: 
    username: bot-username                                  # your bot (or service account) username
    privateKey:
      path: /path/to/bot/rsa-private-key.pem        # your bot RSA private key
```
> Click [here](./configuration.md) for more detailed documentation about BDK configuration

### Create a Simple Bot Application
Now you can create a Simple Bot Application by creating main class `src/main/java/com/example/symphony/BotApplication.java`:
 
```java
public class BotApplication {
    
    public static void main(String[] args) {
      
        final SymphonyBdk bdk = new SymphonyBdk(BdkConfigLoader.loadFromClasspath("/config.yaml"));        // (1)
      
        bdk.datafeed().subscribe(new RealTimeEventListener() {                                              // (2)

            @Override
            public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
              bdk.messages().send(event.getMessage().getStream(), "<messageML>Hello, World!</messageML>");  // (3)
            }
        });
        
        bdk.datafeed().start();                                                                             // (4)
    }
}
```
1. The `SymphonyBdk` class acts as an entry point into the library and provides a [fluent API](./fluent-api.md) to access
to the main BDK features such as [Datafeed](./datafeed.md), services or [Activities](./activity-api.md)
2. Subscribe to the [`onMessageSent`](https://developers.symphony.com/restapi/docs/real-time-events#section-message-sent) 
[Real Time Event](https://developers.symphony.com/restapi/docs/real-time-events)
3. When any message is sent into a stream where your bot is a member, it will reply by message `Hello, World`! 
4. Start the Datafeed read loop

----
[Home :house:](./index.md)
 
