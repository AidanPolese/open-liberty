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
 * SIB0002.mp.10    250705 tevans   RMQ Sessions
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * Internal extended sib.core DestinationSession interface
 */
public interface MPDestinationSession
{  
  public SIBUuid12 getUuid();
}
 
