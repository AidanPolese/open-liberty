/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.metadata;

import com.ibm.ws.jaxws.bus.LibertyApplicationBus;
import com.ibm.ws.jaxws.bus.LibertyApplicationBusFactory;

/**
 * The class holds client runtime meta data for target application, those data will be recreated once the application is restarted.
 */
public class JaxWsClientMetaData {

    private final LibertyApplicationBus clientBus;

    private final JaxWsModuleMetaData moduleMetaData;

    public JaxWsClientMetaData(JaxWsModuleMetaData moduleMetaData) {
        this.moduleMetaData = moduleMetaData;
        this.clientBus = LibertyApplicationBusFactory.getInstance().createClientScopedBus(moduleMetaData);
    }

    public void destroy() {

        /* the server will not destroy the bus, we should also destroy the bus */
        if (clientBus != null)
            clientBus.shutdown(false);
    }

    public LibertyApplicationBus getClientBus() {
        return clientBus;
    }

    public JaxWsModuleMetaData getModuleMetaData() {
        return moduleMetaData;
    }

}
