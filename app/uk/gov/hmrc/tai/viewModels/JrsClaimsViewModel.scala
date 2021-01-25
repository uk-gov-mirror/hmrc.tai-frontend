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

package uk.gov.hmrc.tai.viewModels

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.tai.model.{Employers, JrsClaims}

case class JrsClaimsViewModel(latestEmpoymentDate: String, isMultipleEmployer: Boolean, employers: List[Employers])

object JrsClaimsViewModel {

  def apply(jrsClaims: JrsClaims): JrsClaimsViewModel =
    JrsClaimsViewModel(
      jrsClaims.employers.head.claims.head.yearAndMonth,
      jrsClaims.employers.size > 1,
      jrsClaims.employers)

  implicit lazy val format: Format[JrsClaimsViewModel] = Json.format[JrsClaimsViewModel]

}