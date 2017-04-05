/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

/**
 * Stub - Liberty does not support DissociatableManagedConnection
 */
public class HandleList implements HandleListInterface {
    @Override
    public void parkHandle() {}

    @Override
    public void reAssociate() {}

    public void close() {}

    public void componentDestroyed() {}
}
