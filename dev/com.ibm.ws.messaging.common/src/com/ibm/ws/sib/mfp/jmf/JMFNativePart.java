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
 * 162584   030328 auerbach Original
 * 177749.1 030925 auerbach Add support for WDO over JMF
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/**
 * A JMFNativePart is a JMFPart that is explicitly in JMF format.  It is either a message
 * part or an entire message (in the latter case it is also a JMFMessage, which provides
 * additional methods).  It includes the JMFMessageData interface, which it shares with
 * JMFList.
 */

public interface JMFNativePart extends JMFPart, JMFMessageData {

  /**
   * Get the encoding JMFSchema associated with this JMFNativePart.  This is the
   * JMFSchema that was used to construct the underlying physical message.  This is the
   * same as the access schema unless a schema compatibility layer has been interposed.
   *
   * @return the encoding JMFSchema associated with this JMFNativePart
   */
  public JMFSchema getEncodingSchema();

  /**
   * Create a new, initially empty, JMFNativePart for a new Dynamic field with a
   * specified JMFSchema.  The JMFNativePart may be filled in by calling its set methods
   * directly, or by passing it to AbstractMessage.transcribe to transcribe a particular
   * AbstractMessage.  The JMFNativePart will be associated with the same JMF encoding
   * version as the JMFNativePart receiving the method call.
   *
   * @param schema the JMFSchema for the new JMFNativePart
   * @return the JMFNativePart
   */
  public JMFNativePart newNativePart(JMFSchema schema);

  /**
   * Create a new list of variant boxes with a given shape at a given position.
   *
   * @param accessor the accessor for the variant box list relative to this
   *    JMFNativePart's schema
   * @param val an array or Collection describing the desired shape.  The value is
   *    not actually assigned to the result in any way
   * @return the new variant box list, which is also installed in this JMFNativePart
   * @throws JMFSchemaViolationException
   * @throws JMFModelNotImplementedException
   * @throws JMFUninitializedAccessException
   * @throws JMFMessageCorruptionException
   */
   public JMFList createBoxList(int accessor, Object val)
     throws JMFSchemaViolationException, JMFModelNotImplementedException,
            JMFUninitializedAccessException, JMFMessageCorruptionException;
}
