/*
 * 
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 253203          030205 tevans   New Statestream
 * ============================================================================
 */

package com.ibm.ws.sib.processor.gd.statestream;

import com.ibm.ws.sib.processor.impl.interfaces.SIMPMessage;

public class GDMessageData implements TickData
{
  public SIMPMessage msg;
  public long itemStreamIndex;
  public boolean reallocateOnCommit = false;
}
