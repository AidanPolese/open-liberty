/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

import javax.resource.ResourceException;
import javax.transaction.Synchronization;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public final class RRSNoTransactionWrapper extends NoTransactionWrapper implements Synchronization {

    private static final TraceComponent tc = Tr.register(RRSNoTransactionWrapper.class, J2CConstants.traceSpec, J2CConstants.messageFile);

    private String _hexId = "0x0"; // Null

    private final String hexId() {
        return "Id " + _hexId;
    }

    protected RRSNoTransactionWrapper() {
        _hexId = Integer.toHexString(this.hashCode());
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":<init>");
        }
    }

    /*
     * (non-Javadoc)
     * No-op for NoTransaction resources.
     * 
     * @return false to indicate this wrapper is not registered
     * 
     * @see com.ibm.ejs.j2c.NoTransactionWrapper#addSync()
     */
    @Override
    public boolean addSync() throws ResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":addSynch()");
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * No-op for NoTransaction resource.
     * 
     * @see com.ibm.ejs.j2c.NoTransactionWrapper#enlist()
     */
    @Override
    public void enlist() throws ResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":enlist()");
        }
    }

    /*
     * (non-Javadoc)
     * No-op for NoTransaction resource.
     * 
     * @see com.ibm.ejs.j2c.NoTransactionWrapper#delist()
     */
    @Override
    public void delist() throws ResourceException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":delist()");
        }
    }

    /**
     * Indicate whether this TranWrapper is RRS transactional
     */
    @Override
    public boolean isRRSTransactional() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":isRRSTransactional()=" + true);
        }
        return true;
    }

    @Override
    public void beforeCompletion() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":beforeCompletion()");
        }
    }

    @Override
    public void afterCompletion(int arg0) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, hexId() + ":afterCompletion()");
        }
    }

    @Override
    public String toString() {
        return "RRSNoTransactionWrapper@" + _hexId;
    }

}
