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
 * 159093           070303 jroots   Original
 * 162915           080403 tevans   Make the Core API code look like the model
 * 163636           160403 tevans   Upgrade to model version 0.4
 * 166828           060603 tevans   Core MP rewrite
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite     
 * 199140           180804 gatfora  Cleanup javadoc      
 * 276259           130505 dware    Improve security related javadoc
 * 354500           140306 gatfora  Add the missing t
 * ===========================================================================
 */

package com.ibm.wsspi.sib.core;

/**
 AsynchConsumerCallback is an interface that can be implemented by the client 
 application (or API layer), in order to receive messages asynchronously. The 
 consumeMessages method is called when messages are available for receipt by the 
 consumer respresented by the ConsumerSession with which the 
 AsynchConsumerCallback has been registered using 
 ConsumerSession.registerAsynchConsumerCallback.
 <p>
 This class has no security implications.
 
 
 @see com.ibm.wsspi.sib.core.ConsumerSession#registerAsynchConsumerCallback
 @see com.ibm.wsspi.sib.core.ConsumerSession#deregisterAsynchConsumerCallback
*/
public interface AsynchConsumerCallback {
	
  /**
   The consumeMessages method is called by the Core API implementation to 
   deliver messages asynchronously to an application (or API layer). Messages 
   are locked to the consumer, and cannot be delivered to other consumers; they 
   must be either deleted or unlocked by the consumer to which they have been 
   delivered, unless unlocked implicitly as a result of closing the consumer, 
   shutdown of the Messaging Engine, or returning from consumeMessages without 
   having viewed the messages using LockedMessageEnumeration.nextLocked().
	 <p>
   Note that if the consumer views some but not all of the locked messages, 
   then only those that have not been seen are implicitly unlocked on return 
   from consumeMessages.
   <p>
   The signature of the consumeMessages method does not declare any exceptions. 
   However, if the implementation throws a Throwable, the Core API 
   implementation will catch it and increment the redeliveryCount for the 
   message(s) that were not consumed.
   <p>
   It should be noted that any messages not unlocked or deleted by the 
   AsynchConsumerCallback when control is returned from consumeMessages remain 
   locked to the ConsumerSession. The unlockSet, unlockAll, and deleteSet 
   methods on  ConsumerSession can be used to process the messages at some
   later time.
   <p>
   Any exception thrown by the implementation of this method will be caught and 
   delivered to any registered SICoreConnectionListeners.
   
   @param lockedMessages
   
   @throws Throwable
   
  */
  public void consumeMessages(
    LockedMessageEnumeration lockedMessages) throws Throwable;
}

