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
 * Reason     Date   Origin   Description
 * --------   ------ -------- --------------------------------------------------
 * 162584     280303 baldwint Original
 * 163400     140403 baldwint JMF improvements
 * 177749.1.1 030926 auerbach Better encapsulation support in JMFI motivated by WDO
 * 424474     070312 susana   Use of master needs to take account of compatible messages
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/**
 * A JMFEncapsulationManager is responsible for encapsulating and de-encapsulating the
 * messages of a particular Message Model in JMF messages, and for transcribing to/from
 * JMF format for a particular Message Model.
 */

public interface JMFEncapsulationManager {

  /**
   * Produces an JMFEncapsulation by de-encapsulating the contents of a Dynamic field
   * within a JMF message
   *
   * @param frame the byte array within which the encapsulated JMFEncapsulation's
   * serialized bytes will be found
   * @param offset the offset within the frame argument where the JMFEncapsulation's
   * serialized bytes begin.
   * @param length the length of the area within the frame containing the
   * @param msg the JMF Message in which this JMFPart appears.  The JMFEncapsulationManager
   *   may use non-destructive JMFI calls to extract any needed meta-data from this message.
   *   The JMFEncapsulationManager should not mutate the message.
   * @return the JMFEncapsulation resulting from de-encapsulating the supplied bytes, using
   * the particular view implemented by this JMFEncapsulationManager.
   * @exception JMFMessageCorruptionException if the message could not be de-encapsulated
   * due to message corruption
   */
  public JMFEncapsulation deencapsulate(byte[] frame, int offset, int length, JMFMessageData msg)
                                                                  throws JMFMessageCorruptionException;
}
