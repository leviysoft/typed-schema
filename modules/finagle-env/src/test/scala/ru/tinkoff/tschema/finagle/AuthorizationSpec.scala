package ru.tinkoff.tschema.finagle

import com.twitter.finagle.http.Response
import monix.eval.Task
import org.scalatest.flatspec.AnyFlatSpec
import ru.tinkoff.tschema.finagle.Authorization.{OAuth2, Provide}
import ru.tinkoff.tschema.finagle.envRouting.TaskRouting.TaskHttp
import ru.tinkoff.tschema.syntax._
import tofu.env.Env

class AuthorizationSpec extends AnyFlatSpec {

  "api with oauth authorization" should "compile" in {}
}

object AuthorizationSpec {
  val api = operation("test") |> get |> oauth[From, To]("param", None) |> $$[String]

  val service: TaskHttp[Response] =
    MkService[TaskHttp](api)(new {
      def test(param: To): Task[String] =
        Task.pure(s"Greetings, ${param.name}!")
    })

  implicit val provide = new Provide[TaskHttp, From] {
    override def provide: TaskHttp[Option[From]] = {
      Routed[TaskHttp].request.map { req =>
        Some(
          From(
            req.cookies("codeId").value.toInt,
            req.headerMap("Custom-Header")
          )
        )
      }
    }
  }

  implicit val auth = new Authorization[OAuth2, TaskHttp, To, From] {
    override def apply(s: Option[From]): TaskHttp[To] = s match {
      case Some(from) if from.param1 == 0 =>
        Env.pure(To(from.param1, from.param2))
      case None                           =>
        Routed[TaskHttp].reject(Rejection.unauthorized)
    }
  }

  implicit val toCompleteRequest: Completing[TaskHttp, String, Task[String]] = ???

  case class From(param1: Int, param2: String)
  case class To(id: Long, name: String)
}
