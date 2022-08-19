package repositories

import models.Subject
import org.joda.time.DateTime
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubjectRepository @Inject()(implicit ec: ExecutionContext, api: ReactiveMongoApi) {

  def collection: Future[BSONCollection] = api.database.map(db => db.collection("subjects"))

  def findAll(limit: Int = 100): Future[Seq[Subject]] =
    collection.flatMap(_.find(BSONDocument(), Option.empty[Subject])
      .cursor[Subject](ReadPreference.Primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[Subject]]()))

  def findOne(id: BSONObjectID): Future[Option[Subject]] =
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Subject]).one[Subject])

  def create(subject: Subject): Future[WriteResult] =
    collection.flatMap(_.insert(ordered = false)
    .one(subject.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime()))))

  def update(id: BSONObjectID, subject: Subject): Future[WriteResult] =
    collection.flatMap(_.update(ordered = false).one(BSONDocument("_id" -> id),
      subject.copy(_updateDate = Some(new DateTime())))
    )

  def delete(id: BSONObjectID): Future[WriteResult] = {
    collection.flatMap(_.delete().one(BSONDocument("_id" -> id), Some(1)))
  }
}
