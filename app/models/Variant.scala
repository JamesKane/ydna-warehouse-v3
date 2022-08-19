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
 * @param aka          Name from the Paper or Repository
 * @param investigator Reference credit name
 * @param year         Year published or discovered
 */
case class VariantReference(
                             aka: String,
                             investigator: Option[String],
                             year: Option[Int]
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
 * @param _id           Mongo DB Identifier
 * @param _creationDate Date the document was created
 * @param _updateDate   Date the document was last updated
 * @param name          The accepted name from the list of aliases for reporting
 * @param vType         The type of variation from the reference sequence
 * @param refs          The credits for use of the variant published or sourced
 * @param definition    The position and state changes of the variant on one or more accessions
 * @param subject       The consensus call status for a Subject from all testing
 * @param comment       Any additional notes about the variant
 */
case class Variant(
                    _id: Option[BSONObjectID],
                    _creationDate: Option[DateTime],
                    _updateDate: Option[DateTime],
                    name: String,
                    vType: VariantType,
                    refs: Set[VariantReference] = Set(),
                    definition: Set[VariantDefinition],
                    subject: Set[SubjectStatus] = Set(),
                    comment: Option[String]
                  )

// ===================== JSON and BSON Document Binding Boiler Plate =====================

object VariantReference {
  implicit val fmt: Format[VariantReference] = Json.format[VariantReference]

  implicit object VariantRefBSONReader extends BSONDocumentReader[VariantReference] {
    override def read(bson: BSONDocument): VariantReference = {
      VariantReference(
        bson.getAs[String]("aka").get,
        bson.getAs[String]("investigator"),
        bson.getAs[Int]("year")
      )
    }
  }

  implicit object VariantRefBSONWriter extends BSONDocumentWriter[VariantReference] {
    override def write(t: VariantReference): BSONDocument = {
      BSONDocument(
        "aka" -> t.aka,
        "investigator" -> t.investigator,
        "year" -> t.year
      )
    }
  }
}

object VariantDefinition {
  implicit val fmt: Format[VariantDefinition] = Json.format[VariantDefinition]

  implicit object VariantDefBSONReader extends BSONDocumentReader[VariantDefinition] {
    override def read(bson: BSONDocument): VariantDefinition = {
      VariantDefinition(
        bson.getAs[Int]("accession").map(v => AccessionType.apply(v)).get,
        bson.getAs[Int]("start").get,
        bson.getAs[Int]("stop").get,
        bson.getAs[String]("anc").get,
        bson.getAs[String]("der").get,
        bson.getAs[Boolean]("orient").get
      )
    }
  }

  implicit object VariantDefBSONWriter extends BSONDocumentWriter[VariantDefinition] {
    override def write(t: VariantDefinition): BSONDocument = {
      BSONDocument(
        "accession" -> t.accession.id,
        "start" -> t.start,
        "stop" -> t.stop,
        "anc" -> t.anc,
        "der" -> t.der,
        "orient" -> t.orient
      )
    }
  }
}

object SubjectStatus {
  implicit val fmt: Format[SubjectStatus] = Json.format[SubjectStatus]

  implicit object SubjectStatusBSONReader extends BSONDocumentReader[SubjectStatus] {
    override def read(bson: BSONDocument): SubjectStatus = {
      SubjectStatus(
        bson.getAs[BSONObjectID]("subjectID").get,
        bson.getAs[String]("status")
      )
    }
  }

  implicit object SubjectStatusBSONWriter extends BSONDocumentWriter[SubjectStatus] {
    override def write(t: SubjectStatus): BSONDocument = {
      BSONDocument(
        "subjectId" -> t.subjectID,
        "status" -> t.status
      )
    }
  }
}
object Variant {
  implicit val fmt: Format[Variant] = Json.format[Variant]

  implicit object VariantBSONReader extends BSONDocumentReader[Variant] {
    override def read(bson: BSONDocument): Variant = {
      Variant(
        bson.getAs[BSONObjectID]("_id"),
        bson.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
        bson.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
        bson.getAs[String]("name").get,
        bson.getAs[Int]("vType").map(t => VariantType.apply(t)).get,
        bson.getAs[Set[VariantReference]]("refs").get,
        bson.getAs[Set[VariantDefinition]]("definition").get,
        bson.getAs[Set[SubjectStatus]]("subject").get,
        bson.getAs[String]("comment")
      )
    }
  }

  implicit object VariantBSONWriter extends BSONDocumentWriter[Variant] {
    override def write(t: Variant): BSONDocument = {
      BSONDocument(
        "_id" -> t._id,
        "_creationDate" -> t._creationDate.map(date => BSONDateTime(date.getMillis)),
        "_updateDate" -> t._updateDate.map(date => BSONDateTime(date.getMillis)),
        "name" -> t.name,
        "vType" -> t.vType.id,
        "refs" -> t.refs,
        "definition" -> t.definition,
        "subject" -> t.subject,
        "comment" -> t.comment
      )
    }
  }
}