# symphony-bdk-cli

An operational command-line interface (`bdk`) for the Symphony BDK. It authenticates as a
configured bot and performs one-shot Symphony operations, emitting JSON on `stdout`.

## Install (`bin/bdk` onto `PATH`)

```bash
# build the launcher + runtime jars
./gradlew :symphony-bdk-cli:installDist

# the launcher is produced at:
#   symphony-bdk-cli/build/install/symphony-bdk-cli/bin/bdk

# symlink it onto your PATH
ln -s "$(pwd)/symphony-bdk-cli/build/install/symphony-bdk-cli/bin/bdk" /usr/local/bin/bdk

bdk --help
```

By default the CLI reads `~/.symphony/config.yaml` (override with `-c/--config`).

See [`docs/cli.md`](../docs/cli.md) for the full command reference, configuration and the
JSON / exit-code contract.
