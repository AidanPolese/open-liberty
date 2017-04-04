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
 * --------------- ------    -------- --------------------------------------------
 *                 06-May-03 pnickoll Original
 * 169897.5        07-Jul-03 pnickoll Updated to encompass the core API changes
 * 178860          06-Oct-03 djhoward Change close to throw SICoreException 
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 203656          17-May-04 dcurrie  Code cleanup
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra;

//Sanjay Liberty Changes
//import javax.resource.ResourceException;
//import javax.resource.spi.IllegalStateException;
//import javax.resource.spi.LocalTransactionException;

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransactionException;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * Session interface between the JMS API and resource adapter. Provides access
 * to <code>SITransaction</code> objects representing container and
 * application local transactions.
 */
public interface JmsJcaSession {

    /**
     * A convenience method that returns the core connection associated with
     * this session's connection.
     * 
     * @return the core connection
     * @throws IllegalStateException
     *             if this session has been closed or invalidated
     */
    public SICoreConnection getSICoreConnection() throws IllegalStateException;

    /**
     * Returns the current core transaction object for this session. This may
     * represent a global transaction started by the container, a local
     * transaction started for the application or by the container, or may be
     * null.
     * 
     * @return the current transaction, if any
     * @throws IllegalStateException
     *             if this session has been closed or invalidated. This can also
     *             be thrown if the connection is not associated with a managed
     *             connection and the connection manager does not support lazy
     *             enlistment
     * @throws ResourceException 
     *             if a lazy enlist was required and failed 
     * @throws SIException
     *             if the call resulted in a failed attempt to start a
     *             transaction
     * @throws SIErrorException
     *             if the call resulted in a failed attempt to start a
     *             transaction
     */
    public SITransaction getCurrentTransaction() throws IllegalStateException,
            ResourceException, SIException, SIErrorException;

    /**
     * Returns the transacted flag specified on creation of this session. This
     * flag indicates whether, in the absence of a global or container local
     * transaction, work should be performed inside an application local
     * transaction.
     * 
     * @return the transacted flag
     * @throws IllegalStateException
     *             if this session has been closed or invalidated
     */
    public boolean getTransacted() throws IllegalStateException;

    /**
     * Commits the current application local transaction.
     * 
     * @throws IllegalStateException
     *             if this session has been closed. This can also
     *             be thrown if the connection is not associated with a managed
     *             connection and the connection manager does not support lazy
     *             enlistment
     * @throws LocalTransactionException
     *             if there is currently no local transaction for this session
     * @throws ResourceException
     *             if a lazy enlist was required and failed 
     * @throws SIException
     *             if the commit fails
     * @throws SIErrorException
     *             if the commit fails
     */
    public void commitLocalTransaction() throws IllegalStateException,
            LocalTransactionException, ResourceException, SIException,
            SIErrorException;

    /**
     * Rolls back the current application local transaction.
     * 
     * @throws IllegalStateException
     *             if this session has been closed. This can also
     *             be thrown if the connection is not associated with a managed
     *             connection and the connection manager does not support lazy
     *             enlistment
     * @throws LocalTransactionException
     *             if there is currently no local transaction for this session
     * @throws ResourceException
     *             if a lazy enlist was required and failed 
     * @throws SIException
     *             if the rollback fails
     * @throws SIErrorException
     *             if the rollback fails
     */
    public void rollbackLocalTransaction() throws IllegalStateException,
            LocalTransactionException, ResourceException, SIException, SIErrorException;

    /**
     * Closes this session.
     * 
     * @throws SIErrorException
     * @throws SIResourceException
     * @throws SIIncorrectCallException
     * @throws SIConnectionLostException
     */
    public void close() throws SIConnectionLostException,
            SIIncorrectCallException, SIResourceException, SIErrorException;
    
}
