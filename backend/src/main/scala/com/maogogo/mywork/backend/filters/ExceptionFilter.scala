package com.maogogo.mywork.backend.filters

import com.twitter.finagle._
import com.twitter.util.Future
import com.twitter.finagle.http.{ Status ⇒ HttpStatus, _ }
import scala.util.control.NonFatal

//@Providers
class ExceptionFilter extends SimpleFilter[Request, Response] {

  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    try {
      service(request)
    } catch {
      case NonFatal(e) ⇒
        println(e)
        e.printStackTrace()
        Future.exception(e)
    }
  }
  //  rescue {
  //    case e: CancelledRequestException ⇒
  //      // This only happens when ChannelService cancels a reply.
  //      //log.warning("cancelled request: uri:%s", request.uri)
  //      respond(request, ClientClosedRequestStatus)
  //    case e: Throwable ⇒
  //      try {
  //        log.warning(e, "exception: uri:%s exception:%s", request.uri, e)
  //        respond(request, Status.InternalServerError)
  //      } catch {
  //        // logging or internals are broken.  Write static string to console -
  //        // don't attempt to include request or exception.
  //        case e: Throwable ⇒
  //          Console.err.println("ExceptionFilter failed")
  //          throw e
  //      }
  //  }

  private def respond(request: Request, responseStatus: HttpStatus): Future[Response] = {
    val response = Response(responseStatus)
    //response.status = responseStatus
    response.clearContent()
    response.contentLength = 0
    Future.value(response)
  }
}