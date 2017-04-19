/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.transaction;

import java.util.Map;

import javax.transaction.TransactionManager;

import org.apache.yoko.osgi.locator.BundleProviderLoader;
import org.apache.yoko.osgi.locator.Register;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.transport.iiop.spi.SubsystemFactory;
import com.ibm.ws.transport.iiop.transaction.nodistributedtransactions.NoDTxClientTransactionPolicyConfig;
import com.ibm.ws.transport.iiop.transaction.nodistributedtransactions.NoDtxServerTransactionPolicyConfig;

@Component(service = SubsystemFactory.class, configurationPolicy = ConfigurationPolicy.IGNORE, property = { "service.ranking:Integer=2" })
public class TransactionSubsystemFactory extends SubsystemFactory {

    private Register providerRegistry;
    private BundleProviderLoader transactionInitializerClass;

    private TransactionManager transactionManager;

    @Reference
    protected void setRegister(Register providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Reference
    protected void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        Bundle bundle = bundleContext.getBundle();
        transactionInitializerClass = new BundleProviderLoader(TransactionInitializer.class.getName(), TransactionInitializer.class.getName(), bundle, 1);
        providerRegistry.registerProvider(transactionInitializerClass);
    }

    @Deactivate
    protected void deactivate() {
        providerRegistry.unregisterProvider(transactionInitializerClass);
    }

    /** {@inheritDoc} */
    @Override
    public Policy getTargetPolicy(ORB orb, Map<String, Object> properties, Map<String, Object> extraConfig) throws Exception {
        return new ServerTransactionPolicy(new NoDtxServerTransactionPolicyConfig(transactionManager));
    }

    /** {@inheritDoc} */
    @Override
    public Policy getClientPolicy(ORB orb, Map<String, Object> properties) throws Exception {
        return new ClientTransactionPolicy(new NoDTxClientTransactionPolicyConfig(transactionManager));
    }

    /** {@inheritDoc} */
    @Override
    public String getInitializerClassName(boolean endpoint) {
        return TransactionInitializer.class.getName();
    }

}
