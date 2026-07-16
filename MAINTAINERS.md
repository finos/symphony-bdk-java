# Maintainers

This file lists the maintainers of this repository.

## Current maintainers

| GitHub Username | Name | Organization | Email |
|----------------|------|--------------|-------|
| @FabienVSymphony | Fabien Vicente | Symphony | *please add email* |
| @KiranNiranjan | Kiran Niranjan | KiKe | *please add email* |
| @Yannick-Malins | Yannick | @SymphonyOSF | *please add email* |
| @benoit-sy | Benoit Charbonnier | *please add organization* | *please add email* |
| @pierreneu | Pierre Neu | @SymphonyOSF  | *please add email* |
| @sbenmoussati | Salah Benmoussati | *please add organization* | *please add email* |
| @thibauult | Thibault Pensec | @SymphonyOSF  | *please add email* |
| @vladokrsymphony | Vlado Kragujevski | *please add organization* | *please add email* |

For information about maintainer responsibilities and resources, see the [FINOS Maintainers Cheatsheet](https://community.finos.org/docs/finos-maintainers-cheatsheet).

## Cutting a release

Releasing is fully automated — just publish a GitHub Release from `main`:

1. Go to the repository **Releases** page and click **Draft a new release**.
2. Set **Target** to `main`.
3. Create a new tag in the form `vMAJOR.MINOR.PATCH` (e.g. `v3.3.16`).
4. Click **Generate release notes** (auto-categorized from merged PRs).
5. Click **Publish release**.

The `Release` workflow then derives the version from the tag, publishes the signed artifacts to Maven Central, uploads the CLI binaries to the release, and commits the next `-SNAPSHOT` version to `main`. There is no need to create a branch or edit the version in `build.gradle` manually.

## Updating this file

All changes to the maintainer list are managed openly:

- **Submit a Pull Request** to this file for any addition, removal, or update.
- **If your project's governance requires a vote**, document or link to the vote outcome in the PR description or comments.
- This process creates a public audit trail of project leadership over time.

Please email **help@finos.org** whenever this file is updated with a change to maintainership.