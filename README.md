# Wilpam Tweaks

Build with Java 25 or newer:

```sh
./gradlew runDatagen
./gradlew build
```

To create a disposable Exaroton test server, configure your API token, server name, and username in
`exaroton.properties` (see `exaroton.properties.example`), then run:

```sh
./gradlew runExarotonTestServer --console=plain
```
