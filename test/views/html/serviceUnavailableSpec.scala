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

package views.html

import uk.gov.hmrc.tai.util.viewHelpers.TaiViewSpec

class serviceUnavailableSpec extends TaiViewSpec {

  override def view = views.html.serviceUnavailable()

  "Service Unavailable Page" should {
    behave like pageWithHeader(messages("tai.gatekeeper.refuse.title"))
    behave like pageWithTitle(messages("tai.gatekeeper.refuse.title"))

    "display the correct error message in a paragraph" in {
      doc must haveParagraphWithText(messages("tai.service.error.message"))
    }

    "have a no update incomes link" in {
      val noUpdateIncomeLink = doc.getElementById("no-update-incomes-link")
      noUpdateIncomeLink.text must include(messages("tai.hmrc.enquiry.text"))
      noUpdateIncomeLink must haveLinkURL(messages("tai.hmrc.enquiry.url"))
    }
  }
}
