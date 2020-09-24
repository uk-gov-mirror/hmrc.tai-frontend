/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.{Args, Status, Suite, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.tai.model.domain.Person

import scala.concurrent.ExecutionContext

trait FakeTaiPlayApplication extends GuiceOneServerPerSuite with PatienceConfiguration with TestSuite {
  this: Suite =>
  override lazy val port = 12345

  val additionalConfiguration = Map[String, Any](
    "govuk-tax.services.contact-frontend.host"         -> "localhost",
    "govuk-tax.services.contact-frontend.port"         -> "12345",
    "govuk-tax.services.pertax-frontend.host"          -> "localhost",
    "govuk-tax.services.pertax-frontend.port"          -> "1111",
    "govuk-tax.services.personal-tax-summary.host"     -> "localhost",
    "govuk-tax.services.personal-tax-summary.port"     -> "2222",
    "govuk-tax.services.activity-logger.host"          -> "localhost",
    "govuk-tax.services.activity-logger.port"          -> "12345",
    "tai.cy3.enabled"                                  -> true,
    "govuk-tax.services.feedback-survey-frontend.host" -> "localhost",
    "govuk-tax.services.feedback-survey-frontend.port" -> "3333",
    "govuk-tax.services.company-auth.host"             -> "localhost",
    "govuk-tax.services.company-auth.port"             -> "4444",
    "govuk-tax.services.citizen-auth.host"             -> "localhost",
    "govuk-tax.services.citizen-auth.port"             -> "9999",
    "metrics.enabled"                                  -> false
  )

  implicit override lazy val app: Application = new GuiceApplicationBuilder().configure(additionalConfiguration).build()

  org.slf4j.LoggerFactory
    .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    .asInstanceOf[ch.qos.logback.classic.Logger]
    .setLevel(ch.qos.logback.classic.Level.WARN)

  def fakePerson(nino: Nino) = Person(nino, "firstname", "surname", false, false)
  val fakeRequest: FakeRequest[AnyContent] = FakeRequest("GET", "/")

  abstract override def run(testName: Option[String], args: Args): Status =
    super[GuiceOneServerPerSuite].run(testName, args)

  implicit val ec = app.injector.instanceOf[ExecutionContext]
}
