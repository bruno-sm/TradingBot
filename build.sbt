val scala3Version = "3.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "bot",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    scalacOptions += "-source:future",

    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.12",
    libraryDependencies += "org.web3j" % "web3j-maven-plugin" % "4.8.7",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11",
    libraryDependencies += ("com.influxdb" % "influxdb-client-scala" % "6.2.0").cross(CrossVersion.for3Use2_13),
    libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.7.6" % Test,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )
