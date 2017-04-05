/*
 * @start_prolog@
 * Version: @(#) 1.12 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/SendListener.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 09:56:49 [4/12/12 22:14:12]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2003, 2005
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
 * Creation        030521 prestona Original
 * F174602         030819 prestona Switch to using SICommsException
 * F181603.2       040119 prestona JFAP Segmentation
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199145         040812 prestona Fix Javadoc
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * Listener for asynchronous notification a send has completed.  This
 * listener can be supplied when invoking the send method on a
 * conversation.
 * @see com.ibm.ws.sib.jfapchannel.Conversation
 */
public interface SendListener
{
   /**
    * Invoked to notify the implementor that the data was sent successfully.
    * @param conversation The conversation the data was sent successfully over.
    */
	void dataSent(Conversation conversation);                  // F181603.2
	
   /**
    * Invoked if an error occurred when attempting to sent the data.
    * @param exception The exception that occurred.
    * @param conversation The conversation associated with the error that
    * occurred, or null if this not known.
    */
	void errorOccurred(SIConnectionLostException exception,    // F174602, F181603.2 
                       Conversation conversation);
	
}
