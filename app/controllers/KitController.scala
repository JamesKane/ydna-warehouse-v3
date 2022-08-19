package controllers

import models.Kit
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import repositories.KitRepository

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class KitController @Inject()(implicit val ec: ExecutionContext, val repo: KitRepository, val controllerComponents: ControllerComponents) extends BaseController {

  def findAll(): Action[AnyContent] = Action.async { implicit request =>
    repo.findAll().map {
      kits => Ok(Json.toJson(kits))
    }
  }

  def findOne(id: UUID): Action[AnyContent] = Action.async { implicit request =>
    repo.findOne(id).map {
      kit => Ok(Json.toJson(kit))
    }
  }

  def create(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Kit].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      kit =>
        repo.create(kit).map {
          _ => Created(Json.toJson(kit))
        }
    )
  }

  def update(id: UUID): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Kit].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      kit =>
        repo.update(id, kit).map {
          result => Ok(Json.toJson(result.code))
        }
    )
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request =>
    repo.delete(id).map {
      _ => NoContent
    }
  }

}
