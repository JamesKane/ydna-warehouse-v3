package models

import org.joda.time.DateTime
import play.api.libs.json._
import reactivemongo.api.bson._
import reactivemongo.play.json.compat._
import bson2json._

/**
 * LifeEvent
 *
 * Date and place information for a MDKA life event
 *
 * @param date -  Gedcom formatted date
 * @param place - Gedcom formatted place
 * @param point - Geo-point
 */
case class LifeEvent(
                      date: Option[String],
                      place: Option[String],
                      point: Option[GeoPoint]
                    )

/**
 * Mdka Info
 *
 * Information about the most distant known ancestor
 *
 * @param givenName   The Given Name
 * @param surname     The Family Name
 * @param birthInfo   Birth event
 * @param deathInfo   Death event
 * @param info        Free-form additional details
 */
case class MdkaInfo(
                     givenName: Option[String] = None,
                     surname: Option[String] = None,
                     birthInfo: Option[LifeEvent] = None,
                     deathInfo: Option[LifeEvent] = None,
                     info: Option[String] = None
                   )

/**
 * A test subject
 *
 * @param _id             Mongo DB ID
 * @param _creationDate   Date the subject was created
 * @param _updateDate     Last updated
 * @param displayName     The display name
 * @param paternalMdka    The Paternal MDKA information
 * @param ySubclade       The current known Y DNA subclade
 * @param maternalMdka    The Maternal MDKA information
 * @param mtSubclade      The current known mt DNA subclade
 */
case class Subject(
                    _id: Option[BSONObjectID],
                    _creationDate: Option[DateTime],
                    _updateDate: Option[DateTime],
                    displayName: String,
                    taxaID: String,
                    paternalMdka: Option[MdkaInfo],
                    ySubclade: Option[BSONObjectID],
                    maternalMdka: Option[MdkaInfo],
                    mtSubclade: Option[BSONObjectID]
                  )

// ===================== JSON and BSON Document Binding Boiler Plate =====================

object LifeEvent {
  implicit object LifeEventWrites extends OWrites[LifeEvent] {
    override def writes(o: LifeEvent): JsObject = Json.obj(
      "date" -> o.date,
      "place" -> o.place,
      "point" -> o.point
    )
  }

  implicit object LifeEventReads extends Reads[LifeEvent] {
    override def reads(json: JsValue): JsResult[LifeEvent] = json match {
      case obj: JsObject => try {
        JsSuccess(
          LifeEvent(
            (obj \ "date").asOpt[String],
            (obj \ "place").asOpt[String],
            (obj \ "point").asOpt[GeoPoint]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected.jsobject")
    }
  }
}

object MdkaInfo {
  implicit object MdkaInfoWrites extends OWrites[MdkaInfo] {
    override def writes(o: MdkaInfo): JsObject = Json.obj(
      "givenName" -> o.givenName,
      "surname" -> o.surname,
      "birthInfo" -> o.birthInfo,
      "deathInfo" -> o.deathInfo,
      "info" -> o.info
    )
  }

  implicit object MdkaInfoReads extends Reads[MdkaInfo] {
    override def reads(json: JsValue): JsResult[MdkaInfo] = json match {
      case obj: JsObject => try {
        val maybeInfo = (obj \ "info").asOpt[String]
        JsSuccess(
          MdkaInfo(
            givenName = (obj \ "givenName").asOpt[String],
            surname = (obj \ "surname").asOpt[String],
            birthInfo = (obj \ "birthInfo").asOpt[LifeEvent],
            deathInfo = (obj \ "deathInfo").asOpt[LifeEvent],
            info = maybeInfo
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object Subject {
  implicit object SubjectWrites extends OWrites[Subject] {
    override def writes(o: Subject): JsObject = Json.obj(
      "_id" -> o._id,
      "_creationDate" -> o._creationDate.fold(-1L)(_.getMillis),
      "_updateDate" -> o._updateDate.fold(-1L)(_.getMillis),
      "displayName" -> o.displayName,
      "taxaID" -> o.taxaID,
      "paternalMdka" -> o.paternalMdka,
      "ySubclade" -> o.ySubclade,
      "maternalMdka" -> o.maternalMdka,
      "mtSubclade" -> o.mtSubclade
    )
  }

  implicit object SubjectReads extends Reads[Subject] {
    override def reads(json: JsValue): JsResult[Subject] = json match {
      case obj: JsObject => try {
        JsSuccess(
          Subject(
            (obj \ "_id").asOpt[BSONObjectID],
            (obj \ "_creationDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "_updateDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "displayName").as[String],
            (obj \ "taxaID").as[String],
            (obj \ "paternalMdka").asOpt[MdkaInfo],
            (obj \ "ySubclade").asOpt[BSONObjectID],
            (obj \ "maternalMdka").asOpt[MdkaInfo],
            (obj \ "mtSubclade").asOpt[BSONObjectID]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}