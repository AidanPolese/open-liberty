/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0105.mp.5     071106 cwilkin  Original
 * 536566           060708 dware    Add reason prioritisation
 * 535791           220708 nyoung   Update Health State of link when it is blocked or unblocked
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.processor.runtime.HealthState;

public interface HealthStateListener extends HealthState {
  
  public static final Integer OK_STATE = Integer.valueOf(0);
  public static final Integer ACK_EXPECTED_STATE = Integer.valueOf(1);   // Sender reason
  public static final Integer BLOCKED_STREAM_STATE = Integer.valueOf(2); // Sender reason
  public static final Integer STREAM_FULL_STATE = Integer.valueOf(3);    // Sender reason
  public static final Integer NACK_RECEIVED_STATE = Integer.valueOf(4);  // Sender reason
  public static final Integer CONNECTION_UNAVAILABLE_STATE = Integer.valueOf(5);
  public static final Integer GAP_DETECTED_STATE = Integer.valueOf(6);   // Receiver reason
  public static final Integer MSG_LOST_ERROR_STATE = Integer.valueOf(7); // Receiver reason
  public static final Integer ERROR = Integer.valueOf(8);
  public static final Integer BLOCKED_TARGET_STREAM_STATE = Integer.valueOf(9); // Receiver reason
  
  // Each reason (for a comparable state) has a pecking order, this array defines that order.
  // A reason's relative order is represented by the value in the array at the position of the
  // reason (the greater the value, the higher the reason, i.e. worse).
  public static final int[][] orderedReasons = {{0,0,0,0,0,1,0,0,2,0},  // HealthState.RED reasons
                                                {0,3,4,2,1,0,2,3,0,1},  // HealthState.AMBER reasons
                                                {1,0,0,0,0,0,0,0,0,0}}; // HealthState.GREEN reasons
  
  public void updateHealth(Integer key, int state);
  
  public void updateHealth(Integer key, int state, String[] inserts);
  
  public void register(Integer key);
  
  public void deregister(Integer key);
  
}
