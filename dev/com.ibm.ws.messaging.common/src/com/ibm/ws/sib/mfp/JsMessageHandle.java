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
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import java.io.Serializable;

import com.ibm.wsspi.sib.core.SIMessageHandle;
import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * JsMessageHandle is the internal interface for Jetstream components to
 * access an SIMessageHandle.
 */
public interface JsMessageHandle extends SIMessageHandle, Serializable {

  /**
   *  Get the value of the SystemMessageSourceUuid field for the message
   *  represented by the JsMessageHandle.
   *
   *  @return A SIBUuid8 containing the source UUID of the message.
   */
  public SIBUuid8 getSystemMessageSourceUuid();

  /**
   *  Get the value of the SystemMessageValue field for the message
   *  represented by the JsMessageHandle.
   *
   *  @return A long containing the identifier value of the message.
   */
  public long getSystemMessageValue();

}
