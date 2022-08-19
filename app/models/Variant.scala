package models

import models.AccessionType.AccessionType
import models.VariantType.VariantType
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

/**
 * Enumeration of the supported variant types
 */
object VariantType extends Enumeration {
  type VariantType = Value

  val SNP = Value(0, "SNP")
  val INS = Value(1, "INS")
  val DEL = Value(2, "DEL")
  val MNP = Value(3, "MNP")
  val STR = Value(4, "STR")
}

/**
 * Enumeration of the supported accessions
 */
object AccessionType extends Enumeration {
  type AccessionType = Value

  val ChrY37 = Value(37, "Y(37)")
  val ChrY = Value(38, "Y(38)")
  val CP086569_1 = Value(40, "CP086569.1")
  val CP086569_2 = Value(41, "CP086569.2")
  val CM034974_1 = Value(50, "CM034974.1")
}

/**
 * Named references to the definition(s) of this variant
 *
 * @param aka           Name from the Paper or Repository
 * @param investigator  Reference credit name
 * @param year          Year published or discovered
 */
case class VariantReference(
                             aka: String,
                             investigator: Option[String],
                             year: Option[Short]
                           )

/**
 * The definition of the variation within context of the accession
 *
 * @param accession - Accession reference
 * @param start     - Start position
 * @param stop      - Stop position
 * @param anc       - Ancestral allele state (may not match reference)
 * @param der       - Derived allele state via ASR (may match the reference)
 * @param orient    - Strand orientation
 */
case class VariantDefinition(
                              accession: AccessionType,
                              start: Int,
                              stop: Int,
                              anc: String,
                              der: String,
                              orient: Boolean
                            )

/**
 * The container for the sparse matrix of calls for this variant
 * Links the Subject's ID with the called final allele value
 *
 * @param subjectID ID of the Subject for the call
 * @param status    the consensus allele value
 */
case class SubjectStatus(subjectID: BSONObjectID, status: Option[String])

/**
 * A known point of variation from the reference sequence and matrix of calls
 * for test Subjects
 *
 * @param _id               Mongo DB Identifier
 * @param _creationDate     Date the document was created
 * @param _updateDate       Date the document was last updated
 * @param name              The accepted name from the list of aliases for reporting
 * @param vType             The type of variation from the reference sequence
 * @param refs              The credits for use of the variant published or sourced
 * @param definition        The position and state changes of the variant on one or more accessions
 * @param subject           The consensus call status for a Subject from all testing
 * @param comment           Any additional notes about the variant
 */
case class Variant(
                    _id: Option[BSONObjectID],
                    _creationDate: Option[DateTime],
                    _updateDate: Option[DateTime],
                    name: String,
                    vType: VariantType,
                    refs: Set[VariantReference] = Set(),
                    definition: VariantDefinition,
                    subject: Set[SubjectStatus] = Set(),
                    comment: Option[String]
                  )




