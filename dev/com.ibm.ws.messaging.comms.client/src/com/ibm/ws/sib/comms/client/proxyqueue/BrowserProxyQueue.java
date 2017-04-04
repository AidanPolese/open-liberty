/*
 * @start_prolog@
 * Version: @(#) 1.16 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/BrowserProxyQueue.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 07:50:53 [4/12/12 22:14:05]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2004, 2005
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030612 prestona Original
 * F171893         030721 prestona Add BrowserSession support on client.
 * F174602         030819 prestona Switch to using SICommsException.
 * f177889         030929 mattheg  Core API M4 completion
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * D249096         050129 prestona Fix proxy queue synchronization
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client.proxyqueue;

import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.comms.client.BrowserSessionProxy;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.mfp.MessageDecodeFailedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 * A proxy queue for browser sessions.
 */
public interface BrowserProxyQueue extends ProxyQueue
{ 
   /**
    * Returns the next un-browsed message from this
    * proxy queue.  A value of null is returned if there
    * is no next message.
    * 
    * @return JsMessage
    */
   JsMessage next() 
      throws MessageDecodeFailedException, SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, 
             SIErrorException,
             SINotAuthorizedException;
   
   /**
    * Associates a browser session with this proxy queue.  The
    * session must be the session the proxy queue is being used
    * by. 
    */
   void setBrowserSession(BrowserSessionProxy browserSession);
   
   /**
    * Resets the browser session.  In proxy queue terms, this sends 
    * a reset request to the ME and purges the contents of the queue.
    */
   void reset()
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, 
             SIErrorException;

   /**
    * Closes the browser session proxy queue.
    * @throws SIResourceException
    * @throws SIConnectionLostException
    * @throws SIErrorException
    * @throws SIConnectionDroppedException
    */
   void close()                                             // D249096
   throws SIResourceException, 
          SIConnectionLostException, 
          SIErrorException, 
          SIConnectionDroppedException;
}
