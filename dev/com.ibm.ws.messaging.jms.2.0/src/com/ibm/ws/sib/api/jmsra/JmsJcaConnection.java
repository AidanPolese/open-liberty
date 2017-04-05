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
 * --------------- --------- -------- --------------------------------------------
 *                 06-May-03 pnickoll Original
 * 169897.5        07-Jul-03 pnickoll Updated to encompass the core API changes
 * 169626.6        21-Jul-03 pnickoll Updates from code review
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 203656          17-May-04 dcurrie  Code cleanup
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * 201972.4.1      05-Aug-04 pnickoll Add more coreSPI exceptions
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra;

//Sanjay Liberty Changes
//import javax.resource.ResourceException;
//import javax.resource.spi.IllegalStateException;

import javax.resource.ResourceException;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;

/**
 * Manages the lifecycle of an authenticated core connection and provides a 
 * factory method for the creation of <code>JmsJcaSession</code> objects.
 * There will be a one-to-one relationship between JMS connections and objects
 * implementing this interface.  This class is thread safe.
 */
public interface JmsJcaConnection {
   
   /**
    * Creates a <code>JmsJcaSession</code> that shares the core connection
    * from this connection.
    * 
    * @param transacted
    *            a flag indicating whether, in the absence of a global or
    *            container local transaction, work should be performed inside
    *            an application local transaction
    * @return the session
    * @throws ResourceException
    *             if the JCA runtime fails to allocate a managed connection
    * @throws IllegalStateException
    *             if this connection has been closed
    * @throws SIException
    *             if the core connection cannot be cloned
    * @throws SIErrorException
    *             if the core connection cannot be cloned
    */
    public JmsJcaSession createSession(boolean transacted)
            throws ResourceException, IllegalStateException, SIException,
            SIErrorException;
   
   /**
    * Returns the core connection created for, and associated with, this
    * connection.
    * 
    * @return the core connection
    * @throws IllegalStateException
    *             if this connection has been closed
    */
   public SICoreConnection getSICoreConnection() throws IllegalStateException;
   
   /**
    * Closes this connection, any open sessions created from it and its
    * associated core connection.
    * 
    * @throws SIErrorException
    *             if the associated core connection failed to close
    * @throws SIResourceException
    *             if the associated core connection failed to close
    * @throws SIIncorrectCallException
    *             if the associated core connection failed to close
    * @throws SIConnectionLostException
    *             if the associated core connection failed to close
    * @throws SIConnectionDroppedException
    *             if the associated core connection failed to close
    * @throws SIConnectionUnavailableException
    *             if the associated core connection failed to close
    */
   public void close() throws SIConnectionLostException,
            SIIncorrectCallException, SIResourceException, SIErrorException,
            SIConnectionDroppedException, SIConnectionUnavailableException;

}
