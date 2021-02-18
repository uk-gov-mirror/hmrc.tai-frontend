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

package controllers.actions

import controllers.auth.{AuthenticatedRequest, OptionalDataRequest}
import uk.gov.hmrc.http.cache.client.CacheMap
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.tai.util.CachedData

import scala.concurrent.{ExecutionContext, Future}

class FakeDataRetrievalAction(cacheMapToReturn: Option[CacheMap]) extends DataRetrievalAction {

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[OptionalDataRequest[A]] =
    cacheMapToReturn match {
      case None => Future.successful(OptionalDataRequest(request, request.externalId, None))
      case Some(cacheMap) =>
        Future.successful(OptionalDataRequest(request, request.externalId, Some(new CachedData(cacheMap))))
    }

  override protected def executionContext: ExecutionContext = stubControllerComponents().executionContext
}