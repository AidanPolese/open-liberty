-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.jsr375-1.0
visibility=public
IBM-API-Package: javax.security.enterprise; type="spec", \
 javax.security.enterprise.authentication.mechanism.http; type="spec", \
 javax.security.enterprise.credential; type="spec", \
 javax.security.enterprise.identitystore; type="spec"
IBM-ShortName: jsr375-1.0
#IBM-SPI-Package:
Subsystem-Name: JSR-375 Security 1.0
-features=com.ibm.websphere.appserver.cdi-1.2, \
 com.ibm.websphere.appserver.jaspic-1.1, \
 com.ibm.websphere.appserver.servlet-3.0; ibm.tolerates:=3.1
-bundles=com.ibm.websphere.javaee.jsr375.1.0; location:=dev/api/spec/, \
 com.ibm.ws.security.jsr375.1.0, \
 com.ibm.ws.security.jsr375.cdi
kind=noship
edition=core
#-jars=xyz; location:=dev/spi/ibm/
#-files=dev/spi/ibm/javadoc/com.ibm.websphere.appserver.spi.jsr375_1.0-javadoc.zip