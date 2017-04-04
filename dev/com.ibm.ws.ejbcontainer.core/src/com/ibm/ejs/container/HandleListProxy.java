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
package com.ibm.ejs.container;

import com.ibm.ejs.j2c.HandleListInterface;

class HandleListProxy implements HandleListInterface {
    public static final HandleListInterface INSTANCE = null;

    @Override
    public void parkHandle() {}

    @Override
    public void reAssociate() {}
}
