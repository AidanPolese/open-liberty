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
package com.ibm.ws.security.authentication.principals;

import java.io.Serializable;
import java.security.Identity;

/**
 * Used by EJB container API for getCallerIdentity
 */
@SuppressWarnings("deprecation")
public class WSIdentity extends Identity implements Serializable {

    /**  */
    private static final long serialVersionUID = 6151616852790963219L;

    public WSIdentity(String name) {
        super(name);
    }
}
