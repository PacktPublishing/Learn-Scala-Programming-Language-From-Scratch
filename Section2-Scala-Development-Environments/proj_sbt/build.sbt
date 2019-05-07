import com.github.retronym.SbtOneJar._

scalaVersion := "2.11.5"

name := "dev_env"

version := "1.0"

libraryDependencies ++= Seq(
   "com.rockymadden.stringmetric" % "stringmetric-core_2.10" % "0.27.3"
)

oneJarSettings
libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

resolvers ++= Seq(
  	"Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  	"Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
	"bintray-sbt-plugins"at "https://dl.bintray.com/sbt/sbt-plugin-releases/",
	"Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)


