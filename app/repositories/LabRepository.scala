package repositories

import models.Lab
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json.compat._
import reactivemongo.play.json.compat.json2bson._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 */
class LabRepository @Inject()(implicit ec: ExecutionContext, api: ReactiveMongoApi) extends MongoRepository {

  def collection: Future[BSONCollection] = api.database.map(db => db.collection("labs"))

  def findAll(limit: Int = 100): Future[Seq[Lab]] =
    collection.flatMap(_.find(BSONDocument(), Option.empty[Lab])
    .cursor[Lab](ReadPreference.Primary)
    collect[Seq](limit, Cursor.FailOnError[Seq[Lab]]()))

  def findOne(id: BSONObjectID): Future[Option[Lab]] =
    collection.flatMap(_.find(queryBy(id), Option.empty[Lab]).one[Lab])

  def create(lab: Lab): Future[WriteResult] =
    collection.flatMap(_.insert(ordered = false).one(lab))

  def update(id: BSONObjectID, lab: Lab): Future[WriteResult] =
    collection.flatMap(_.update(ordered = false).one(queryBy(id), lab))

  def delete(id: BSONObjectID): Future[WriteResult] =
    collection.flatMap(_.delete().one(queryBy(id), Some(1)))
}
