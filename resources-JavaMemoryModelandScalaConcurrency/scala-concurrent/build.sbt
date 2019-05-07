import com.github.retronym.SbtOneJar._
// Name of the project
name := "Concurrent Scala"

// Project version
version := "1.0.3"
val akkaVersion = "2.3.6"

// Version of Scala used by the project
scalaVersion := "2.11.7"

mainClass := Some("com.eduonix.EntryPoint")

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9"

libraryDependencies ++= Seq(
  "com.typesafe.akka"     %%  "akka-actor"              % akkaVersion,
  "ch.qos.logback"        %   "logback-classic"         % "1.1.2")

oneJarSettings
libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "bintray-sbt-plugins"at "https://dl.bintray.com/sbt/sbt-plugin-releases/",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true
