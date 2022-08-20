package repositories

import models.Subject
import org.joda.time.DateTime
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json.compat.json2bson._

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 */
class SubjectRepository @Inject()(implicit ec: ExecutionContext, api: ReactiveMongoApi) extends MongoRepository {
  val collectionName = "subjects"

  def collection: Future[BSONCollection] = api.database.map(db => db.collection(collectionName))

  def findAll(limit: Int = 100): Future[Seq[Subject]] =
    collection.flatMap(_.find(BSONDocument(), Option.empty[Subject])
      .cursor[Subject](ReadPreference.Primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[Subject]]()))

  def findOne(id: UUID): Future[Option[Subject]] =
    collection.flatMap(_.find(queryBy(id), Option.empty[Subject]).one[Subject])

  def create(subject: Subject): Future[WriteResult] =
    collection.flatMap(_.insert(ordered = false)
    .one(subject.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime()))))

  def update(id: UUID, subject: Subject): Future[WriteResult] =
    collection.flatMap(_.update(ordered = false).one(queryBy(id),
      subject.copy(_updateDate = Some(new DateTime())))
    )

  def delete(id: UUID): Future[WriteResult] = {
    collection.flatMap(_.delete().one(queryBy(id), Some(1)))
  }
}
