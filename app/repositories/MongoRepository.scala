package repositories

import reactivemongo.api.bson.{BSONDocument, BSONObjectID}

import java.util.UUID

/**
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 */
trait MongoRepository {

  protected def queryBy(id: BSONObjectID) = {
    BSONDocument("_id" -> id)
  }

  protected def queryBy(id: UUID) = {
    BSONDocument("_id" -> id)
  }
}
