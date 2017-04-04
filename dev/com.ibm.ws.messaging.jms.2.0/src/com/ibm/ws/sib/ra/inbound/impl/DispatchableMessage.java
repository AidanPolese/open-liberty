/**
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
 * Reason          Date      Origin   Description
 * --------------- --------- -------- ---------------------------------------
 * 342455          01-Feb-06 ajw      Original
 */
package com.ibm.ws.sib.ra.inbound.impl;

import java.util.Map;

import com.ibm.wsspi.sib.core.SIBusMessage;

/**
 * This object is used to contain the SIBus Message and its 
 * corresponding context to be used with the RAHandlers. The context
 * that is used when calling PRE_DISPATCH needs to be passed when calling
 * PRE_DELIVERY and POST_DELIVERY. 
 * 
 * This is distributed only as MessageTokens are used on zOS. 
 * 
 * @author ajw
 */
public final class DispatchableMessage 
{
  // The SIBusMessage that is contained within this object
  private SIBusMessage message;
  
  // The message context that goes with the corresponding SIBus Message
  private Map context;
  
  /**
   * Creates a DispatchableMessage object 
   * 
   * @param message the SIBus Message
   * @param context the message context
   */
  public DispatchableMessage(SIBusMessage message, Map context)
  {
    this.message = message;
    this.context = context;
  }
  
  /**
   * @return the SIBus Message
   */
  public SIBusMessage getMessage()
  {
    return message;
  }
  
  /**
   * @return the context associated with the SIBus Message
   */
  public Map getContext()
  {
    return context;
  }
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation
   */
  public final String toString() 
  {
    final SibRaStringGenerator generator = new SibRaStringGenerator(this);
    generator.addField("message", message);
    generator.addField("context", context);
    return generator.getStringRepresentation();
  }
}