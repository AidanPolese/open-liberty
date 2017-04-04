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
 * 180483.3        281003 sbhola   initial implementation
 * 180483.4        070104 sbhola   improvements
 * SIB0113a.mp.3   010807 vaughton Class rename
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl;

// Import required classes.

/**
 * The information kept for each value tick in a stream
 */
public final class AORequested
{
  public final JSRemoteConsumerPoint aock;
  public final long startTime; // used to calculate the waitTime
  public final long expiryInterval; // expiry occurs at approximately startTime+expiryInterval
  public final long tick;

  /** true if in the process of inserting a value message for this tick, else false
   * initial state: false
   * final state: false, true
   * possible state transitions: false -> true
   */
  public boolean inserting;

  public AORequested(JSRemoteConsumerPoint aock, long startTime, long expiryInterval, long tick)
  {
    this.aock = aock;
    this.startTime = startTime;
    this.inserting = false;
    this.expiryInterval = expiryInterval;
    this.tick = tick;
  }
}
