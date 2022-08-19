package repositories

import models.Lab
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat._
import json2bson._
import reactivemongo.api.commands.WriteResult

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LabRepository @Inject()(implicit ec: ExecutionContext, api: ReactiveMongoApi) {

  def collection: Future[BSONCollection] = api.database.map(db => db.collection("labs"))

  def findAll(limit: Int = 100): Future[Seq[Lab]] =
    collection.flatMap(_.find(BSONDocument(), Option.empty[Lab])
    .cursor[Lab](ReadPreference.Primary)
    collect[Seq](limit, Cursor.FailOnError[Seq[Lab]]()))

  def findOne(id: BSONObjectID): Future[Option[Lab]] =
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Lab]).one[Lab])

  def create(lab: Lab): Future[WriteResult] =
    collection.flatMap(_.insert(ordered = false).one(lab))

  def update(id: BSONObjectID, lab: Lab): Future[WriteResult] =
    collection.flatMap(_.update(ordered = false).one(BSONDocument("_id" -> id), lab))

  def delete(id: BSONObjectID): Future[WriteResult] =
    collection.flatMap(_.delete().one(BSONDocument("_id" -> id), Some(1)))
}
