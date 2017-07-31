-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpConfig-1.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: org.eclipse.microprofile.config;  type="spec", \
	org.eclipse.microprofile.config.spi;  type="spec", \
        org.eclipse.microprofile.config.inject;  type="spec"
IBM-ShortName: mpConfig-1.0
Subsystem-Name: MicroProfile Config
-features=com.ibm.websphere.appserver.org.eclipse.microprofile.config-1.0, \
 com.ibm.websphere.appserver.javax.cdi-1.2, \
 com.ibm.websphere.appserver.javax.annotation-1.2
-bundles=com.ibm.ws.require.java8, \
 com.ibm.ws.microprofile.config; apiJar=false; location:="lib/", \
 com.ibm.ws.com.netflix.archaius.2.1.10; apiJar=false; location:="lib/", \
 com.ibm.ws.org.apache.commons.lang3.3.5; apiJar=false; location:="lib/", \
 com.ibm.ws.org.slf4j.api.1.7.7; apiJar=false; location:="lib/", \
 com.ibm.ws.org.slf4j.jdk14.1.7.7; apiJar=false; location:="lib/"
kind=beta
edition=core
