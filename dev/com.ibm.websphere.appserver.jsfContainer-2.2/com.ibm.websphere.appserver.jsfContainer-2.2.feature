-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.jsfContainer-2.2
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: org.jboss.weld; type=internal,
 org.jboss.weld.bootstrap.api.helpers; type=internal,
 org.jboss.weld.context.http; type=internal,
 org.jboss.weld.jsf; type=internal
IBM-ShortName: jsfContainer-2.2
Subsystem-Name: JavaServer Faces Container 2.2
-features=com.ibm.websphere.appserver.javax.cdi-1.2, \
 com.ibm.websphere.appserver.servlet-3.1, \
 com.ibm.websphere.appserver.javax.validation-1.1, \
 com.ibm.websphere.appserver.jsp-2.3, \
 com.ibm.websphere.appserver.javaeeCompatible-7.0
kind=noship
edition=full
