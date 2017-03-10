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
package com.ibm.ws.install;

import java.util.EventObject;

/**
 *
 */
public abstract class InstallEvent extends EventObject {

    private static final long serialVersionUID = -1991865252974291860L;

    public InstallEvent(String notificationType) {
        super(notificationType);
    }

}
