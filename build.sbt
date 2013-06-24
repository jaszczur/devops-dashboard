name := "Devops"
 
version := "1.0"
  
scalaVersion := "2.10.2"
   
resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
    
libraryDependencies ++= Seq(
  "junit"             % "junit"           % "4.11"  % "test",
  "com.novocode"      % "junit-interface" % "0.7"   % "test->default"
)

