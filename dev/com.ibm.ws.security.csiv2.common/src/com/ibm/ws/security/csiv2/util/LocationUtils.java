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
package com.ibm.ws.security.csiv2.util;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsLocationConstants;

@Component
public class LocationUtils {

    private static final TraceComponent tc = Tr.register(LocationUtils.class);

    private static WsLocationAdmin locationAdmin;

    public LocationUtils() {}

    /* constructor for the unit test */
    @SuppressWarnings("static-access")
    public LocationUtils(WsLocationAdmin locationAdmin) {
        this.locationAdmin = locationAdmin;
    }

    @SuppressWarnings("static-access")
    @Reference
    protected synchronized void setLocationAdmin(WsLocationAdmin locationAdmin) {
        this.locationAdmin = locationAdmin;
    }

    public static boolean isServer() {
        return locationAdmin.resolveString(WsLocationConstants.SYMBOL_PROCESS_TYPE).equals(WsLocationConstants.LOC_PROCESS_TYPE_SERVER);
    }
}