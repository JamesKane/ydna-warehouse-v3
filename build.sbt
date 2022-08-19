ThisBuild / scalaVersion := "2.13.8"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """WarehouseRedux""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.reactivemongo" %% "play2-reactivemongo" % "1.0.10-play28",
      "org.reactivemongo" %% "reactivemongo-bson-api" % "1.0.10",
      "org.reactivemongo" %% "reactivemongo-bson-geo" % "1.0.10",
      "com.typesafe.play" %% "play-json-joda" % "2.9.2"
    )
  )