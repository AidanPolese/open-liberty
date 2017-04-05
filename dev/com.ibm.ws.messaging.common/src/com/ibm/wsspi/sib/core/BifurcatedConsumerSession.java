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
 * ---------------  ------ -------- -------------------------------------------------
 * 195758.0         050404 jroots   Milestone 7.5 Core SPI changes
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite 
 * 223986           170804 gatfora  Removal of SIErrorExceptions from method throws declarations
 * 219476.0         240804 jroots   Consolidated Z3 Core SPI changes
 * 276259           130505 dware    Improve security related javadoc
 * 232270           021105 rjnorris Fix Javadoc for ConsumerSession.close
 * 337346           110106 gatfora  Remove unmappable chars.
 * ===========================================================================
 */

package com.ibm.wsspi.sib.core;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SIMessageNotLockedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 A BifurcatedConsumerSession is intended for use with a regular ConsumerSession
 in asynchronous mode in order to read and delete messages from a different
 context to that which receives the initial notification (via 
 AsynchConsumerCallback.consumeMessages) of message availability). A
 BifurcatedConsumerSession is created with 
 SICoreConnection.createBifurcatedConsumerSession, using the identifier of the
 original ConsumerSession.
 <p>
 There are some subtleties to the way locked messages are managed, which affects 
 what happens to them when ConsumerSession.close() and 
 BifurcatedConsumerSession.close() are called, as well as the accessibility of 
 messages from the original ConsumerSession: When a message is locked and 
 delivered to an asynchronous consumer, it is "owned" by the corresponding 
 ConsumerSession. In the absence of bifurcated consumers, then when the 
 ConsumerSession is closed, any remaining locked messages - that is, those that 
 have not been imported into a transaction, unlocked, or deleted - are 
 automatically unlocked. In the presence of bifurcated consumers, there are two 
 cases: If the bifurcated consumer reads a message with readSet(), then the 
 ownership of the message is transferred to the BifurcatedConsumerSession.
 In this case, the  original ConsumerSession no longer has access to the message: it 
 cannot, for example, delete it using deleteSet(). If the bifurcated consumer does not 
 call readSet() (as would be the case, for example, if the entire message content 
 were communicated out-of-band between the two parts of the consumer), then the 
 message ownership remains with the original ConsumerSession. This notion of 
 transferral of message ownership is necessary in order to be able to optimize 
 the lookup of messages by the ids in (for example) the implementation of 
 deleteSet.
 <p>
 Closing a BifurcatedConsumerSession will unlock any locked messages it currently
 owns. Closing the original ConsumerSession will close any associated
 BifurcatedConsumerSessions, thus unlocking any locked messages.  
 <p>
 This class has no direct security implications. Security checks are applied at
 the time that a BifurcatedConsumerSession is created, see the SICoreConnection
 class for details.
*/
public interface BifurcatedConsumerSession extends AbstractConsumerSession
{
  
  /**
   This method can be used to read the content of messages that are locked
   to the ConsumerSession whose ID was used to create this 
   BifurcatedConsumerSession. 

   @param msgHandles identifies the messages to be deleted
   
   @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
   @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
   @throws com.ibm.websphere.sib.exception.SIMessageNotLockedException
  */  
  public SIBusMessage[] readSet(
      SIMessageHandle[] msgHandles) 
    throws SISessionUnavailableException, SISessionDroppedException,
           SIConnectionUnavailableException, SIConnectionDroppedException,
           SIResourceException, SIConnectionLostException, 
           SIIncorrectCallException,
           SIMessageNotLockedException;

  /**
   This method can be used to read and delete a set of messages using a single
   round-trip from the client to the Messaging Engine. In all other respects,
   it behaves as though readSet followed by deleteSet had been called. 

   @param msgHandles identifies the messages to be deleted
   @param tran the transaction under which the delete is to occur
   
   @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
   @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
   @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
   @throws com.ibm.websphere.sib.exception.SIMessageNotLockedException
  */  
  public SIBusMessage[] readAndDeleteSet(
      SIMessageHandle[] msgHandles,
      SITransaction tran)
    throws SISessionUnavailableException, SISessionDroppedException,
           SIConnectionUnavailableException, SIConnectionDroppedException,
           SIResourceException, SIConnectionLostException, SILimitExceededException,
           SIIncorrectCallException,
           SIMessageNotLockedException;

}
