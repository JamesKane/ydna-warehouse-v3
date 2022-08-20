package controllers

import models.Variant
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import reactivemongo.api.bson.BSONObjectID
import repositories.VariantRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * A simple Variant CRUD Repo
 *
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 *
 * @param ec                    An implicit execution context
 * @param repo                  A variant repository
 * @param controllerComponents  Play controller components
 */
class VariantController @Inject()(implicit val ec: ExecutionContext, val repo: VariantRepository, val controllerComponents: ControllerComponents) extends BaseController {

  def findAll(): Action[AnyContent] = Action.async { implicit request =>
    repo.findAll().map { kits => Ok(Json.toJson(kits)) }
  }

  def findOne(id: String): Action[AnyContent] = Action.async { implicit request =>
    BSONObjectID.parse(id) match {
      case Success(value) =>
        repo.findOne(value).map {
          kit => Ok(Json.toJson(kit))
        }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the variant id"))
    }

  }

  def create(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Variant].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      kit => repo.create(kit).map { _ => Created(Json.toJson(kit)) }
    )
  }

  def update(id: String): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Variant].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      variant =>
        BSONObjectID.parse(id) match {
          case Success(value) => repo.update(value, variant).map { result => Ok(Json.toJson(result.code)) }
          case Failure(_) => Future.successful(BadRequest("Cannot parse the variant id"))
        }

    )
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    BSONObjectID.parse(id) match {
      case Success(value) =>
        repo.delete(value).map { _ => NoContent }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the variant id"))
    }
  }

}
