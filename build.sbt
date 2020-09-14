import sbt.internal.ProjectMatrix

val scala2_11        = "2.11.12"
val scala2_12        = "2.12.12"
val scala2_13        = "2.13.3"
val allScalaVersions = List(scala2_11, scala2_12, scala2_13)

ThisBuild / organization := "com.47deg"
ThisBuild / scalaVersion := scala2_13

addCommandAlias("ci-test", "scalafmtCheckAll; scalafmtSbtCheck; mdoc; testCovered")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll; publishMicrosite")
addCommandAlias("ci-publish", "github; ci-release")

lazy val microsite = project
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .dependsOn(
    core.jvm(scala2_13),
    `scalacheck-toolbox-magic`.jvm(scala2_13),
    `scalacheck-toolbox-combinators`.jvm(scala2_13)
  )
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(
      Seq(
        core.jvm(scala2_13),
        `scalacheck-toolbox-magic`.jvm(scala2_13),
        `scalacheck-toolbox-combinators`.jvm(scala2_13)
      ).map(_.project): _*
    )
  )

lazy val documentation = project
  .settings(mdocOut := file("."))
  .enablePlugins(MdocPlugin)

lazy val core: ProjectMatrix = (projectMatrix in file("modules/scalacheck-toolbox-datetime"))
  .settings(description := "A library for helping use date and time libraries with ScalaCheck")
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck"         %%% "scalacheck"              % "1.14.3",
      "org.scala-lang.modules" %%% "scala-collection-compat" % "2.2.0",
      "joda-time"                % "joda-time"               % "2.10.6"
    )
  )
  .jvmPlatform(
    scalaVersions = allScalaVersions,
    libraryDependencies += "joda-time" % "joda-time" % "2.10.6"
  )
  .jsPlatform(
    scalaVersions = allScalaVersions,
    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0" % Test
  )

lazy val `scalacheck-toolbox-magic`: ProjectMatrix =
  (projectMatrix in file("modules/scalacheck-toolbox-magic"))
    .enablePlugins(BigListOfNaughtyStringsPlugin)
    .settings(description := "ScalaCheck Generators for magic values")
    .settings(libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.14.3")
    .jvmPlatform(scalaVersions = allScalaVersions)

lazy val `scalacheck-toolbox-combinators`: ProjectMatrix =
  (projectMatrix in file("modules/scalacheck-toolbox-combinators"))
    .settings(description := "Useful generic combinators for ScalaCheck")
    .settings(libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.14.3")
    .jvmPlatform(scalaVersions = allScalaVersions)
    .jsPlatform(scalaVersions = allScalaVersions)
