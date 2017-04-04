/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.adapter;

import javax.resource.spi.ManagedConnectionFactory;
import javax.transaction.xa.XAResource;

import org.osgi.framework.Version;

import com.ibm.ws.resource.ResourceRefInfo;

/**
 * WebSphere Application Server extensions to the ManagedConnectionFactory interface.
 */
public abstract class WSManagedConnectionFactory implements ManagedConnectionFactory {
    private static final long serialVersionUID = -8501184761741716982L;

    /**
     * Returns the default type of branch coupling that should be used for BRANCH_COUPLING_UNSET.
     *
     * @return the default type of branch coupling: BRANCH_COUPLING_LOOSE or BRANCH_COUPLING_TIGHT.
     *         If branch coupling is not supported or it is uncertain which type of branch coupling is default,
     *         then BRANCH_COUPLING_UNSET may be returned.
     * @see ResourceRefInfo
     */
    public int getDefaultBranchCoupling() {
        return ResourceRefInfo.BRANCH_COUPLING_UNSET;
    }

    /**
     * Indicates whether or not this managed connection factory is RRS-enabled.
     *
     * @return true if RRS-enabled, otherwise false.
     */
    public boolean getRRSTransactional() {
        return false;
    }

    /**
     * Indicates whether or not this managed connection factory supports thread identity.
     *
     * @return Thread Identity Support: Either "ALLOWED", "REQUIRED", or "NOTALLOWED"
     */
    public String getThreadIdentitySupport() {
        return "NOTALLOWED";
    }

    /**
     * Indicates whether or not we should "synch to thread" for the
     * allocateConnection, i.e., push an ACEE corresponding to the current java
     * Subject on the native OS thread.
     *
     * @return true if we should "synch to thread", otherwise false.
     */
    public boolean getThreadSecurity() {
        return false;
    }

    /**
     * Returns the xa.start flags (if any) to include for the specified branch coupling.
     * XAResource.TMNOFLAGS should be returned if the specified branch coupling is default.
     * -1 should be returned if the specified branch coupling is not supported.
     *
     * @param couplingType one of the BRANCH_COUPLING_* constants
     * @return the xa.start flags (if any) to include for the specified branch coupling.
     */
    public int getXAStartFlagForBranchCoupling(int couplingType) {
        if (couplingType == ResourceRefInfo.BRANCH_COUPLING_UNSET || couplingType == getDefaultBranchCoupling())
            return XAResource.TMNOFLAGS;
        else
            return -1;
    }

    /**
     * Indicated the level of JDBC support for the ManagedConnectionFactory
     *
     * @return The jdbc version which the ManagedConnectionFactory supports.
     */
    public Version getJDBCRuntimeVersion() {
        return new Version(4, 0, 0);
    }

    public boolean isPooledConnectionValidationEnabled() {
        return false;
    }
}