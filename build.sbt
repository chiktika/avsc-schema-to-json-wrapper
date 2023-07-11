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
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.7",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    assembly / test := (Test / test).value
  )

Compile / mainClass := Some("AvscSchemaToJson")
assembly / mainClass := Some("AvscSchemaToJson")
assembly / assemblyJarName := "AvscSchemaToJson.jar"
assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case x if x.endsWith("/module-info.class") => MergeStrategy.discard
    case "application.conf" => new sbtassembly.MergeStrategy {
        val name = "reverseConcat"

        def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] =
            MergeStrategy.concat(tempDir, path, files.reverse)
    }
    case "logback.xml" => MergeStrategy.first
    case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)

}




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