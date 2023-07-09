ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.11"

ThisBuild / versionScheme := Some("early-semver")

enablePlugins(
    JavaAppPackaging,
    DockerPlugin
)

lazy val root = (project in file("."))
  .settings(
    name := "avsc-schema-to-json-wrapper",
    libraryDependencies += "com.github.fge" % "json-schema-avro" % "0.1.4",
    libraryDependencies += "com.github.fge" % "jackson-coreutils" % "1.8",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    assembly / test := (Test / test).value
  )

Compile / mainClass := Some("AvscSchemaToJson")
assembly / mainClass := Some("AvscSchemaToJson")
assembly / assemblyJarName := "AvscSchemaToJson.jar"
packageName := "chiktikacontrib/avsc-schema-to-json"
dockerBaseImage := "openjdk:17-jdk-slim"
dockerUpdateLatest := true

// publish to github packages settings
publishTo := Some("GitHub chiktika Apache Maven Packages" at s"https://maven.pkg.github.com/chiktika/${name.value}")
publishMavenStyle := true
credentials += Credentials(
    "GitHub Package Registry",
    "maven.pkg.github.com",
    "chiktika",
    System.getenv("GITHUB_TOKEN")
)