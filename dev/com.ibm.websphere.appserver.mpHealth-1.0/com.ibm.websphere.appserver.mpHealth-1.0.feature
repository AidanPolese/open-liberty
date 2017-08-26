-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpHealth-1.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: org.eclipse.microprofile.health; type="spec"
IBM-ShortName: mpHealth-1.0
Subsystem-Name: MicroProfile Health 1.0
-features=com.ibm.websphere.appserver.org.eclipse.microprofile.health-1.0
-bundles=com.ibm.ws.require.java8, \
 com.ibm.ws.microprofile.health; apiJar=false; location:="lib/"
kind=noship
edition=full
