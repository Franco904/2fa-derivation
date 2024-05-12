plugins {
    kotlin("jvm") version "1.9.23"
}

version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Security
    implementation("org.bouncycastle:bc-fips:1.0.2.5")

    // Encoding
    implementation("commons-codec:commons-codec:1.16.1")

    // TOTP
    implementation("de.taimos:totp:1.0")

    // QR Code
    implementation("com.google.zxing:javase:3.5.3")
    implementation("com.github.auties00:qr-terminal:2.1")
}

tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to "MainKt")
    }

    from(configurations.compileClasspath.map { config ->
        config.map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
}
