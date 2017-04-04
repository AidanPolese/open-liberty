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
 * 218660.1        040817 susana   Original
 * 370507          060531 mphillip Adding flattenToXXX and getSystemMessageId methods
 * 370507.1        060811 mphillip Update javadoc of flattenToXXX to define max sizes
 * ============================================================================
 */
package com.ibm.wsspi.sib.core;

/**
 * SIMessageHandle is an opaque handle which unique identifies an SIBusMessage.
 * An SIMessageHandle can not be manipulated in any way.
 *
 */
public interface SIMessageHandle {
  
  /**
   *  Returns the SIMessageHandles System Message ID.
   *  
   *  @return String The SystemMessageID of the SIMessageHandle.
   */
  
  public String getSystemMessageId();
  

  /**
   *  Flattens the SIMessageHandle to a byte array.
   *  This flattened format can be restored into a SIMessageHandle by using
   *  com.ibm.wsspi.sib.core.SIMessageHandleRestorer.restoreFromBytes(byte [] data)
   *
   *  This byte[] will not have a length greater than 64. This allows SPI users to 
   *  plan sufficient space for storage of the byte[] should they need it. 
   *
   *  @see com.ibm.wsspi.sib.core.SIMessageHandleRestorer#restoreFromBytes(byte[])
   *  @return byte[] The flattened SIMessageHandle.
   */
  
  public byte[] flattenToBytes(); 

  /**
   *  Flattens the SIMessageHandle to a String.
   *  This flattened format can be restored into a SIMessageHandle by using
   *  com.ibm.wsspi.sib.core.SIMessageHandleRestorer.restoreFromString(String data)
   *  
   *  This String will not have a length greater than 128.  This allows SPI users to 
   *  plan sufficient space for storage of the String should they need it. 
   *
   *  @see com.ibm.wsspi.sib.core.SIMessageHandleRestorer#restoreFromString(String) 
   *  @return String The flattened SIMessageHandle.
   */
  
  public String flattenToString(); 

}
