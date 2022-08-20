package repositories

import models.Variant
import org.joda.time.DateTime
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json.compat.json2bson._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 */
class VariantRepository @Inject()(implicit ec: ExecutionContext, api: ReactiveMongoApi) extends MongoRepository {
  def collection: Future[BSONCollection] = api.database.map(db => db.collection("variants"))

  def findAll(limit: Int = 100): Future[Seq[Variant]] =
    collection.flatMap(_.find(BSONDocument(), Option.empty[Variant])
      .cursor[Variant](ReadPreference.Primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[Variant]]()))

  def findOne(id: BSONObjectID): Future[Option[Variant]] =
    collection.flatMap(_.find(queryBy(id), Option.empty[Variant]).one[Variant])

  def create(variant: Variant): Future[WriteResult] =
    collection.flatMap(_.insert(ordered = false)
      .one(variant.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime()))))

  def update(id: BSONObjectID, variant: Variant): Future[WriteResult] =
    collection.flatMap(_.update(ordered = false).one(queryBy(id),
      variant.copy(_updateDate = Some(new DateTime())))
    )

  def delete(id: BSONObjectID): Future[WriteResult] = {
    collection.flatMap(_.delete().one(queryBy(id), Some(1)))
  }
}
