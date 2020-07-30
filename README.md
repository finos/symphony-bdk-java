# Symphony Java BDK [![CircleCI](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java.svg?style=shield)](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-api-client-java) [![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](https://opensource.org/licenses/MIT)  [![Email](https://img.shields.io/static/v1?label=contact&message=email&color=darkgoldenrod)](mailto:platformsolutions@symphony.com?subject=Java%20SDK)

The Symphony Java BDK helps you to create Bots and Applications on top of the Symphony REST APIs. 

Documentation about this BDK features is available under [docs](./docs/index.md) folder.

## How to Build

As this project contains modules for the legacy SDK/BDK as well as for the 2.0 ones, some
Maven are defined to make the build faster depending on which version you are working on.

The BDK can be built and published to your local Maven cache using the [Maven Wrapper](https://github.com/takari/maven-wrapper). 

**Build Legacy Modules**

The `legacy` profile is activated by default so there is no specific argument to define to have
it part of the build. However, it is also possible to skip legacy modules to be built using argument `-P -legacy`:

```shell script
# build the legacy modules
./mvnw clean install

# skip building legacy modules
./mvnw clean install -P -legacy
```

**Build BDK 2.0 Modules**

Still in construction, the 2.0 modules are deactivated by default but can be activated through the Maven profile `2.0` : 

```shell script
# build the 2.0 modules only, skip the legacy ones
./mvnw clean install -P2.0,-legacy
```

