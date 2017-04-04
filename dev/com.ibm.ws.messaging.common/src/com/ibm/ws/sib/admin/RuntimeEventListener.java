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
 * 196675.1.1      040504 philip   Original
 * 206161.2        250105 nyoung   Admin infrastructure for Event Management.
 * 206161.2.2      310105 nyoung   Put isEventNotificationEnabled on JsMessagingEngine.
 * ============================================================================
 */
 
package com.ibm.ws.sib.admin;

import java.util.Properties;

/**
 * The implementors of this interface receive events from elsewhere
 * in JetStream for emission via the J2EE MBean notification mechanism.
 * 
 * @see com.ibm.ws.sib.admin.ControllableRegistrationService
 */
public interface RuntimeEventListener
{
  /**
   * Sends an event to the interface implementor.
   *  
   * @param me The MessagingEngine object associated with this Notification.
   * @param type The type of Notification to be propagated.
   * @param message The message to be propagated in the Notification.
   * @param properties The Properties associated with this Notification
   * type.
   */
  public void runtimeEventOccurred(JsMessagingEngine me,
                                   String type,
                                   String message,
                                   Properties properties);
}

