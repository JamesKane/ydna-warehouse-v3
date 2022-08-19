package models

import models.EventType.EventType
import org.joda.time.DateTime
import play.api.libs.json._
import reactivemongo.api.bson._

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
 * @param givenName       The Given Name
 * @param surname     The Family Name
 * @param lifeEvents  Map of Life Events
 * @param info        Free-form additional details
 */
case class MdkaInfo(
                     givenName: Option[String] = None,
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

// ===================== JSON and BSON Document Binding Boiler Plate =====================

object LifeEvent {
  implicit object LifeEventWrites extends OWrites[LifeEvent] {
    override def writes(o: LifeEvent): JsObject = ???
  }

  implicit object LifeEventReads extends Reads[LifeEvent] {
    override def reads(json: JsValue): JsResult[LifeEvent] = ???
  }
}

object MdkaInfo {
  implicit object MdkaInfoWrites extends OWrites[MdkaInfo] {
    override def writes(o: MdkaInfo): JsObject = ???
  }

  implicit object MdkaInfoReads extends Reads[MdkaInfo] {
    override def reads(json: JsValue): JsResult[MdkaInfo] = ???
  }
}

object Subject {
  implicit object SubjectWrites extends OWrites[Subject] {
    override def writes(o: Subject): JsObject = ???
  }

  implicit object SubjectReads extends Reads[Subject] {
    override def reads(json: JsValue): JsResult[Subject] = ???
  }
}