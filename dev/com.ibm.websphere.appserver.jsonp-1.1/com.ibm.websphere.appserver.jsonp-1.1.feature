-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.jsonp-1.1
visibility=public
IBM-API-Package: javax.json; type="spec", \
 javax.json.stream; type="spec", \
 javax.json.spi; type="spec"
IBM-ShortName: jsonp-1.1
Subsystem-Name: JavaScript Object Notation Processing
-features=com.ibm.websphere.appserver.jsonpImpl-1.1.1; ibm.tolerates:="1.1.0"
kind=beta
edition=core
