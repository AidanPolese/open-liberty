/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transaction.services;

import com.ibm.tx.config.RuntimeMetaDataProvider;
import com.ibm.tx.jta.embeddable.GlobalTransactionSettings;
import com.ibm.tx.jta.embeddable.TransactionSettingsProvider;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class LibertyRuntimeMetaDataProvider implements RuntimeMetaDataProvider {

    private static final TraceComponent tc = Tr.register(LibertyRuntimeMetaDataProvider.class);

    private final JTMConfigurationProvider _serverWideConfigProvider;

    public LibertyRuntimeMetaDataProvider(JTMConfigurationProvider cp) {
        _serverWideConfigProvider = cp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.tx.config.RuntimeMetaDataProvider#getTransactionTimeout()
     */
    @Override
    public int getTransactionTimeout() {
        for (TransactionSettingsProvider s : _serverWideConfigProvider.getTransactionSettingsProviders().services()) {
            if (s.isActive()) {

                final GlobalTransactionSettings gts = s.getGlobalTransactionSettings();

                if (gts != null) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Found a GlobalTransactionSettings");
                    return gts.getTransactionTimeout();
                } else {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "No GlobalTransactionSettings on this thread");
                }
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Found the TransactionSettingsProvider but it was inactive");
            }
        }

        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.tx.config.RuntimeMetaDataProvider#isClientSideJTADemarcationAllowed()
     */
    @Override
    public boolean isClientSideJTADemarcationAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.tx.config.RuntimeMetaDataProvider#isHeuristicHazardAccepted()
     */
    @Override
    public boolean isHeuristicHazardAccepted() {
        // TODO
        return _serverWideConfigProvider.isAcceptHeuristicHazard();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.tx.config.RuntimeMetaDataProvider#isUserTransactionLookupPermitted(java.lang.String)
     */
    @Override
    public boolean isUserTransactionLookupPermitted(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

}
