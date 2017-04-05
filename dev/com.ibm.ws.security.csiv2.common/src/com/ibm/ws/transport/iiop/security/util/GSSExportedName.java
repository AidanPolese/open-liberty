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
package com.ibm.ws.transport.iiop.security.util;

import java.io.Serializable;

/**
 *
 */
public final class GSSExportedName implements Serializable {

    private final String name;
    private final String oid;

    public GSSExportedName(String name, String oid) {
        this.name = name;
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public String getOid() {
        return oid;
    }

}
