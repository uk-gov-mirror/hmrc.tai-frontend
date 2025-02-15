import sbt._

private object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._
  
  private val playVersion = "play-26"

  val compile = Seq(
    filters,
    jodaForms,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % s"1.2.0-$playVersion",
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVersion" % "3.4.0",
    "uk.gov.hmrc"       %% "domain"                           % s"5.10.0-$playVersion",
    "uk.gov.hmrc"       %% "url-builder"                      % s"3.4.0-$playVersion",
    "uk.gov.hmrc"       %% "play-partials"                    % s"7.1.0-$playVersion",
    "uk.gov.hmrc"       %% "play-language"                    % s"4.10.0-$playVersion",
    "uk.gov.hmrc"       %% "local-template-renderer"          % s"2.10.0-$playVersion",
    "uk.gov.hmrc"       %% "auth-client"                      % s"3.3.0-$playVersion",
    "uk.gov.hmrc"       %% "play-ui"                          % s"8.21.0-$playVersion",
    "org.typelevel"     %% "cats-core"                        % "2.0.0",
    "com.typesafe.play" %% "play-json-joda"                   % "2.6.10"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test: Seq[ModuleID] = Seq.empty

    val scalaTestPlusVersion = "3.1.3"
    val hmrcTestVersion = s"3.9.0-$playVersion"
    val pegdownVersion = "1.6.0"
    val jsoupVersion = "1.8.3"
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
        "uk.gov.hmrc"            %% "hmrctest"           % hmrcTestVersion % scope,
        "org.pegdown"            %  "pegdown"            % pegdownVersion % scope,
        "org.jsoup"              %  "jsoup"              % jsoupVersion % scope,
        "org.mockito"            %  "mockito-all"        % "1.9.5" % scope,
        "com.github.tomakehurst" %  "wiremock-jre8"      % "2.26.1" % scope,
        "com.typesafe.play"      %% "play-test"          % PlayVersion.current % scope,
        "org.scalacheck"         %% "scalacheck"         % "1.14.3" % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {
      override lazy val scope: String = "it"
      override lazy val test = Seq(
        "uk.gov.hmrc"            %% "hmrctest"           % hmrcTestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
        "org.pegdown"            %  "pegdown"            % pegdownVersion % scope,
        "org.jsoup"              %  "jsoup"              % jsoupVersion % scope,
        "com.typesafe.play"      %% "play-test"          % PlayVersion.current % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}
