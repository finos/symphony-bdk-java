# How to contribute

If you found a bug or issue, please ensure the bug was not already reported by searching in
[GitHub issues](https://github.com/SymphonyPlatformSolutions/symphony-api-client-java/issues).
If you are unable to find an open issue addressing the problem, open a new one. 
Be sure to include a title and clear description, the SDK version, and a code sample demonstrating the issue.

If you open a PR to fix any issue, please reference the ticket in the PR title.
A [Symphony SDK team](https://github.com/orgs/SymphonyPlatformSolutions/teams/symphony-sdk/members) member
will have to approve before it is merged and eventually released.

If you want to request an enhancement or feature, please open a Github issue if none has been opened before.
New feature requests on the legacy SDK will not be accepted.

## Module and package structure

This repository contains both the legacy Java SDK under the [symphony-bdk-legacy module](symphony-bdk-legacy) 
and the BDK2.0 under all other root modules.
Please check the [legacy module readme file](symphony-bdk-legacy/README.md) and the 
[BDK architecture](docs/tech/architecture.md) documentation for more information.

## Testing

Unit tests should be added or updated each time a PR is submitted. Line code coverage is enforced by Jacoco to 90%.

## Coding guidelines

Coding guidelines are enforced by an [.editorconfig file](.editorconfig).
These should be followed on any code update.

## Documentation

Public classes and methods should be properly documented using javadoc. Main features should be documented using 
[Markdown](https://daringfireball.net/projects/markdown/) under the [docs folder](docs)
and exemplified with compilable code under the [symphony-bdk-examples module](symphony-bdk-examples).
