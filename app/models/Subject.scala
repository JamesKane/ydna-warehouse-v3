package models

import models.EventType.EventType
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import reactivemongo.api.bson.GeoPoint

object EventType extends Enumeration {
  type EventType = Value

  val BIRTH = Value(1)
  val DEATH = Value(2)
}

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
 * @param given       The Given Name
 * @param surname     The Family Name
 * @param lifeEvents  Map of Life Events
 * @param info        Free-form additional details
 */
case class MdkaInfo(
                     given: Option[String] = None,
                     surname: Option[String] = None,
                     lifeEvents: Map[EventType, LifeEvent] = Map(),
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

object LifeEvent {
  implicit val fmt: Format[LifeEvent] = Json.format[LifeEvent]

  implicit object LifeEventBSONReader extends BSONDocumentReader[LifeEvent] {
    override def read(bson: BSONDocument): LifeEvent = ???
  }

  implicit object LifeEventBSONWriter extends BSONDocumentWriter[LifeEvent] {
    override def write(t: LifeEvent): BSONDocument = ???
  }
}

object MdkaInfo {
  implicit val fmt: Format[MdkaInfo] = Json.format[MdkaInfo]

  implicit object MdkaInfoBSONReader extends BSONDocumentReader[MdkaInfo] {
    override def read(bson: BSONDocument): MdkaInfo = ???
  }

  implicit object MdkaInfoBSONWriter extends BSONDocumentWriter[MdkaInfo] {
    override def write(t: MdkaInfo): BSONDocument = ???
  }
}

object Subject {
  implicit val fmt: Format[Subject] = Json.format[Subject]

  implicit object SubjectBSONReader extends BSONDocumentReader[Subject] {
    def read(doc: BSONDocument): Subject = {
      Subject(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("displayName").get,
        doc.getAs[String]("taxaID").get,
        doc.getAs[MdkaInfo]("paternalMdka"),
        doc.getAs[BSONObjectID]("ySubclade"),
        doc.getAs[MdkaInfo]("maternalMdka"),
        doc.getAs[BSONObjectID]("mtSubclade")
      )
    }
  }

  implicit object SubjectBSONWriter extends BSONDocumentWriter[Subject] {
    def write(subject: Subject): BSONDocument = {
      BSONDocument(
        "_id" -> subject._id,
        "_creationDate" -> subject._creationDate.map(date => BSONDateTime(date.getMillis)),
        "_updateDate" -> subject._updateDate.map(date => BSONDateTime(date.getMillis)),
        "displayName" -> subject.displayName,
        "taxaID" -> subject.taxaID,
        "paternalMdka" -> subject.paternalMdka,
        "ySubclade" -> subject.ySubclade,
        "maternalMdka" -> subject.maternalMdka,
        "mtSubclade" -> subject.mtSubclade
      )
    }
  }
}