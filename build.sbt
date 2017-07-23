import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.pgp.PgpKeys._
import sbtunidoc.Plugin.UnidocKeys._

lazy val commonScalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-encoding", "utf8",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xfuture",
  "-Xlint",
  // "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard")

lazy val buildSettings = Seq(
  name := "prologScala",
  organization in Global := "org.ieee.oforero",
  scalaVersion in Global := "2.12.2"
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false,
  publishSigned := ()
)

lazy val credentialSettings = Seq(
  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 75,
  coverageFailOnMinimum := false,
  coverageExcludedPackages := "instances"
)

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions,
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats" % "0.9.0"
  ),
  fork in test := true
)


lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  homepage := Some(url("https://github.com/oforero/prologScala")),
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  scmInfo := Some(ScmInfo(url("https://github.com/oforero/prologScala"), "scm:git:git@github.com:oforero/prologScala.git")),
  autoAPIMappings := true,
  pomExtra := (
    <developers>
      <developer>
        <name>Oscar Forero</name>
        <url>@oforero</url>
      </developer>
    </developers>
  )
) ++ credentialSettings


lazy val prologScalaSettings = buildSettings ++ commonSettings ++ scoverageSettings

lazy val prologScala = project.in(file("."))
  .settings(moduleName := "root")
  .settings(noPublishSettings:_*)
  .aggregate(docs, tests, core)

lazy val core = project.in(file("core"))
  .settings(moduleName := "prologScala-core")
  .settings(prologScalaSettings:_*)
  .settings(publishSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "org.parboiled" %% "parboiled" % "2.1.4"
    )
  )

lazy val docSettings = Seq(
  autoAPIMappings := true,
  micrositeName := "prologScala",
  micrositeDescription := "A Prolog parser/interpreter in Scala",
  micrositeBaseUrl :="/prologScala",
  micrositeDocumentationUrl := "/prologScala/api",
  micrositeGithubOwner := "oforero",
  micrositeGithubRepo := "prologScala",
  micrositeHighlightTheme := "atom-one-light",
  micrositePalette := Map(
    "brand-primary" -> "#5B5988",
    "brand-secondary" -> "#292E53",
    "brand-tertiary" -> "#222749",
    "gray-dark" -> "#49494B",
    "gray" -> "#7B7B7E",
    "gray-light" -> "#E5E5E6",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"),
  git.remoteRepo := "git@github.com:oforero/prologScala.git",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md",
  ghpagesNoJekyll := false,
  siteSubdirName in ScalaUnidoc := "api",
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc),
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(tests)
)

lazy val docs = project
    .enablePlugins(MicrositesPlugin)
    .settings(moduleName := "prolog-docs")
    .settings(unidocSettings: _*)
    .settings(ghpages.settings)
    .dependsOn(core)
    .settings(docSettings:_*)
    .settings(prologScalaSettings:_*)
    .settings(noPublishSettings:_*)

lazy val tests = project.in(file("tests"))
  .dependsOn(core)
  .settings(moduleName := "prologScala-tests")
  .settings(prologScalaSettings:_*)
  .settings(noPublishSettings:_*)
  .settings(
    coverageEnabled := false,
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-laws" % "0.9.0",
      "org.scalatest"  %% "scalatest" % "3.0.0" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    )
  )
