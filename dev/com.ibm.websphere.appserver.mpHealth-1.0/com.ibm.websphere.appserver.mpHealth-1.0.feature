-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpHealth-1.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: org.eclipse.microprofile.health;  type="stable", \
    org.eclipse.microprofile.health.spi;  type="stable"
IBM-ShortName: mpHealth-1.0
Subsystem-Name: MicroProfile Health 1.0
-features=com.ibm.websphere.appserver.org.eclipse.microprofile.health-1.0, \
 com.ibm.websphere.appserver.cdi-1.2, \
 com.ibm.wsspi.appserver.webBundle-1.0, \
 com.ibm.websphere.appserver.servlet-3.1
-bundles=com.ibm.ws.require.java8, \
 com.ibm.websphere.org.eclipse.microprofile.health.1.0, \
 com.ibm.ws.microprofile.health; apiJar=false; location:="lib/", \
 com.ibm.ws.org.joda.time.1.6.2, \
 com.ibm.websphere.jsonsupport 
kind=noship
edition=full
