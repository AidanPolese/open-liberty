/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *
 * Change activity:
 *
 * Issue Date Name Description
 * ----------- ----------- -------- ------------------------------------
 *
 */

package com.ibm.ws.config.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ibm.ws.config.admin.internal.WSConfigAdminActivator;
import com.ibm.ws.config.xml.internal.WSConfigXMLActivator;

/**
 * 
 */
public class WSConfigActivator implements BundleActivator {

    WSConfigAdminActivator adminActivator = new WSConfigAdminActivator();
    WSConfigXMLActivator xmlActivator = new WSConfigXMLActivator();

    @Override
    public void start(BundleContext bc) {
        adminActivator.start(bc);
        xmlActivator.start(bc);
    }

    @Override
    public void stop(BundleContext bc) {
        xmlActivator.stop(bc);
        adminActivator.stop(bc);
    }
}
