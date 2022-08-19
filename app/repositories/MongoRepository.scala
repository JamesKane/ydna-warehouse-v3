package repositories

import reactivemongo.api.bson.{BSONDocument, BSONObjectID}

trait MongoRepository {

  protected def queryBy(id: BSONObjectID) = {
    BSONDocument("_id" -> id)
  }

}
