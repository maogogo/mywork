import sbt._

object Resolvers {

  val springIo  = "Spring IO" at "https://repo.spring.io/release"
  val artima    = "Artima Maven Repository" at "http://repo.artima.com/releases"
  val twttr     = "twttr" at "https://maven.twttr.com/"
  val twitter4j = "Twitter 4j Repo" at "http://twitter4j.org/maven2"
  val finch     = "finch-server" at "http://storage.googleapis.com/benwhitehead_me/maven/public"

  val repositories: Seq[MavenRepository] = Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    springIo,
    artima,
    twttr,
    twitter4j,
    finch)

}