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
package com.ibm.ws.webcontainer.osgi.webapp;

import com.ibm.websphere.csi.J2EENameFactory;
import com.ibm.ws.container.service.metadata.MetaDataService;
import com.ibm.ws.managedobject.ManagedObjectService;
import com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration;
import com.ibm.wsspi.injectionengine.ReferenceContext;

/**
 *
 */
public interface WebAppFactory {

    WebApp createWebApp(WebAppConfiguration webAppConfig,
                  ClassLoader moduleLoader,
                  ReferenceContext referenceContext,
                  MetaDataService metaDataService,
                  J2EENameFactory j2eeNameFactory,
                  ManagedObjectService managedObjectService);
}
