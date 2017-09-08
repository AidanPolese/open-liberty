-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpMetrics-1.0
visibility=public
singleton=true
IBM-API-Package: org.eclipse.microprofile.metrics.annotation;  type="stable", \
 org.eclipse.microprofile.metrics; type="stable"
IBM-ShortName: mpMetrics-1.0
Subsystem-Name: MicroProfile Metrics 1.0
-features=com.ibm.websphere.appserver.org.eclipse.microprofile.metrics-1.0, \
 com.ibm.websphere.appserver.cdi-1.2, \
 com.ibm.websphere.appserver.javax.annotation-1.2, \
 com.ibm.websphere.appserver.restHandler-1.0, \
 com.ibm.websphere.appserver.servlet-3.0; ibm.tolerates:=3.1
-bundles=com.ibm.ws.require.java8, \
 com.ibm.ws.microprofile.metrics
-jars=com.ibm.websphere.appserver.spi.restHandler; location:=dev/spi/ibm/, \
 com.ibm.websphere.microprofile.metrics; location:=target
kind=beta
edition=core
