Please update this file as necessary.

pom.xml - 
  * Downloads EclipseLink binaries from GSA
  * Processes(via package.xml) those binaries into jars that are consumable by the WAS build.
  * Why we use it? The ant dependencies for FTP (ant-commons-net.jar and commons-net.jar) and POST (ant-contrib.jar) are easily imported
  when using mvn. Alternative is placing commons-net.jar into your "ANT_HOME/lib" directory and defining the task for ant-contrib.jar 
 
package.xml -- 
  * Execute http post to contact a Jenkins server and start a Release Build
  * Downloads EclipseLink binaries from GSA.
  * Builds ossc.xml file
  * Invokes nls checking to see if any messages have changed in the new binaries
 
 
 HOW TO:
 
 Pull binaries:
 1. Change "build.properties" to the correct binary values you want to pull (ie. "eclipselink.version=2.6.1_WAS")
 2. Invoke "mvn install -Dgit.hash=GIT_HASH -Dgsa.id=ID -Dgsa.password=PASSWORD" from the command line
    *Replace "GIT_HASH" with the 7 digit, "git hash" value associate with the binaries you wish to pull in
    *Replace "ID" with your GSA ID. You may need access granted to the GSA directory
    *Replace "PASSWORD" with your GSA password
    *If you get an error, try using the credentials for the jpabuild GSA id
 
 Create a test iFix
 1. Change "build.properties" to the correct binary values you have built locally (ie. "eclipselink.version=2.6.1_WAS")
 2. Invoke "mvn install -Dlocal.build=true -Dplugin.dir=ECLIPSELINK_PLUGINS"
    *Replace "ECLIPSELINK_PLUGINS" with the absolute path to your local "eclipselink.runtime\plugins" directory
 