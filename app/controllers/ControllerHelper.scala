package controllers

import play.api.mvc.Result
import play.api.mvc.Results.BadRequest

import scala.concurrent.Future

object ControllerHelper {
  val VariantIdParseFailure: Future[Result] = Future.successful(BadRequest("Cannot parse the variant id"))
  val FutureBodyParseFailure: Future[Result] = Future.successful(BadRequest("Cannot parse request body"))
}
