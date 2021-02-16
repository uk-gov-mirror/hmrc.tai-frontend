/*
 * Copyright 2021 HM Revenue & Customs
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

import cats.implicits.catsSyntaxFlatten
import cats.instances.future._
import com.google.inject.{Inject, Singleton}
import controllers.actions.{DataRequiredAction, DataRetrievalActionProvider, ValidatePerson}
import controllers.auth.AuthAction
import play.api.mvc._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.partials.FormPartialRetriever
import uk.gov.hmrc.renderer.TemplateRenderer
import uk.gov.hmrc.tai.config.ApplicationConfig
import uk.gov.hmrc.tai.connectors.DataCacheConnector
import uk.gov.hmrc.tai.identifiers.JrsClaimsId
import uk.gov.hmrc.tai.service._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JrsClaimsController @Inject()(
  val auditConnector: AuditConnector,
  authenticate: AuthAction,
  validatePerson: ValidatePerson,
  dataRetrievalAction: DataRetrievalActionProvider,
  dataRequiredAction: DataRequiredAction,
  dataCacheConnector: DataCacheConnector,
  jrsService: JrsService,
  mcc: MessagesControllerComponents,
  appConfig: ApplicationConfig,
  override implicit val partialRetriever: FormPartialRetriever,
  override implicit val templateRenderer: TemplateRenderer)(implicit ec: ExecutionContext)
    extends TaiBaseController(mcc) {

  private def authorise =
    (authenticate andThen validatePerson andThen dataRetrievalAction.getData() andThen dataRequiredAction)

  def onPageLoad(): Action[AnyContent] = authorise.async { implicit request =>
    val nino = request.request.taiUser.nino

    if (appConfig.jrsClaimsEnabled) {

      request.cachedData.getJrsClaims match {
        case Some(jrsClaims) => Future.successful(Ok(views.html.jrsClaimSummary(jrsClaims, appConfig)))
        case None =>
          jrsService
            .getJrsClaims(nino)
            .fold(
              Future.successful(NotFound(views.html.noJrsClaim(appConfig)))
            )(
              jrsClaims => {
                val dataWithJrs = request.cachedData.set(JrsClaimsId, jrsClaims)
                dataCacheConnector.save(dataWithJrs) map { _ =>
                  Ok(views.html.jrsClaimSummary(jrsClaims, appConfig))
                }
              }
            )
            .flatten
      }
    } else {
      Future.successful(InternalServerError(views.html.internalServerError(appConfig)))
    }
  }
}
