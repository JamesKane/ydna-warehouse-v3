package models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Subject(
                    _id: Option[BSONObjectID],
                    _creationDate: Option[DateTime],
                    _updateDate: Option[DateTime],
                    displayName: String
                  )

object Subject {
  implicit val fmt: Format[Subject] = Json.format[Subject]

  implicit object SubjectBSONReader extends BSONDocumentReader[Subject] {
    def read(doc: BSONDocument): Subject = {
      Subject(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("displayName").get
      )
    }
  }

  implicit object SubjectBSONWriter extends BSONDocumentWriter[Subject] {
    def write(subject: Subject): BSONDocument = {
      BSONDocument(
        "_id" -> subject._id,
        "_creationDate" -> subject._creationDate.map(date => BSONDateTime(date.getMillis)),
        "_updateDate" -> subject._updateDate.map(date => BSONDateTime(date.getMillis)),
        "displayName" -> subject.displayName
      )
    }
  }
}