-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.jwt-1.0
visibility=public
IBM-ShortName: jwt-1.0
IBM-API-Package: com.ibm.websphere.security.jwt; type="ibm-api",


Subsystem-Name: Json Web Token
-features=com.ibm.wsspi.appserver.webBundle-1.0, \
  com.ibm.wsspi.appserver.webBundleSecurity-1.0, \
  com.ibm.websphere.appserver.servlet-3.0; ibm.tolerates:=3.1


-jars=com.ibm.websphere.appserver.api.jwt; location:=dev/api/ibm/

-files=dev/api/ibm/javadoc/com.ibm.websphere.appserver.api.jwt_1.1-javadoc.zip

kind=ga
edition=core
