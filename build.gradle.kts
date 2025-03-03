plugins { kotlin("jvm") version "2.0.+" }

val targetJavaVersion = 21

kotlin { jvmToolchain(targetJavaVersion) }

repositories {
    mavenCentral()
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    implementation("org.joml:joml:1.10.0")
}