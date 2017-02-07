package ru.tinkoff.travel.schema

import akka.http.scaladsl.server.Route
import akka.shapeless.ops.hlist.Prepend
import shapeless._

object typeDSL {
  /**
    * Any path component that is subtype of Meta will be ignored
    * by `Serve` but could support additional information like swagger tags or descriptions
    */
  trait Meta


  /**
    * indicates result of element of type `x`
    * via POST HTTP method
    * all `Get`s and `Post`s except last will be ignored
    */
  final class Post[x]

  /**
    * indicates result of element of type `x`
    * via GET HTTP method
    * all `Get`s and `Post`s except last will be ignored
    */
  final class Get[x]

  /**
    * Indicated single path prefix
    * Could be replaced by it's parameter
    *
    * @tparam pref singleton string
    */
  final class Prefix[pref]
  /**
    * captures param from query
    *
    * @tparam name name of param
    * @tparam x    type of param, should have instance of `FromQueryParam`
    */
  final class QueryParam[name, x]

  /**
    * captures param from path element
    *
    * @tparam name name of param, have no effect to routing
    * @tparam x    type of param, should have instance of `FromPathParam`
    */
  final class Capture[name, x]

  /**
    * captures param list from query
    *
    * @tparam name name of param
    * @tparam x    type of param, should have instance of `FromQueryParam`
    */
  final class QueryParams[name, x]

  /**
    * captures fact of provision of param in query
    *
    * @tparam name name of param
    */
  final class QueryFlag[name]

  /**
    * captures request body and unmarshalls in to requested type
    *
    * @tparam x type of body, should have `FromRequestUnmarshaller` instance
    */
  final class ReqBody[x]

  /**
    * captures header value
    *
    * @tparam name header name
    * @tparam x    parameter type, should have `FromHeader` instance
    */
  final class Header[name, x]

  /**
    * captures field value from form data
    * @tparam name field name
    * @tparam x parameter type, should have `FromFormParam` instance
    */
  final class FormField[name, x]

  /**
    * captures field value from Cookie
    */
  final class Cookie[name, x]

  /**
    * concatenates pair of paths into complete path
    *
    * @tparam path    prefix - always simple path without disjunctions
    * @tparam postfix postfix
    */
  final class :>[path, postfix]

  /**
    * disjunction operator
    * can be used both for defining API type and for joining different handlers
    * resulting type is effectively `Either[left input, right input] => Either[left output, right output]`
    */
  final case class <|>[left, right](left: left, right: right){
    override def toString = s"$left <|> $right"
  }

  implicit class TypeApiOps[x](val x: x) {
    def <|>[y](y: y): x <|> y = new <|>(x, y)
    def :>[y](y: y): x :> y = new :>
  }



  object Get {
    def apply[x]: Get[x] = new Get
  }

  object Post {
    def apply[x]: Post[x] = new Post
  }

  object ReqBody {
    def apply[x]: ReqBody[x] = new ReqBody
  }
}
