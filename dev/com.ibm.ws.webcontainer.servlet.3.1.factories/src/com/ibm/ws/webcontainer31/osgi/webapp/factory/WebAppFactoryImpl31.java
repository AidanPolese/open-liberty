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
package com.ibm.ws.webcontainer31.osgi.webapp.factory;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.csi.J2EENameFactory;
import com.ibm.ws.container.service.metadata.MetaDataService;
import com.ibm.ws.managedobject.ManagedObjectService;
import com.ibm.ws.webcontainer.osgi.webapp.WebApp;
import com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration;
import com.ibm.ws.webcontainer.osgi.webapp.WebAppFactory;
import com.ibm.ws.webcontainer31.osgi.webapp.WebApp31;
import com.ibm.wsspi.injectionengine.ReferenceContext;

/**
 *
 */
@Component(service = WebAppFactory.class, property = { "service.vendor=IBM" })
public class WebAppFactoryImpl31 implements WebAppFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.osgi.webapp.WebAppFactory#createWebApp(com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration, java.lang.ClassLoader,
     * com.ibm.wsspi.injectionengine.ReferenceContext, com.ibm.ws.container.service.metadata.MetaDataService, com.ibm.websphere.csi.J2EENameFactory)
     */
    @Override
    public WebApp createWebApp(WebAppConfiguration webAppConfig, ClassLoader moduleLoader, ReferenceContext referenceContext, MetaDataService metaDataService,
                               J2EENameFactory j2eeNameFactory, ManagedObjectService managedObjectService) {
        return new WebApp31(webAppConfig, moduleLoader, referenceContext, metaDataService, j2eeNameFactory, managedObjectService);
    }

}
