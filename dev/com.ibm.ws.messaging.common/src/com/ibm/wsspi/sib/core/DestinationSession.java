/*
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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 179339           211203 jroots   Forward/reverse routing paths
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * 223986           170804 gatfora  Removal of SIErrorExceptions from method throws declarations
 * 191835           260804 jroots   Modified Javadoc for close() method
 * 276259           130505 dware    Improve security related javadoc
 * 232270           021105 rjnorris Fix Javadoc for ConsumerSession.close
 * ============================================================================
 */

package com.ibm.wsspi.sib.core;

import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 DestinationSession is the parent interface for the Core SPI interfaces that are
 used to interact with a destination, namely ProducerSession, ConsumerSession,
 and BrowserSession.
 <p>
 This class has no direct security implications. AbstractConsumerSession,
 BrowserSession and ProducerSession extend this class, see those classes for
 further security implications.
*/
public interface DestinationSession {

  /**
   Closes the DestinationSession. Any subsequent attempt to call methods on the 
   DestinationSession will result in an SISessionUnavailableException.
   Calling close on an already closed DestinationSession has no effect.
   <p>
   Normally, when close is called against a ConsumerSession that has an 
   AsynchConsumerCallback regstered, and if consumeMessages is currently being
   executed, the close method blocks until control is returned from 
   consumeMessages. However, if close is called from within the consumeMessages 
   method implementation, then the close is effective immediately:, any remaining
   locked messages are unlocked, and no further work can be done using the 
   AsynchConsumerCallback's LockedMessageEnumeration.
   Closing a ConsumerSession will close any associated BifurcatedConsumerSessions
   thus unlocking any locked messages ( see BifurcatedConsumerSession ).
   <p>
   Once a DestinationSession has been closed, it cannot be used to do further
   work. If any transactions remain uncompleted that include work done using the
   DestinationSession, then their state is unaltered by the close of the
   DestinationSession, and the transactions should be committed or rolled back
   by the Core SPI user in the normal way. (However, if the whole 
   SICoreConnection is closed, any SIUncoordinatedTransaction created using the
   SICoreConnection are automatically rolled back.)    

   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
  */
  public void close()
    throws SIResourceException, SIConnectionLostException,
           SIConnectionDroppedException;
	
  /** 
   Returns the connection from which this BrowserSession was created, and with 
   which it is associated.
      
   @return the SICoreConnection from which the BrowserSession was created
	 
   @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
  */
  public SICoreConnection getConnection()
    throws SISessionUnavailableException, SISessionDroppedException,
           SIConnectionUnavailableException, SIConnectionDroppedException;

  /** 
   Returns the address of the destination to which the DestinationSession is 
   (or, in the case of a closed DestinationSession, was) attached.
      
   @return the SIDestinationAddress for the destination
	 
  */
  public SIDestinationAddress getDestinationAddress();
}
