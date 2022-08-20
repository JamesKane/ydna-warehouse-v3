package controllers

import models.Subject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import repositories.SubjectRepository

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * A simple Subject CRUD controller
 *
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 *
 * @param ec                    An implicit execution context
 * @param subjectRepo           A subject repository
 * @param controllerComponents  Play controller components
 */
@Singleton
class SubjectController @Inject()(implicit val ec: ExecutionContext, val subjectRepo: SubjectRepository, val controllerComponents: ControllerComponents) extends BaseController {

  def findAll(): Action[AnyContent] = Action.async { implicit request =>
    subjectRepo.findAll().map {
      subjects => Ok(Json.toJson(subjects))
    }
  }

  def findOne(id: UUID): Action[AnyContent] = Action.async { implicit request =>
    subjectRepo.findOne(id).map {
      subject => Ok(Json.toJson(subject))
    }
  }

  def create(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Subject].fold(
      _ => FutureBodyParseFailure,
      subject =>
        subjectRepo.create(subject).map {
          _ => Created(Json.toJson(subject))
        }
    )
  }

  def update(id: UUID): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Subject].fold(
      _ => FutureBodyParseFailure,
      subject =>
        subjectRepo.update(id, subject).map {
          result => Ok(Json.toJson(result.code))
        }
    )
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request =>
    subjectRepo.delete(id).map {
      _ => NoContent
    }
  }

  val FutureBodyParseFailure = Future.successful(BadRequest("Cannot parse request body"))
}
