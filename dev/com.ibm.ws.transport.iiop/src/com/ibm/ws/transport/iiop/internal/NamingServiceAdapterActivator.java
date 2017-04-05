/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.internal;

import static org.apache.yoko.orb.spi.naming.NameServiceInitializer.createPOAPolicies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import com.ibm.ws.transport.iiop.spi.AdapterActivatorOp;
import com.ibm.ws.transport.iiop.spi.ORBRef;
import com.ibm.ws.transport.iiop.spi.ServerPolicySource;

/*
 * N.B. IF we need any more of these, we can make everything except the application of "static" policies into an abstract superclass easily.
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE, property = { "POAName=NameServicePOA" })
public class NamingServiceAdapterActivator implements AdapterActivatorOp {

    private ServerPolicySource serverPolicySource;

    //org.apache.yoko.orb.spi.naming.NameServiceInitializer.POA_NAME
    private final String poaName = "NameServicePOA";

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    protected void setServerPolicySource(ServerPolicySource serverPolicySource) {
        this.serverPolicySource = serverPolicySource;
    }

    @Override
    public boolean unknown_adapter(POA parent, String name, ORBRef orbRef) {
        ServerPolicySource serverPolicySource = this.serverPolicySource;
        if (serverPolicySource == null) {
            if (orbRef instanceof ServerPolicySource) {
                serverPolicySource = (ServerPolicySource) orbRef;
            } else {
                return false;
            }
        }
        if (poaName.equals(name)) {
            List<Policy> policies = new ArrayList<Policy>();
            try {
                serverPolicySource.addConfiguredPolicies(policies, orbRef);
            } catch (Exception e) { //TODO figure out what exceptions can occur
                throw new IllegalStateException(e);
            }

            POA rootPOA = orbRef.getPOA();
            policies.addAll(Arrays.asList(createPOAPolicies(rootPOA)));

            try {
                parent.create_POA(poaName, parent.the_POAManager(), policies.toArray(new Policy[policies.size()]));
            } catch (AdapterAlreadyExists e) {
                throw new IllegalStateException(e);
            } catch (InvalidPolicy e) {
                throw new IllegalStateException(e);
            }
            return true;
        }
        return false;
    }
}
