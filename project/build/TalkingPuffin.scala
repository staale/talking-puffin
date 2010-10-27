import sbt._

class TalkingPuffin(info: ProjectInfo) extends ParentProject(info) with IdeaProject {

  override val name = "TalkingPuffin Parent Project"

//  val lag_net = "lag.net Repository" at "http://www.lag.net/repo"
  val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
  
  val scala_version = "2.7.7"

  lazy val twitter_api = project("twitter-api", "TalkingPuffin Twitter API", new TwitterApi(_))
  lazy val desktop = project("desktop", "TalkingPuffin Desktop", new Desktop(_), twitter_api)
  lazy val web = project("web-mvn", "TalkingPuffin Web Client", new Web(_), twitter_api, desktop)

  class TwitterApi(info: ProjectInfo) extends DefaultProject(info) with ScalaProject with IdeaProject {
    val scala_swing = "org.scala-lang" % "scala-swing" % scala_version
    val commons_httpclient = "commons-httpclient" % "commons-httpclient" % "3.1"
    val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"
    val google_collections = "com.google.collections" % "google-collections" % "1.0-rc1"
    val log4j = "log4j" % "log4j" % "1.2.14"
    val junit = "junit" % "junit" % "4.5"
    val joda_time = "joda-time" % "joda-time" % "1.6"
  }

  class Desktop(info: ProjectInfo) extends DefaultProject(info) with ScalaProject with IdeaProject {

    override def mainClass = Some("org.talkingpuffin.Main")

    val configgy = "net.lag" % "configgy" % "1.6.6"
    val swingx = "org.swinglabs" % "swingx" % "1.0"
    val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"
    val junit = "junit" % "junit" % "4.5"
  }

  class Web(info: ProjectInfo) extends DefaultWebProject(info) with ScalaProject with IdeaProject {
    val servletapi = "javax.servlet" % "servlet-api" % "2.5"
  }
}
