/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * SIB0105.mp.6     210607 nyoung   MQLink Controllable changes
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.exceptions.SIMPException;
import com.ibm.ws.sib.processor.exceptions.SIMPInvalidRuntimeIDException;

public interface SIMPMQLinkQueuePointControllable extends
  SIMPLocalQueuePointControllable {

  /**
   * Get the iterator over the messages on this MQLink transmit queue point.
   * @return Iterator over messages
   */
  SIMPIterator getTransmitMessageIterator();
  
  /**
   * Get a message with a given id from this MQLink transmit queue point
   * @param id
   * @return transmit message
   * @throws SIMPException 
   * @throws SIMPControllableNotFoundException 
   * @throws SIMPInvalidRuntimeIDException 
   */
  SIMPMQLinkTransmitMessageControllable getTransmitMessageByID( String id ) 
    throws SIMPInvalidRuntimeIDException, 
           SIMPControllableNotFoundException, 
           SIMPException;
}
