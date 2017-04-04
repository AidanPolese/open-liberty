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
 * Reason   Date   Origin   Description
 * -------- ------ -------- --------------------------------------------------
 * 181755   031103 auerbach Created file
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/** A trivial wrapper for primitive values of the IDREF type.  This is needed to
 * unambiguously distinguish between BINARY and IDREF when inspecting an object argument
 * (for example, in the coder for anySimpleType).  */
public class JMFAddress {
  private byte[] content;
  
  /** Construct a JMFAddress from a non-null byte[] */
  public JMFAddress(byte[] content) {
    this.content = content;
  }
  
  /** Copy constructor */
  public JMFAddress(JMFAddress toCopy) {
    content = (byte[]) toCopy.getContent().clone();
  }
  
  /** Retrieve the content as a byte[] */
  public byte[] getContent() {
    return content;
  }
}
