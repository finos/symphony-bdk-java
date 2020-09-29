# Symphony BDK for Java 
[![CircleCI](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java.svg?style=shield)](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java)
[![Known Vulnerabilities](https://snyk.io/test/github/SymphonyPlatformSolutions/symphony-api-client-java/badge.svg)](https://snyk.io/test/github/SymphonyPlatformSolutions/symphony-api-client-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.symphony.platformsolutions/symphony-bdk-bom/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.symphony.platformsolutions/symphony-bdk-bom)
[![javadoc](https://javadoc.io/badge2/com.symphony.platformsolutions/symphony-bdk-core/javadoc.svg)](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core)
[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](https://opensource.org/licenses/MIT)
[![Email](https://img.shields.io/static/v1?label=contact&message=email&color=darkgoldenrod)](mailto:platformsolutions@symphony.com?subject=Java%20SDK)

The Symphony BDK for Java helps you to create production-grade Chat Bots and Extension Applications on 
top of the [Symphony REST APIs](https://developers.symphony.com/restapi/reference). 

## Installation and Getting Started
The [reference documentation](./docs/index.md) includes detailed installation instructions as well as a comprehensive 
[getting started](./docs/getting-started.md) guide.

Here is a quick teaser of a complete Symphony BDK application in Java:
```java
public class BotApplication {
    
    public static void main(String[] args) {
      
        final SymphonyBdk bdk = new SymphonyBdk(BdkConfigLoader.loadFromSymphonyDir("config.yaml"));
      
        bdk.activities().register(slash("/hello", context -> {
            bdk.messages().send(context.getStreamId(), "<messageML>Hello, World!</messageML>");
        }));
        
        bdk.datafeed().start();
    }
}
```

## Build from Source
The Symphony BDK uses a [Gradle](https://docs.gradle.org/) build. The instructions below use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
from the root of the source tree. The wrapper script serves as a cross-platform, self-contained bootstrap mechanism for
the build system.

### Before you start
To build you will need [Git](https://docs.github.com/en/github/getting-started-with-github/set-up-git) and [JDK 8 or later](https://adoptopenjdk.net/).
Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8+` folder extracted from the JDK download.

### Build from the Command Line
To compile, test and build all jars, use:
```shell script
./gradlew
```
### Install in local Maven repository
To install all Symphony BDK jars in your local Maven repository, use:
```shell script
./gradlew publishToMavenLocal
```

## License
The Symphony BDK is Open Source software released under the [MIT License](./LICENSE).
