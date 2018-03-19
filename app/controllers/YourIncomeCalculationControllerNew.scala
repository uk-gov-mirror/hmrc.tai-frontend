/*
 * Copyright 2018 HM Revenue & Customs
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

import controllers.auth.{TaiUser, WithAuthorisedForTaiLite}
import play.api.mvc.{Action, AnyContent, Request}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.DelegationAwareActions
import uk.gov.hmrc.play.partials.PartialRetriever
import uk.gov.hmrc.tai.config.TaiHtmlPartialRetriever
import uk.gov.hmrc.tai.connectors.LocalTemplateRenderer
import uk.gov.hmrc.tai.connectors.responses.TaiSuccessResponseWithPayload
import uk.gov.hmrc.tai.model.domain.income.TaxCodeIncome
import uk.gov.hmrc.tai.model.tai.TaxYear
import uk.gov.hmrc.tai.service.{EmploymentService, TaiService, TaxAccountService}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.tai.model.TaiRoot
import uk.gov.hmrc.tai.viewModels.YourIncomeCalculationViewModelNew

trait YourIncomeCalculationControllerNew extends TaiBaseController
  with DelegationAwareActions
  with WithAuthorisedForTaiLite {

  def taiService: TaiService

  def taxAccountService: TaxAccountService

  def employmentService: EmploymentService

  def yourIncomeCalculationPage(empId: Int): Action[AnyContent] = authorisedForTai(taiService).async {
    implicit user =>
      implicit taiRoot =>
        implicit request =>
          ServiceCheckLite.personDetailsCheck {
            incomeCalculationPage(empId, false)
          }
  }

  def printYourIncomeCalculationPage(empId: Int): Action[AnyContent] = authorisedForTai(taiService).async {
    implicit user =>
      implicit taiRoot =>
        implicit request =>
          ServiceCheckLite.personDetailsCheck {
            incomeCalculationPage(empId, true)
          }
  }

  private def incomeCalculationPage(empId: Int, printPage: Boolean)(implicit request: Request[AnyContent], user: TaiUser, taiRoot: TaiRoot) = {
    val taxCodeIncomesFuture = taxAccountService.taxCodeIncomes(Nino(user.getNino), TaxYear())
    val employmentFuture = employmentService.employment(Nino(user.getNino), empId)

    for {
      taxCodeIncomeDetails <- taxCodeIncomesFuture
      employmentDetails <- employmentFuture
    } yield {
      (taxCodeIncomeDetails, employmentDetails) match {
        case (TaiSuccessResponseWithPayload(taxCodeIncomes: Seq[TaxCodeIncome]), Some(employment)) =>
          taxCodeIncomes.find(_.employmentId.contains(empId)) match {
            case Some(taxCodeIncome) =>
              val model = YourIncomeCalculationViewModelNew(taxCodeIncome, employment)
              if(printPage){
                Ok(views.html.print.yourIncomeCalculationNew(model))
              } else {
                Ok(views.html.incomes.yourIncomeCalculationNew(model))
              }
            case _ => throw new RuntimeException("Error while fetching tax code income details")
          }
        case _ => throw new RuntimeException("Error while fetching RTI details")
      }
    }
  }
}

object YourIncomeCalculationControllerNew extends YourIncomeCalculationControllerNew with AuthenticationConnectors {
  override implicit def templateRenderer = LocalTemplateRenderer
  override implicit def partialRetriever: PartialRetriever = TaiHtmlPartialRetriever

  override val taiService = TaiService
  override val taxAccountService: TaxAccountService = TaxAccountService
  override val employmentService: EmploymentService = EmploymentService
}