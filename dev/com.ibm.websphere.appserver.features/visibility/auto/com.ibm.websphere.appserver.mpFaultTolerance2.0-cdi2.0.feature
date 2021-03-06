-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpFaultTolerance2.0-cdi2.0
visibility=private
singleton=true
IBM-Provision-Capability: \
  osgi.identity; filter:="(&(type=osgi.subsystem.feature)(osgi.identity=com.ibm.websphere.appserver.mpFaultTolerance-2.0))", \
  osgi.identity; filter:="(&(type=osgi.subsystem.feature)(osgi.identity=com.ibm.websphere.appserver.cdi-2.0))"
-bundles=com.ibm.ws.microprofile.faulttolerance.cdi,\
  com.ibm.ws.microprofile.faulttolerance.cdi.2.0.services,\
  com.ibm.ws.microprofile.faulttolerance.cdi.2.0
IBM-Install-Policy: when-satisfied
kind=beta
edition=core
