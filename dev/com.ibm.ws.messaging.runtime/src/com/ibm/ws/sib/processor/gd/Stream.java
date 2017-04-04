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
 * ---------------  ------ -------- --------------------------------------------
 * 186256.1         301203 tevans   Refactor GD/stream/flush code
 * 199212           210404 gatfora  Fixing javadoc
 * 186484.21        010704 cwilkin  Update Stream interface
 * 186484.22        090704 cwilkin  Lookup transmit msgs by id
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * ============================================================================
 */

package com.ibm.ws.sib.processor.gd;

import java.util.List;

import com.ibm.websphere.sib.exception.SIException;


public interface Stream
{
  /**
   * @return the completed prefix
   */
  public long getCompletedPrefix();
  
  /**
   * @return the underlying StateStream 
   */
  public StateStream getStateStream();
  
  /**
   * Remove a message from the stream given it's tick
   * @param tick
   */
  public void writeSilenceForced(long tick)
    throws SIException;
  
  /**
   * Gets a list of the ticks on a stream
   * @return
   */
  public List getTicksOnStream();
  
  /**
   * Gets the tickRange for the given tick value
   * @param tick
   * @return
   */
  public TickRange getTickRange(long tick);
  
  /**
   * Get a unique id of a stream within a streamSet
   * @return
   */
  public String getID();
}
