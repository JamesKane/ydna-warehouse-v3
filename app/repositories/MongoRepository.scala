package repositories

import reactivemongo.api.bson.{BSONDocument, BSONObjectID}

import java.util.UUID

trait MongoRepository {

  protected def queryBy(id: BSONObjectID) = {
    BSONDocument("_id" -> id)
  }

  protected def queryBy(id: UUID) = {
    BSONDocument("_id" -> id)
  }
}
