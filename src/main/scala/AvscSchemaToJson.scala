import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.avro.Avro2JsonSchemaProcessor
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.{DevNullProcessingReport, ProcessingReport}
import com.github.fge.jsonschema.core.tree.{JsonTree, SimpleJsonTree}
import com.typesafe.scalalogging.LazyLogging

import java.io.{File, FileWriter}
import java.nio.file.Paths
import scala.annotation.tailrec
import scala.util.Try

object AvscSchemaToJson extends LazyLogging {

  private val avroProcessor: Avro2JsonSchemaProcessor = new Avro2JsonSchemaProcessor()

  def main(args: Array[String]): Unit = {

    require(args.length == 2, "Define input and output dirs")
    require(args.count(_.nonEmpty) == 2, "Define non empty input and output dirs")

    val input: File = new File(args(0))
    val outputRootPath: String = args(1)

    val schemas = getFiles(input = input)
      .filter(_.getName.endsWith(".avsc"))
      .map(
        f => (f, convertToJson(f))
      )

    if (schemas.isEmpty) {
      logger.warn(s"No avsc files found in ${input}")
    }

    for ((file, schema) <- schemas) {
      writeFile(outputRootPath, file, schema.asText())
    }

  }

  @tailrec
  private def getFiles(files: Array[File] = Array[File](), dirs: Array[File] = Array[File](), input: File): Array[File] = {

    val dirList: Array[File] = Option(input.listFiles())
      .fold(Array[File]())(d => d)

    val newFiles = dirList.filter(_.isFile) ++ files
    val newDirs = dirList.filter(_.isDirectory) ++ dirs

    newDirs.length match {
      case l: Int if l == 0 =>
        newFiles
      case _ => getFiles(newFiles, newDirs.tail, newDirs.head)
    }
  }

  private def convertToJson(fileName: File): JsonNode = {
    val source = scala.io.Source.fromFile(fileName)
    val lines = try source.mkString finally source.close()

    val schema: JsonNode = JsonLoader.fromString(lines)
    val tree: JsonTree = new SimpleJsonTree(schema)
    val report: ProcessingReport = new DevNullProcessingReport
    val processor = avroProcessor.rawProcess(report, tree)
    processor.getBaseNode
  }

  private def writeFile(path: String, file: File, content: String): Unit = {
    Try {

      val lastFilePath = Paths.get(file.getCanonicalPath).getParent.toUri.toString.split("/").last
      val filename = file.getName.split("\\.").head

      val outputFile = new File(s"$path/$lastFilePath/$filename.json")
      outputFile.getParentFile.mkdirs

      val fileWriter = new FileWriter(outputFile)
      fileWriter.write(content)
      fileWriter.close()
      outputFile

    }.toEither match {
      case Left(e) =>
        throw new Exception(s"Error while writing file ${file.getPath} : $e")
      case Right(outputFile) => logger.info(s"Convert ${file.getPath} --to--> $outputFile")
    }
  }
}
