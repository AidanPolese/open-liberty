/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.admin.RuntimeEvent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author nyoung
 */
public class MPRuntimeEvent implements RuntimeEvent
{
  private static TraceComponent tc =
    SibTr.register(
      MPRuntimeEvent.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  /** The NLS message for this event */
  private String _eventMessage;

  /** The type of this event */
  private String _eventType;

  /** The user data associated with this event */
  private Object _userData;

  public MPRuntimeEvent(String type,
                        String message,
                        Object userData)
    {
      if (tc.isEntryEnabled()) 
        SibTr.entry(tc, "MPRuntimeEvent", 
          new Object[] { type, message, userData } );
      
      _eventMessage = message;
      _eventType = type ;
      _userData = userData;
      
      if (tc.isEntryEnabled()) 
        SibTr.exit(tc, "MPRuntimeEvent", this);
    }  
  /**
   * Get the NLS message stored in this event
   */
  public String getMessage()
  {
    return _eventMessage;
  }

  /**
   * Set the NLS message stored in this event
   * @param newMessage
   */
  public void setMessage(String newMessage)
  {
    _eventMessage = newMessage;
  }

  /**
   * Get the type of this event.
   */
  public String getType()
  {
    return _eventType;
  }

  /**
   * Set the event type stored in this event
   * @param newType
   */
  public void setType(String newType)
  {
    _eventType = newType;
  }

  /**
   * Get the user data for this event
   */
  public Object getUserData()
  {
    return _userData;
  }

  /**
   * Set the user data stored in this event
   * @param newType
   */
  public void setUserData(Object newUserData)
  {
    _userData = newUserData;
  }

}
