ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "docker-demo"
  )
enablePlugins(DockerPlugin)

docker / dockerfile := {
  val jarFile: File = (Compile / packageBin / sbt.Keys.`package`).value
  val classpath = (Compile / managedClasspath).value
  val mainclass = (Compile / packageBin / mainClass).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    from("openjdk:11-jre")
    add(classpath.files, "/app/")
    add(jarFile, jarTarget)
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"
//import sbtdocker.DockerKeys._
//
//lazy val dockerSettings = Seq(
//  // things the docker file generation depends on are listed here
//  dockerfile in docker := {
//    // any vals to be declared here
//    new sbtdocker.mutable.Dockerfile {
//      <<docker commands>>
//    }
//  }
//)
