-include= ~../cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpFaultTolerance1.0-cdi1.2
visibility=private
singleton=true
IBM-Provision-Capability: osgi.identity; filter:="(&(type=osgi.subsystem.feature)(osgi.identity=com.ibm.websphere.appserver.mpFaultTolerance-1.0))", \
 osgi.identity; filter:="(&(type=osgi.subsystem.feature)(osgi.identity=com.ibm.websphere.appserver.cdi-1.2))"
-bundles=com.ibm.ws.microprofile.faulttolerance.cdi
IBM-Install-Policy: when-satisfied
kind=beta
edition=core