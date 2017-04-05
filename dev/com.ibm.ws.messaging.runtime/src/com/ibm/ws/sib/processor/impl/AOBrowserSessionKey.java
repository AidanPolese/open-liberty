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
 * 180483.3        161003 sbhola   initial implementation
 * 181718.4         221203 gatfora  Move to the new UUID classes
 * 248145           201204 gatfora  Remove code that is not used
 * SIB0113a.mp.8    200607 cwilkin  Gathering infrastructure in remote get
 * SIB0113a.mp.12   211207 cwilkin  Gathering Recovery+Browse
 * 516346           280408 djvines  Implement equals to meet the spec
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl;

import com.ibm.ws.sib.utils.SIBUuid12;
import com.ibm.ws.sib.utils.SIBUuid8;

// Import required classes.

/**
 * The key of a remote browse session, at the DME, which is composed of 2 parts, the RME and the browse id.
 */
public final class AOBrowserSessionKey
{
  private final SIBUuid8 remoteMEUuid;
  private final SIBUuid12 gatheringTargetDestUuid;
  private final long browseId; // the unique Id of the browse session, wrt the remoteMEUuid

  /**
   * Constructor
   * @param remoteMEUuid The UUID of the remote ME
   * @param browseId The unique browse id created by this remote ME
   */
  public AOBrowserSessionKey(SIBUuid8 remoteMEUuid, SIBUuid12 gatheringTargetDestUuid, long browseId)
  {
    this.remoteMEUuid = remoteMEUuid;
    this.gatheringTargetDestUuid = gatheringTargetDestUuid;
    this.browseId = browseId;
  }

  public final SIBUuid8 getRemoteMEUuid()
  {
    return remoteMEUuid;
  }

  public final long getBrowseId() {
    return browseId;
  }

  /**
   * Overriding the Object.hashCode() method
   */
  public final int hashCode()
  {
    // the lower significant bits should be good enough to prevent frequent hash collisions
    return (int) (browseId % Integer.MAX_VALUE);
  }

  /**
   * Overriding the Object.equals() method
   */
  public final boolean equals(Object obj)
  {
    if (obj == null) return false;

    if (obj instanceof AOBrowserSessionKey)
    {
      AOBrowserSessionKey o = (AOBrowserSessionKey) obj;
      if (remoteMEUuid.equals(o.remoteMEUuid) &&
          browseId == o.browseId &&
          (gatheringTargetDestUuid == o.gatheringTargetDestUuid ||  // covers targetDestUuid=null
          gatheringTargetDestUuid.equals(o.gatheringTargetDestUuid)))
      {
        return true;
      }
    }

    return false;
  }
}
