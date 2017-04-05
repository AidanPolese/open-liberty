/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 183467          26-Jan-04 dcurrie  Original
 * 197921.10       28-Apr-04 dcurrie  Add SPI JavaDoc tags
 * 226508          28-Apr-04 dcurrie  Remove SPI Javadoc tags
 * ============================================================================
 */

package com.ibm.wsspi.sib.ra;

import com.ibm.wsspi.sib.core.SITransaction;

/**
 * Transaction object passed on core SPI methods to indicate that the work
 * should be performed immediately when a <code>FactoryType</code> of
 * <code>RA_CONNECTION</code> has been specified on the
 * <code>SICoreConnectionFactorySelector</code>. This is required because,
 * for this factory type, a transaction parameter of <code>null</code>
 * indicates that the current container transaction (if any) should be used.
 */
public class SibRaAutoCommitTransaction implements SITransaction {

    /**
     * Singleton instance of this class.
     */
    public static final SibRaAutoCommitTransaction AUTO_COMMIT_TRANSACTION = new SibRaAutoCommitTransaction();

    /**
     * Private construtor to prevent instantiation.
     */
    private SibRaAutoCommitTransaction() {

        // Do nothing

    }

}
