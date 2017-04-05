-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.jca-1.6
visibility=public
singleton=true
IBM-API-Package: com.ibm.ws.jca.service; type="internal"
IBM-ShortName: jca-1.6
Subsystem-Name: Java Connector Architecture 1.6
-features=com.ibm.websphere.appserver.javax.connector-1.6, \
 com.ibm.websphere.appserver.javaeePlatform-6.0, \
 com.ibm.websphere.appserver.internal.jca-1.6, \
 com.ibm.websphere.appserver.javaeeCompatible-6.0
-bundles=com.ibm.ws.app.manager.rar
kind=ga
edition=base
