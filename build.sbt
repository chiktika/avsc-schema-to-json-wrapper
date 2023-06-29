ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.11"

enablePlugins(
    JavaAppPackaging,
    DockerPlugin
)

lazy val root = (project in file("."))
  .settings(
    name := "avsc_schema_to_json",
    libraryDependencies += "com.github.fge" % "json-schema-avro" % "0.1.4",
    libraryDependencies += "com.github.fge" % "jackson-coreutils" % "1.8",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    assembly / test := (Test / test).value,
  )

Compile / mainClass := Some("AvscSchemaToJson")
assembly / mainClass := Some("AvscSchemaToJson")
assembly / assemblyJarName := "AvscSchemaToJson.jar"
packageName := "chiktikacontrib/avsc-schema-to-json"
dockerBaseImage := "openjdk:17-jdk-slim"
dockerUpdateLatest := true
