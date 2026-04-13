# JManhunt ![Version](https://img.shields.io/badge/Version-1.0.0--SNAPSHOT-aqua) ![Java](https://img.shields.io/badge/Java-21-orange) ![Platform](https://img.shields.io/badge/Platform-Paper-blue) [![Discord](https://img.shields.io/discord/YOUR_ID?color=7289da&label=Discord)](https://jozelot.de/discord)


High Performance Manhunt Engine für Minecraft. For Players and Developers.


**Support and Project Discussion:**
- [Discord](https://jozelot.de/discord)

## Features
- Complete setup via a UI or commands
- API support for developers (Addon Development)
- All normal Manhunt Features you need
- Multiple runner and hunter support
- Support for spectators
- Headstart system
- Full plugin- and world-reset for starting a new manhunt

## How to (Developing Addons)
- [See our API](https://github.com/FrostleafDev/JManhunt/tree/master/api)
- [Frostleaf's API javadocs](https://jozelot.de/javadocs)
- [JManhunt API javadocs](https://jd.jozelot.de/jmanhunt-api/)
- [JManhunt API documentation](https://docs.jozelot.de/manhunt-api)
- [JManhunt API versions](https://repo.jozelot.de/#browse/browse:maven-public:de%2Fjozelot%2Fjmanhunt-api)

You can easily create addons for the plugin.
I will provide some addons from my own to show how the api works.
When you publish an addon please send it to my discord so I can review it and advertise it here on the Github and the docs.

**Addons I will create**
- Simple Voice Chat Integration

**Gradle (Kotlin & Groovy)**

```gradle
repositories {
    maven {
        name = "jozelot-repo"
        url = uri("https://repo.jozelot.de/repository/maven-public/")
    }
}

dependencies {
    compileOnly "de.jozelot:jmanhunt-api:1.0.0-SNAPSHOT"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
```

**Maven**

```xml
<repository>
    <id>jozelot-repo</id>
    <url>https://repo.jozelot.de/repository/maven-public/</url>
</repository>
```
```xml
<dependency>
    <groupId>de.jozelot</groupId>
    <artifactId>jmanhunt-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## How To (Compiling From Source)
To compile JManhunt yourself, you need JDK 21 or later and an internet connection<br>
Clone this repo. Then navigate either to the `api` or `plugin` folder and run: `mvn clean package`
You can find the compiled jar in the `/plugin/target` or `api/target` folder.

## Contribution

The plugin is open source. You can create forks and develop it on your own as long as you don't try to publish it.

Proudly maintained by Jozelot & the Frostleaf team.
