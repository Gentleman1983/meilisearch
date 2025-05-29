import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.sonarqube.gradle.SonarExtension
import org.sonarqube.gradle.SonarTask

plugins {
    application
    id("jacoco")
    id("jacoco-report-aggregation")
    id("java-library")
    alias(meilisearchLibs.plugins.kotlin.jvm)
    alias(meilisearchLibs.plugins.sonarqube.plugin)
}

repositories {
    mavenCentral()
}

configure<SonarExtension> {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectKey", "de.havox_design.meilisearch:meilisearch")
        property("sonar.organization", "havox")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.layout.buildDirectory.get().asFile.absolutePath}/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml")
    }
}

dependencies {
    api(project(":aoc-utils"))

    implementation(meilisearchLibs.commons.lang3)
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(meilisearchLibs.equalsverifier)
    testImplementation(meilisearchLibs.hamcrest)
    testImplementation(meilisearchLibs.junit.jupiter)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "de.havox_design.meilisearch.meili.MainClass"
}

jacoco {
    toolVersion = "0.8.13"
}

val jacocoTestReportTask = tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = "de.havox_design.meilisearch.meili.MainClass"
    }
}

if (tasks.findByName("check") == null) {
    tasks.register("check") {
        group = "verification"
        description = "Collector task for check tasks..."
    }
}

tasks.named("check") {
    dependsOn(tasks.named("testCodeCoverageReport"))
    dependsOn(jacocoTestReportTask)
}

tasks.withType<Test> {
    useJUnitPlatform()

    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "failed", "skipped")
    }

    reports {
        junitXml.required = true
        html.required = true
    }
}

tasks.withType<SonarTask> {
    dependsOn(jacocoTestReportTask)
}

// Switch to gradle "all" distribution.
tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.14"
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
    }
}

