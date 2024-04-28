plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bc-fips:1.0.2.5")
    implementation("commons-codec:commons-codec:1.16.1")
    implementation("de.taimos:totp:1.0")
    implementation("com.google.zxing:javase:3.5.3")
}
