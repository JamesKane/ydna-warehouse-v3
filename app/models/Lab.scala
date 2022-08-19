package models

import play.api.libs.json._

import models.TestType.TestType

object TestType extends Enumeration {
  type TestType = Value

  val Chip = Value(1, "Chip")
  val Sanger = Value(2, "STR or SNP Panel")
  val BigY5 = Value(5, "Big Y-500")
  val BigY7 = Value(7, "Big Y-700")
  val BigYW = Value(20, "Big Y-W57")
  val YElite = Value(7, "Y Elite 1")
  val YElite2 = Value(8, "Y Elite 2")
  val YElite2_1 = Value(9, "Y Elite 2.1")
  val WGS = Value(10, "WGS")
  val WXS = Value(11, "WXS")
  val ONT = Value(12, "ONT")
  val WGS_LR = Value(13, "Chromium-LR")
  val PacBio = Value(14, "PacBio")

  // TODO: There are a couple arrays used in ENA that should be defined here
}

case class LabTest(
                    testType: TestType,
                    retired: Boolean
                  )

case class Lab(
                _id: Int,
                name: String,
                url: String,
                info: String,
                testsOffered: List[LabTest] = List()
              )

// ===================== JSON and BSON Document Binding Boiler Plate =====================

object LabTest {
  implicit object LabTestWrites extends OWrites[LabTest] {
    override def writes(o: LabTest): JsObject = Json.obj(
      "testType" -> o.testType.id,
      "retired" -> o.retired
    )
  }

  implicit object LabTestReads extends Reads[LabTest] {
    override def reads(json: JsValue): JsResult[LabTest] = json match {
      case obj: JsObject => try {
        JsSuccess(
          LabTest(
            TestType.apply((obj \ "date").as[Int]),
            (obj \ "retired").as[Boolean]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected.jsobject")
    }
  }
}

object Lab {
  implicit object LabWrites extends OWrites[Lab] {
    override def writes(o: Lab): JsObject = Json.obj(
      "_id" -> o._id,
      "name" -> o.name,
      "info" -> o.info,
      "url" -> o.url,
      "testsOffered" -> o.testsOffered
    )
  }
}