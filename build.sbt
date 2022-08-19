ThisBuild / scalaVersion := "2.13.8"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """WarehouseRedux""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.reactivemongo" %% "play2-reactivemongo" % "0.20.13-play28",
      "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.1-play28",
      "org.reactivemongo" %% "reactivemongo-bson-compat" % "0.20.13",
      "com.typesafe.play" %% "play-json-joda" % "2.9.2"
    )
  )