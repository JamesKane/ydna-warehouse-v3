package controllers

import play.api.libs.json.{Json, JsValue}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import reactivemongo.bson.BSONObjectID
import models.Subject
import repositories.SubjectRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class SubjectController @Inject()(implicit val ec: ExecutionContext, val subjectRepo: SubjectRepository, val controllerComponents: ControllerComponents) extends BaseController {

  def findAll(): Action[AnyContent] = Action.async { implicit request =>
    subjectRepo.findAll().map {
      subjects => Ok(Json.toJson(subjects))
    }
  }

  def findOne(id: String): Action[AnyContent] = Action.async { implicit request =>
    BSONObjectID.parse(id) match {
      case Success(objectID) => subjectRepo.findOne(objectID).map {
        person => Ok(Json.toJson(person))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the person id"))
    }
  }

  def create(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Subject].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      subject =>
        subjectRepo.create(subject).map {
          _ => Created(Json.toJson(subject))
        }
    )
  }

  def update(id: String): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request =>
    request.body.validate[Subject].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      subject =>
        BSONObjectID.parse(id) match {
          case Success(objectId) => subjectRepo.update(objectId, subject).map {
            result => Ok(Json.toJson(result.ok))
          }
        }
    )
  }

  def delete(id: String): Action[AnyContent] = Action.async {implicit request =>
    BSONObjectID.parse(id) match {
      case Success(value) => subjectRepo.delete(value).map {
        _ => NoContent
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the subject id"))
    }
  }
}
