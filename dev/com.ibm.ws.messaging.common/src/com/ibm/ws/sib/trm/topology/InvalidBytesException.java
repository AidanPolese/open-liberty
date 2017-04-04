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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        030916 vaughton New MP/TRM interface
 * LIDB3706-5.262  050119 vaughton Serialisation
 * ============================================================================
 */

package com.ibm.ws.sib.trm.topology;

/**
 * This class represents an invalid byte exception and is thrown when
 * a LinkCellule or Messaging engine is constructed from a byte[] which
 * was not obtained from a LinkCellule or MessagingEngine object.
 */

public class InvalidBytesException extends Exception {

  private static final long serialVersionUID = -3027843595522849353l; // cf WAS60.SIB

  public InvalidBytesException (String s) {
    super(s);
  }

}
