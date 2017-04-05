-include= ~../cnf/resources/bnd/feature.props

symbolicName = com.ibm.websphere.appserver.laosBundle
visibility = install
singleton = true

IBM-ShortName: laosBundle
-features: com.ibm.websphere.appserver.javaee-7.0, \
 com.ibm.websphere.appserver.javaeeClient-7.0, \
 com.ibm.websphere.appserver.appSecurityClient-1.0, \
 com.ibm.websphere.appserver.bells-1.0, \
 com.ibm.websphere.appserver.microProfile-1.0, \
 com.ibm.websphere.appserver.passwordUtiliites-1.0, \
 com.ibm.websphere.appserver.ldapRegistry-3.0, \
 com.ibm.websphere.appserver.federatedRepository-1.0, \
 com.ibm.websphere.appserver.monitor-1.0, \
 com.ibm.websphere.appserver.restConnector-2.0, \
 com.ibm.websphere.appserver.localConnector-1.0

Subsystem-Name: Liberty as Open Source Bundle
edition=full
kind=noship
