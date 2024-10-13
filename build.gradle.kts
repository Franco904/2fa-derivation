plugins {
    kotlin("jvm") version "1.9.23"
    id("application")
}

version = "1.0"

application {
    mainClass = "MainKt"
}

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
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
