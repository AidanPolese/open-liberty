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
package com.ibm.ws.transport.iiop.client.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.security.csiv2.client.config.css.ClientContainerConfigHelper;
import com.ibm.ws.transport.iiop.security.AbstractCsiv2SubsystemFactory;
import com.ibm.ws.transport.iiop.security.ClientPolicy;
import com.ibm.ws.transport.iiop.security.config.css.CSSConfig;
import com.ibm.ws.transport.iiop.security.config.ssl.yoko.SocketFactory;
import com.ibm.ws.transport.iiop.spi.IIOPEndpoint;
import com.ibm.ws.transport.iiop.spi.SubsystemFactory;

@Component(service = SubsystemFactory.class,
                configurationPolicy = ConfigurationPolicy.IGNORE,
                property = { "service.vendor=IBM", "service.ranking:Integer=3" })
public class CSIv2ClientSubsystemFactory extends AbstractCsiv2SubsystemFactory {

    /** {@inheritDoc} */
    @Override
    public Policy getClientPolicy(ORB orb, Map<String, Object> properties) throws Exception {
        // TODO: Determine if system.RMI_OUTBOUND should be created and used for outbound.
        CSSConfig cssConfig = new ClientContainerConfigHelper(defaultAlias).getCSSConfig(properties);
        ClientPolicy clientPolicy = new ClientPolicy(cssConfig);
        return clientPolicy;
    }

    /** {@inheritDoc} */
    @Override
    public void addClientORBInitArgs(Map<String, Object> clientProperties, List<String> args) {
        args.add("-IIOPconnectionHelper");
        args.add(SocketFactory.class.getName());
    }

    /** {@inheritDoc} */
    @Override
    protected Set<String> extractSslRefs(Map<String, Object> properties, List<IIOPEndpoint> endpoints) {
        return new ClientContainerConfigHelper(defaultAlias).extractSslRefs(properties);
    }

}
