package models

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
