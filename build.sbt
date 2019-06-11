import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := "akka-sharding-practice"

version := "0.1"

scalaVersion := "2.12.0"

libraryDependencies ++= {
  val akkaVersion = "2.5.23"
  Seq(
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "org.iq80.leveldb"  % "leveldb"                % "0.11",
    "com.typesafe.akka" %% "akka-testkit"               % akkaVersion   % Test,
    "com.typesafe.akka" %% "akka-multi-node-testkit"    % akkaVersion   % Test,
    "org.scalatest"     %% "scalatest"                  % "3.0.7"       % Test,
    "commons-io"        %  "commons-io"                 % "2.4",
  )
}

lazy val root = (project in file("."))
  .enablePlugins(MultiJvmPlugin)
  .configs(MultiJvm)
  .settings(multiJvmSettings: _*) // apply the default settings
  .settings(
    parallelExecution in Test := false,
    logLevel := Level.Debug,
    multiNodeHosts in MultiJvm := Seq("xcwang@10.10.11.5", "xcwang@10.10.11.5")
  )
