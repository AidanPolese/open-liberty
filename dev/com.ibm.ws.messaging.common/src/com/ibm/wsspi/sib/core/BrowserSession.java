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
 * 162915           080403 tevans   Make the Core API code look like the model
 * 166828           060603 tevans   Core MP rewrite
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 173765.0         200803 jroots   Addition of missing exceptions
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 179339.0         211203 jroots   Forward/reverse routing paths
 * 192759           090304 jroots   Milestone 7 Core SPI changes
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * 223986           170804 gatfora  Removal of SIErrorExceptions from method throws declarations
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */

package com.ibm.wsspi.sib.core;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 A BrowserSession is used to inspect the contents of a destination. In the 
 interests of domain-neutrality, it is possible to create a BrowserSession for a 
 TopicSpace. However, such a BrowserSession's next method will always return 
 null. It is anticipated that most calls to createBrowserSession will pass a 
 DestinationType of QUEUE, to cause the core API implementation to throw an 
 SIObjectNotFoundException if the named destination is in fact a TopicSpace.
 <p>
 It should be noted that the order in which messages are returned from 
 BrowserSession.next is undefined. It should also be noted that a browser is not 
 a consumer, and does not participate in consumer cardinality checks, for 
 example. The purpose of BrowserSession is to enable simple monitoring 
 applications to be written using the Core API.
 <p>
 This class has no direct security implications. Security checks are applied at
 the time that a BrowserSession is created, see the SICoreConnection
 class for details.
*/
public interface BrowserSession extends DestinationSession
{
	
  /**
   Returns a message that has been sent to the destination but not yet consumed, 
   or null if there are no more messages. Note that a null return value does 
   not imply that there are no messages that have been produced to the 
   destination and not yet consumed, but merely that there are no such messages
   visible in the part of the bus to which the application is connected.
   <p>
   The possibility of an SINotAuthorizedException being thrown by this method has
   been removed, there are no security implications with this method.
   
   @return a message from the destination
	 
   @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
   @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
  */
  public SIBusMessage next()
    throws SISessionUnavailableException, SISessionDroppedException,
           SIConnectionUnavailableException, SIConnectionDroppedException,
           SIResourceException, SIConnectionLostException,
           SINotAuthorizedException;
	  				
  /**
   Resets the BrowserSession, such that the current traversal of the destination 
   is abandoned, and a fresh traversal begun. A subsequent call to next() 
   returns the first message on the destination that is available to this 
   BrowserSession. If there are active consumers attached to the destination, 
   then this will likely be a different message to that first returned in the
   original traversal.
   
   @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
  */
  public void reset()
    throws SISessionUnavailableException, SISessionDroppedException,
           SIConnectionUnavailableException, SIConnectionDroppedException,
           SIResourceException, SIConnectionLostException;

}
