/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.tx.jta;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public interface AbortableXAResource extends XAResource
{
    //
    //  Abort the connection associated with the transaction in which this resource was enlisted
    //
    public void abort(Xid xid);
}