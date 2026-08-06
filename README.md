[![Build](https://github.com/finos/symphony-bdk-java/actions/workflows/build.yml/badge.svg)](https://github.com/finos/symphony-bdk-java/actions/workflows/build.yml)
[![FINOS - Graduated](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-graduated.svg)](https://community.finos.org/docs/governance/lifecycle-stages/graduated)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.finos.symphony.bdk/symphony-bdk-bom/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.finos.symphony.bdk/symphony-bdk-bom)
[![javadoc](https://javadoc.io/badge2/org.finos.symphony.bdk/symphony-bdk-core/javadoc.svg)](https://javadoc.io/doc/org.finos.symphony.bdk/symphony-bdk-core)

# Symphony BDK for Java

> [!NOTE]
> BDK version 4.0: Major change !
>
> The newly introduced BDK version 4.0 relies on Java 25 and Spring Boot 4. This is a major change that allows Symphony to continue to propose the latest security fixes following the end of support of Spring Boot 3 and also to keep up with the latest evolutions of Java.
>
> For the next 6 months Symphony will provide critical security fixes for BDK 3.x where possible (since Spring gives no guarantees for their packages).
>
> Please consider migrating your Bots in the coming months to benefit from the latest features and support.
> See the [4.x Migration Guide](https://symphony-bdk-java.finos.org/migration-4.x.html) for details.

> [!IMPORTANT]
> Spring Boot 3.x + BDK 4.x is not supported. If your application cannot move to Spring Boot 4 and Java 25 yet, stay
> on BDK 3.x — its documentation and sources remain available on the [`3.x`](https://github.com/finos/symphony-bdk-java/tree/3.x) branch.

The **official** Symphony BDK for Java helps you to create production-grade Chat Bots and Extension Applications on 
top of the [Symphony REST APIs](https://developers.symphony.com/restapi/reference). 

## Installation and Getting Started
The [reference documentation](http://symphony-bdk-java.finos.org) includes detailed installation instructions as well as a comprehensive 
[getting started](http://symphony-bdk-java.finos.org/getting-started.html) guide.

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

## Command-Line Interface
The [`symphony-bdk-cli`](./symphony-bdk-cli) module provides an operational `bdk` command-line
interface that authenticates as a configured bot and performs one-shot Symphony operations
(messaging, streams, users, datafeed, health) straight from the shell, emitting JSON on `stdout`
for easy scripting and agent consumption.

```bash
./gradlew :symphony-bdk-cli:installDist
ln -s "$(pwd)/symphony-bdk-cli/build/install/symphony-bdk-cli/bin/bdk" /usr/local/bin/bdk
bdk whoami
```

See the [CLI documentation](./docs/cli.md) for the full command reference and the JSON / exit-code
contract.

## Build from Source
The Symphony BDK uses a [Gradle](https://docs.gradle.org/) build. The instructions below use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
from the root of the source tree. The wrapper script serves as a cross-platform, self-contained bootstrap mechanism for
the build system.

### Before you start
To build you will need [Git](https://docs.github.com/en/github/getting-started-with-github/set-up-git) and [JDK 17 or later](https://adoptium.net/).
Be sure that your `JAVA_HOME` environment variable points to the JDK 17+ folder extracted from the JDK download.

> This is the requirement to build from source on `main` today. The BDK 4.0.0 release itself will require Java 25
> (see the [4.x Migration Guide](https://symphony-bdk-java.finos.org/migration-4.x.html)) — `main` has not yet moved
> its build toolchain to 25.

### Build from the Command Line
To compile, test and build all BDK jars, use:
```shell script
./gradlew
```
To compile, test and build BDK 3.x jars:
```shell script
git checkout 3.x
./gradlew
```
To compile, test and build legacy jars:
```shell script
git checkout legacy
./gradlew
```
> Legacy modules (SDK and BDK 1.0) have been moved to the [legacy](https://github.com/finos/symphony-bdk-java/tree/legacy) branch
### Install in local Maven repository
To install all Symphony BDK jars in your local Maven repository, use:
```shell script
./gradlew publishToMavenLocal
```

## Contributing
In order to get in touch with the project team, please open a [GitHub Issue](https://github.com/finos/symphony-bdk-java/issues).
Alternatively, you can email/subscribe to [symphony@finos.org](https://groups.google.com/a/finos.org/g/symphony).


1. Fork it (<https://github.com/finos/symphony-bdk-java/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Read our [contribution guidelines](.github/CONTRIBUTING.md) and [Community Code of Conduct](https://www.finos.org/code-of-conduct)
4. Commit your changes (`git commit -am 'Add some fooBar'`)
5. Push to the branch (`git push origin feature/fooBar`)
6. Create a new Pull Request

_NOTE:_ Commits and pull requests to FINOS repositories will only be accepted from those contributors with an active, executed Individual Contributor License Agreement (ICLA) with FINOS OR who are covered under an existing and active Corporate Contribution License Agreement (CCLA) executed with FINOS. Commits from individuals not covered under an ICLA or CCLA will be flagged and blocked by the FINOS Clabot tool. Please note that some CCLAs require individuals/employees to be explicitly named on the CCLA.

*Need an ICLA? Unsure if you are covered under an existing CCLA? Email [help@finos.org](mailto:help@finos.org)*

### Thanks to all the people who have contributed
[![contributors](https://contributors-img.web.app/image?repo=finos/symphony-bdk-java)](https://github.com/finos/symphony-bdk-java/graphs/contributors)

## License
Copyright 2021 Symphony LLC

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)