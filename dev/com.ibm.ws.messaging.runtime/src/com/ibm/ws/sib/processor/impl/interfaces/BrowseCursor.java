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
 * SIB0002.mp.3     270605 tpm      RMQ Browser Session support
 * 355323           220306 tevans   RMQSessionDroppedException handling
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;

/**
 * A cursor used to browse messages on either an MQ queue or a
 * MessageStore ItemStream.
 */
public interface BrowseCursor
{
  
  /**
   * Reply the next {@link JsMessage} that matches the filter specified when
   * the cursor was created.
   * Method next.
   * @return the next matching {@link JsMessage}, or null if there is none.
   * @throws SISessionDroppedException 
   */
  public JsMessage next() throws SIResourceException, SISessionDroppedException;
  
  /**
   * Declare that this cursor is no longer required.  This allows the underlying
   * resource to release resources.  
   * Once the cursor has been released it should not be used again.
   */
  public void finished() throws SISessionDroppedException;
  

}
