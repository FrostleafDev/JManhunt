# JManhunt
High Performance Manhunt Engine für Minecraft. For Players and Developers.

**Support and Project Discussion:**
- [Discord](https://jozelot.de/discord)

## Features
- Complete setup via a UI or commands
- API support for developers (Addon developing)
- All normal Manhunt Features you need
- Multiple runner and hunter support
- Support for spectators
- Headstart system

## How to (Developing Addons)
- See our API [here](https://github.com/FrostleafDev/JManhunt/tree/master/api)
- Frostleafs API javadocs [here](https://jozelot.de/javadocs)
- JManhunt API javadocs [here](https://jd.jozelot.de/jmanhunt-api/)
- JManhunt API documentation [here](https://docs.jozelot.de/manhunt-api)
- JManhunt API versions [here](https://repo.jozelot.de/#browse/browse:maven-public:de%2Fjozelot%2Fjmanhunt-api)

You can easily create addons for the plugin.
I will provide some addons from my own to show how the api works.
When you publish an addon please send it to my discord so I can review it and advertise it here on the Github and the docs.

**Addons I will create**
- Simple Voice Chat Integration

**Gradle**

```gradle
repositories {
    maven {
        name = "jozelot-repo"
        url = uri("https://repo.jozelot.de/repository/maven-public/")
    }
}

dependencies {
    implementation "de.jozelot:jmanhunt-api:1.0.0-SNAPSHOT"
}
```

**Kotlin DSL**

```kotlin
repositories {
    maven("https://repo.jozelot.de/repository/maven-public/") {
        name = "jozelot-repo"
    }
}

dependencies {
    implementation("de.jozelot:jmanhunt-api:1.0.0-SNAPSHOT")
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
</dependency>
```

## Contribution

The plugin is open source. You can create forks and develop it on your own as long as you don't try to publish it.
