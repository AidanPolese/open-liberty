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
 * ---------------  ------ -------- ------------------------------------------
 * 186484.23        140704 rjnorris Created
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;


/**
 * An interface class for reallocation of messages between streams
 */
public interface Reallocator
{
  public void reallocateMsgs(DestinationHandler destination, boolean allMsgs, boolean force) throws SIResourceException;
}
