/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.tx.embeddable;

import javax.transaction.xa.XAResource;

import com.ibm.ws.Transaction.RecoverableXAResource;

/**
 *
 */
public class RecoverableXAResourceAccessor {
    public static boolean isRecoverableXAResource(XAResource resource) {
        return resource instanceof RecoverableXAResource;
    }

    public static int getXARecoveryToken(XAResource resource) {
        return ((RecoverableXAResource) resource).getXARecoveryToken();
    }
}
