package ru.tinkoff.tschema.swagger

import org.scalatest.{FunSuite, Inspectors, Matchers, OptionValues}

sealed trait Cop
case class ACase(aField: Int) extends Cop
case class BCase(bField: String) extends Cop

class DerivationSpec extends FunSuite with Matchers with OptionValues with Inspectors {
  test("Configured shapeless derivation") {
    implicit val cfg = SwaggerTypeable.defaultConfig.withDiscriminator("type").snakeCaseAlts
    implicit val atpbl = SwaggerTypeable.genTypeable[ACase]
    implicit val btpbl = SwaggerTypeable.genTypeable[BCase]
    val typeable = SwaggerTypeable.deriveNamedTypeable[Cop]

    typeable.typ shouldBe a [SwaggerOneOf]
    typeable.typ.asInstanceOf[SwaggerOneOf].discriminator.value shouldBe "type"
    forAll(typeable.typ.asInstanceOf[SwaggerOneOf].alts.map(_._2.value)) { alt =>
      alt shouldBe a [SwaggerRef]
    }
  }
}
