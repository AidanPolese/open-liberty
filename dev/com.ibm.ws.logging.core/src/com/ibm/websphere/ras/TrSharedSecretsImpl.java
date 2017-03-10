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
package com.ibm.websphere.ras;

import com.ibm.ws.logging.internal.TrSharedSecrets;

/**
 * Package-private class to expose functionality internal to logging. The
 * instance of this class is created by TrSharedSecrets.
 */
class TrSharedSecretsImpl extends TrSharedSecrets {
    @Override
    public void addGroup(TraceComponent tc, String group) {
        tc.addGroup(group);
    }
}
