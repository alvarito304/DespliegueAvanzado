plugins {
    id("java")
    id ("jacoco")
}

group = "dev.alvaroherrero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(file("build/jacoco"))
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
tasks.jar {
    archiveFileName.set("my-app.jar")
    manifest {
        attributes["Main-Class"] = "dev.alvaroherrero.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}