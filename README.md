[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CircleCI](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java.svg?style=shield)](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java)
[![Known Vulnerabilities](https://snyk.io/test/github/SymphonyPlatformSolutions/symphony-api-client-java/badge.svg)](https://snyk.io/test/github/SymphonyPlatformSolutions/symphony-api-client-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.symphony.platformsolutions/symphony-bdk-bom/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.symphony.platformsolutions/symphony-bdk-bom)
[![javadoc](https://javadoc.io/badge2/com.symphony.platformsolutions/symphony-bdk-core/javadoc.svg)](https://javadoc.io/doc/com.symphony.platformsolutions/symphony-bdk-core)

# Symphony BDK for Java
The Symphony BDK for Java helps you to create production-grade Chat Bots and Extension Applications on 
top of the [Symphony REST APIs](https://developers.symphony.com/restapi/reference). 

## Installation and Getting Started
The [reference documentation](https://symphonyplatformsolutions.github.io/symphony-api-client-java) includes detailed installation instructions as well as a comprehensive 
[getting started](https://symphonyplatformsolutions.github.io/symphony-api-client-java/getting-started.html) guide.

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
To compile, test and build all BDK2.0 jars, use:
```shell script
./gradlew
```
To compile, test and build legacy jars:
```shell script
cd symphony-bdk-legacy
./gradlew
```
### Install in local Maven repository
To install all Symphony BDK jars in your local Maven repository, use:
```shell script
./gradlew publishToMavenLocal
```

## Contributing

1. Fork it (<https://github.com/SymphonyPlatformSolutions/symphony-api-client-java/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Read our [contribution guidelines](./CONTRIBUTING.md) and [Community Code of Conduct](https://www.finos.org/code-of-conduct)
4. Commit your changes (`git commit -am 'Add some fooBar'`)
5. Push to the branch (`git push origin feature/fooBar`)
6. Create a new Pull Request

_NOTE:_ Commits and pull requests to FINOS repositories will only be accepted from those contributors with an active, executed Individual Contributor License Agreement (ICLA) with FINOS OR who are covered under an existing and active Corporate Contribution License Agreement (CCLA) executed with FINOS. Commits from individuals not covered under an ICLA or CCLA will be flagged and blocked by the FINOS Clabot tool. Please note that some CCLAs require individuals/employees to be explicitly named on the CCLA.

*Need an ICLA? Unsure if you are covered under an existing CCLA? Email [help@finos.org](mailto:help@finos.org)*

## License
Copyright 2021 Symphony.com

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
