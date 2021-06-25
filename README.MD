# [< Hex.log >](https://hexlog.dev)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/BartoszBlaszczak/hexlog)
[![Kotlin](https://img.shields.io/badge/kotlin-1.5.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Website](https://img.shields.io/website?label=hexlog.dev&url=https%3A%2F%2Fhexlog.dev%2Fhealth)](https://hexlog.dev)

![](src/main/resources/web/static/icons/favicon.ico)

This is JVM application that [hexlog.dev](https://hexlog.dev) blog runs on.

It is written in kotlin and uses Ktor as a web server and SQLite database.

Hex.log uses gradle 7+ to build and java 8+ to run. 

Run `./gradlew build` to generate executable jar file.

Build reports can be found in `build/reports/`.

Artifact should be located in `build/libs/hexlog-[version].jar.`

Before starting, ensure existence of SQLite database in database.sqlite file and run [sql script](db.sql).

To start this application locally run `java -jar ApplicationKt` with args:
* useSSL=[false | true, default is 'true']
* httpPort=[The port to listen over http, default is '8060']
* httpsPort=[The port to listen over https. Used when 'useSSL=true', Default is '8086']
* externalHttpsPort=[The port where this application is available behind a server, default is '443']
* keystoreType=[default is 'JKS']
* keystorePath=[default is 'keystore.jks']
* certAlias=[default is 'hexlog-cert']
* keystorePassword=[default is JKS]
* dbURL=[URL to SQLite database, default is 'jdbc:sqlite:database.sqlite']
* hexlog_profiles=[Profiles in which this application can run. For now, the 'demo' profile is supported. The default is blank.]
* address=[http address, for example http://localhost:8060]

E.g. `java -jar build/libs/hexlog-1.0.0.jar useSSL=false hexlog_profiles=demo address=http://localhost:8060` 
will run hex.log without SSL and with 'demo' profile that do not use sql database but shows static posts.

Hex.log will be available at http://localhost:[httpPort]

### Enjoy & Code Smart! :)