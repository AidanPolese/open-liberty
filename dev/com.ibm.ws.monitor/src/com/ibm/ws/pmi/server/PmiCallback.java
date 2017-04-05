// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/**
 * Interface exported to container runtime (csi package).
 * 
 * History
 */

/*
 * IBM Confidential OCO Source Material
 * 5639-D57 (C) COPYRIGHT International Business Machines Corp. 1998,1999,2000
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.pmi.server;

public interface PmiCallback {
    public PmiAttribute[] getPmiAttributes();

    public Object getRuntimeInfo();
}
