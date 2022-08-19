package repositories

import models.Kit
import org.joda.time.DateTime
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json.compat._
import reactivemongo.play.json.compat.json2bson._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class KitRepository @Inject()(implicit ec: ExecutionContext, api: ReactiveMongoApi) extends MongoRepository {
  def collection: Future[BSONCollection] = api.database.map(db => db.collection("kits"))

  def findAll(limit: Int = 100): Future[Seq[Kit]] =
    collection.flatMap(_.find(BSONDocument(), Option.empty[Kit])
      .cursor[Kit](ReadPreference.Primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[Kit]]()))

  def findOne(id: BSONObjectID): Future[Option[Kit]] =
    collection.flatMap(_.find(queryBy(id), Option.empty[Kit]).one[Kit])

  def create(kit: Kit): Future[WriteResult] =
    collection.flatMap(_.insert(ordered = false)
      .one(kit.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime()))))

  def update(id: BSONObjectID, kit: Kit): Future[WriteResult] =
    collection.flatMap(_.update(ordered = false).one(queryBy(id),
      kit.copy(_updateDate = Some(new DateTime())))
    )

  def delete(id: BSONObjectID): Future[WriteResult] = {
    collection.flatMap(_.delete().one(queryBy(id), Some(1)))
  }
}
