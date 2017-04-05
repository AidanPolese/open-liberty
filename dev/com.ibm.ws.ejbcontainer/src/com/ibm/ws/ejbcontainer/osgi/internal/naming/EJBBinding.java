/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.naming;

import com.ibm.ejs.container.HomeRecord;

/**
 * Represents EJB object data held for lookup
 */
public class EJBBinding {
    public final HomeRecord homeRecord;
    public final String interfaceName;

    /**
     * The interface index, or -1 for home.
     */
    public final int interfaceIndex;

    public final boolean isLocal;

    /**
     * Create EJB binding data
     *
     * @param interfaceIndex the business interface index, or -1 for home
     */
    public EJBBinding(HomeRecord homeRecord, String interfaceName, int interfaceIndex, boolean local) {
        this.homeRecord = homeRecord;
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.isLocal = local;
    }

    boolean isHome() {
        return interfaceIndex == -1;
    }

    @Override
    public String toString() {
        return super.toString() +
               '[' + homeRecord.getJ2EEName() +
               ", " + interfaceName +
               ", " + interfaceIndex +
               ']';
    }
}
