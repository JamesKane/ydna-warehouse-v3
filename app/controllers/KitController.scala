package controllers

import models.Kit
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import repositories.KitRepository

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * A basic CRUD controller for Kits
 *
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 *
 * @param ec                    An implicit execution context
 * @param repo                  A kit repository
 * @param controllerComponents  Play controller components
 */
@Singleton
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
      _ => FutureBodyParseFailure,
      kit =>
        repo.create(kit).map {
          _ => Created(Json.toJson(kit))
        }
    )
  }

  def update(id: UUID): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Kit].fold(
      _ => FutureBodyParseFailure,
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


  val FutureBodyParseFailure = Future.successful(BadRequest("Cannot parse request body"))
}
