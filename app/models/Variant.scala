package models

import models.AccessionType.AccessionType
import models.VariantType.VariantType
import org.joda.time.DateTime
import play.api.libs.json._
import reactivemongo.api.bson._

import reactivemongo.play.json.compat._
import bson2json._


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
  implicit object VariantReferenceWrites extends OWrites[VariantReference] {
    override def writes(o: VariantReference): JsObject = Json.obj(
      "aka" -> o.aka,
      "investigator" -> o.investigator,
      "year" -> o.year
    )
  }

  implicit object VariantReferenceReads extends Reads[VariantReference] {
    override def reads(json: JsValue): JsResult[VariantReference] = json match {
      case obj: JsObject => try {
        JsSuccess(
          VariantReference(
            (obj \ "aka").as[String],
            (obj \ "investigator").asOpt[String],
            (obj \ "year").asOpt[Int]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected.jsobject")
    }
  }
}

object VariantDefinition {
  implicit object VariantDefinitionWrites extends OWrites[VariantDefinition] {
    override def writes(o: VariantDefinition): JsObject = Json.obj(
      "accession" -> o.accession.id,
      "start" -> o.start,
      "stop" -> o.stop,
      "anc" -> o.anc,
      "der" -> o.der,
      "orient" -> o.orient
    )
  }

  implicit object VariantDefinitionReads extends Reads[VariantDefinition] {
    override def reads(json: JsValue): JsResult[VariantDefinition] = json match {
      case obj: JsObject => try {
        JsSuccess(
          VariantDefinition(
            AccessionType.apply((obj \ "accession").as[Int]),
            (obj \ "start").as[Int],
            (obj \ "stop").as[Int],
            (obj \ "anc").as[String],
            (obj \ "der").as[String],
            (obj \ "anc").as[Boolean]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object SubjectStatus {

  implicit object SubjectStatusWrites extends OWrites[SubjectStatus] {
    override def writes(o: SubjectStatus): JsObject = Json.obj(
      "subjectID" -> o.subjectID,
      "status" -> o.status
    )
  }

  implicit object SubjectStatusReads extends Reads[SubjectStatus] {
    override def reads(json: JsValue): JsResult[SubjectStatus] = json match {
      case obj: JsObject => try {
        JsSuccess(
          SubjectStatus(
            (obj \ "subjectID").as[BSONObjectID],
            (obj \ "status").asOpt[String]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object Variant {
  implicit object VariantWrites extends OWrites[Variant] {
    override def writes(o: Variant): JsObject = Json.obj(
      "_id" -> o._id,
      "_creationDate" -> o._creationDate.fold(-1L)(_.getMillis),
      "_updateDate" -> o._updateDate.fold(-1L)(_.getMillis),
      "name" -> o.name,
      "vType" -> o.vType.id,
      "refs" -> o.refs,
      "definition" -> o.definition,
      "subject" -> o.subject,
      "comment" -> o.comment
    )
  }

  implicit object VariantReads extends Reads[Variant] {
    override def reads(json: JsValue): JsResult[Variant] = json match {
      case obj: JsObject => try {
        JsSuccess(
          Variant(
            (obj \ "_id").asOpt[BSONObjectID],
            (obj \ "_creationDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "_updateDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "name").as[String],
            VariantType.apply((obj \ "vType").as[Int]),
            (obj \ "refs").as[Set[VariantReference]],
            (obj \ "definition").as[Set[VariantDefinition]],
            (obj \ "subject").as[Set[SubjectStatus]],
            (obj \ "comment").asOpt[String]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}