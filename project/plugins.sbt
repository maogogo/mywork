resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "sonatype-releases" at "http://oss.sonatype.org/content/repositories/releases"

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "17.11.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.2")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")
