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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 184035           231104 tevans   New MPAlarmManager for z/OS
 * 529626           160608 dware    Add a cancel method 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.utils.am;

import com.ibm.ejs.util.am.AlarmListener;
import com.ibm.ws.sib.utils.SIBUuid12;

public interface GroupAlarmListener extends AlarmListener
{
  /**
   * Called at the begining of a group callback.
   * 
   * @param firstContext The first alarm context in the group
   */
  public void beginGroupAlarm(Object firstContext);
  
  /**
   * Called for each additional alarm in the group.
   * 
   * @param nextContext the alarm context object
   */
  public void addContext(Object nextContext);
  
  /**
   * Return the unique identifier for this alarm group
   * 
   * @return the unique identifier for this alarm group
   */
  public SIBUuid12 getGroupUuid();
  
  /**
   * Called by the alarm manager to inform that the alarm has been cancelled
   *
   */
  public void cancel();
}
