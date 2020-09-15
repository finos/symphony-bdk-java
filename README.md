# Symphony BDK Java 
[![CircleCI](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java.svg?style=shield)](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java)
[![Known Vulnerabilities](https://snyk.io/test/github/SymphonyPlatformSolutions/symphony-api-client-java/badge.svg)](https://snyk.io/test/github/SymphonyPlatformSolutions/symphony-api-client-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.symphony.platformsolutions/symphony-api-client-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.symphony.platformsolutions/symphony-api-client-java)
[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](https://opensource.org/licenses/MIT)
[![Email](https://img.shields.io/static/v1?label=contact&message=email&color=darkgoldenrod)](mailto:platformsolutions@symphony.com?subject=Java%20SDK)

The Symphony Java BDK helps you to create Bots and Applications on top of the [Symphony REST APIs](https://developers.symphony.com/restapi/reference). 

Documentation about BDK features and usage is available under [docs](./docs/index.md) folder.

## Build from Source
The Symphony BDK uses a Maven [build](https://maven.apache.org/) build. The instructions below use the [Maven Wrapper](https://github.com/takari/maven-wrapper)
from the root of the source treen. The wrapper script serves as a cross-platform, self-contained bootstrap mechanism for
the build system.

### Before you start
To build you will need [Git](https://docs.github.com/en/github/getting-started-with-github/set-up-git) and [JDK 8 or later](https://adoptopenjdk.net/).
Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8+` folder extracted from the JDK download.

### Get the source
```shell script
git clone git@github.com:SymphonyPlatformSolutions/symphony-api-client-java.git
cd symphony-api-client-java
```
### Build from the Command Line
To compile, test and build all jars, use:
```shell script
./mvnw clean package
```
### Install in local Maven repository
To install all Symphony BDK jars in your local Maven repository, use:
```shell script
./mvnw clean install
```

## License

The Symphony BDK is Open Source software released under the [MIT License](./LICENSE).
