plugins {
    java
    `java-library`
}

subprojects {
    apply(from = rootProject.file("common.gradle"))
}

group = "zenlisp"
version = "${properties["ver_major"]}.${properties["ver_minor"]}.${properties["ver_patch"]}"

repositories {
    mavenCentral()
}

dependencies {
    listOf(
            ":ZenCode",
            ":CodeFormatter",
            ":CodeFormatterShared",
            ":JavaIntegration",
            ":JavaAnnotations",
            ":JavaBytecodeCompiler",
            ":JavaShared",
            ":Validator",
            ":CodeModel",
            ":Shared"
    ).forEach { implementation(project(it)) }
    implementation("com.google.guava:guava:30.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.named<Copy>("processResources") {
    dependsOn(":StdLibs:zipItUp")
    from(files(evaluationDependsOn(":StdLibs").tasks.getByName("zipItUp").outputs))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
