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
 * LIDB2117        041103 vaughton Original
 * LIDB3706-5.262  050119 vaughton Serialisation
 * ============================================================================
 */

package com.ibm.ws.sib.trm.links;

/**
 * The LinkException is thrown when a link exception condition occurs.
 */

public final class LinkException extends Exception {

  private static final long serialVersionUID = -8602898460062457634l; // cf WAS60.SIB

  public LinkException (String s) {
    super(s);
  }

}
