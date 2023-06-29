import org.scalatest.BeforeAndAfterEach
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers._

import java.io.File
import scala.annotation.tailrec

class AvscSchemaToJsonTest extends AnyFeatureSpec with should.Matchers with BeforeAndAfterEach {

  override def afterEach(): Unit = {
    val output: File = new File("./src/test/resources/outputs")

    if (output.exists()) deleteOutputs(input = output)

    @tailrec
    def deleteOutputs(dirs: Array[File] = Array[File](), input: File): Unit = {

      val dirList: Array[File] = Option(input.listFiles())
        .fold(Array[File]())(d => d) :+ input

      val remainingDir = dirList.filter(_.delete() == false)
      val newDirs = remainingDir ++ dirs

      newDirs.length match {
        case l: Int if l > 0 => deleteOutputs(newDirs.tail, newDirs.head)
        case _ =>
      }
    }
  }

  Feature("Nominal case") {
    Scenario("Convert 2 avro schema to json schema") {
      AvscSchemaToJson.main(Array("./src/test/resources/inputs", "./src/test/resources/outputs/json"))

      val expectedFile_1 = new File("./src/test/resources/outputs/json/Example/Example.json")
      val expectedFile_2 = new File("./src/test/resources/outputs/json/Example/Example_1.json")
      val expectedFile_3 = new File("./src/test/resources/outputs/json/Example_2/Example_2.json")

      assert(expectedFile_1.exists())
      assert(expectedFile_2.exists())
      assert(expectedFile_3.exists())
    }
  }
  Feature("Exception cases") {

    Scenario("Sending less than 2 parameters should raise an exeption") {
      val thrown = intercept[IllegalArgumentException] {
        AvscSchemaToJson.main(Array())
      }
      assert(thrown.getMessage === "requirement failed: Define input and output dirs")
    }

    Scenario("Sending empty parameters should raise an exeption") {
      val thrown = intercept[IllegalArgumentException] {
        AvscSchemaToJson.main(Array("", ""))
      }
      assert(thrown.getMessage === "requirement failed: Define non empty input and output dirs")
    }
  }
}