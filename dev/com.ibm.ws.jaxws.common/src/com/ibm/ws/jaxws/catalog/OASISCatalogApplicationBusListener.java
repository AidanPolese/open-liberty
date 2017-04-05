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
package com.ibm.ws.jaxws.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.cxf.Bus;
import org.apache.cxf.catalog.OASISCatalogManager;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.JaxWsConstants;
import com.ibm.ws.jaxws.bus.LibertyApplicationBus;
import com.ibm.ws.jaxws.bus.LibertyApplicationBusListener;
import com.ibm.ws.jaxws.metadata.JaxWsModuleMetaData;

/**
 * OASISCatalogApplicationBusListener will create and save a OASISCatalogManager as a bus extension, which will be used
 * to resolve the WSDL URL in the runtime
 */
public class OASISCatalogApplicationBusListener implements LibertyApplicationBusListener {

    private static final TraceComponent tc = Tr.register(OASISCatalogApplicationBusListener.class);

    @Override
    public void preInit(Bus bus) {}

    @Override
    public void initComplete(Bus bus) {

        if (bus == null) {
            return;
        }

        LibertyApplicationBus.Type busType = bus.getExtension(LibertyApplicationBus.Type.class);
        if (busType == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Unable to recognize the bus type from bus, OASISCatalogManager will not be created");
            }
            return;
        }

        ClassLoader appClassLoader = bus.getExtension(ClassLoader.class);
        JaxWsModuleMetaData moduleMetaData = bus.getExtension(JaxWsModuleMetaData.class);
        if (appClassLoader == null || moduleMetaData == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Unable to locate appClassLoader {0} and JaxWsModuleMetaData {1} from bus, OASISCatalogManager will not be created", appClassLoader, moduleMetaData);
            }
            return;
        }

        OASISCatalogManager catalogManager = new OASISCatalogManager();
        parseCatalogs(catalogManager, appClassLoader);

        //Always save an OASISCatalogManager instance as one bus extension, so CXF will not
        //do another round searching and creation
        bus.setExtension(catalogManager, OASISCatalogManager.class);

    }

    @Override
    public void preShutdown(Bus bus) {}

    @Override
    public void postShutdown(Bus bus) {}

    public void parseCatalogs(OASISCatalogManager catalogManager, ClassLoader cl)
    {

        Enumeration<URL> catalogURLs = null;
        try {
            catalogURLs = cl.getResources(JaxWsConstants.DEFAULT_SERVER_CATALOG_WEB_LOCATION);
            if (catalogURLs != null) {
                while (catalogURLs.hasMoreElements())
                {

                    catalogManager.loadCatalog(catalogURLs.nextElement());
                }
            }
            else
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Unable to locate catalog file URL from WEB-INF");
                }
            }

            catalogURLs = cl.getResources(JaxWsConstants.DEFAULT_SERVER_CATALOG_EJB_LOCATION);
            if (catalogURLs != null) {
                while (catalogURLs.hasMoreElements())
                {

                    catalogManager.loadCatalog(catalogURLs.nextElement());
                }
            }
            else
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Unable to locate catalog file URL from META-INF");
                }
            }

        } catch (IOException e) {
            //do-nothing
        }

    }
}
