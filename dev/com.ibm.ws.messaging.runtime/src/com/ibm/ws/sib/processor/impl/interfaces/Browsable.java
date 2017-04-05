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
 * 166828           060603 tevans   Core MP rewrite
 * 170424           240603 tevans   Additional comments
 * 169897.1         300603 tevans   Updates for Milestone 3 Core API
 * 170751.1         150703 tevans   new message store interface 
 * 172279.1         220703 gatfora  Completion of msg store interface
 * 175766           030903 gatfora  Package restructuring
 * 176106           080903 gatfora  Move to new MessageStore Cursor implementation
 * 181796.1         051103 gatfora  New MS5 Core API
 * 199212           210404 gatfora  Fixing javadoc
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * SIB0002.mp.3     270605 tpm      RMQ Browser Session support
 * SIB0002.mp.15    160805 tevans   Transactional PEV Producers and Consumers
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISelectorSyntaxException;

/**
 * Browsable should be implemented by any class which can provide
 * a BrowseCursor for its messages.
 * 
 * @author tevans
 * @see com.ibm.ws.sib.msgstore.NonLockingCursor
 * @see com.ibm.ws.sib.msgstore.ItemStream
 */
public interface Browsable {
  
  /**
   * Get a Cursor on the message point.
   * 
   * @param selectionCriteria Limits the messages returned based on
   * a selection criteria.
   * If the cursor should return all items, selectionCriteria should be null.
   * @return A Cursor
   * @throws SIResourceException Thrown if there is a problem getting a Cursor
   * from the messageStore or from MQ.
   * @throws SIDiscriminatorSyntaxException
   */
  public BrowseCursor getBrowseCursor(SelectionCriteria selectionCriteria) throws SIResourceException, SISelectorSyntaxException, SIDiscriminatorSyntaxException;
  
  
}
