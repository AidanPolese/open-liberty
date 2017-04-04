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
 * LIDB2117        041103 vaughton Original
 * ============================================================================
 */

package com.ibm.ws.sib.trm.links;

import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * The link change listener is an interface which is called by the Link
 * Manager whenever changes occur in the state of a link. A LinkChangeListener
 * is registered via the LinkManager setChangeListener method.
 */

public interface LinkChangeListener {

  /**
   * The listener method called on each link change.
   *
   * @param linkUuid uuid of the link
   *
   * @param outboundMeUuid uuid of the outbound messaging engine if the link
   * has been stared or null if the link has been stopped
   *
   * @param inboundMeUuid uuid of the inbound messaging engine if the link
   * has been started or null if no inbound messaging engine exists for this link
   */

  void linkChange (SIBUuid12 linkUuid, SIBUuid8 outboundMeUuid, SIBUuid8 inboundMeUuid);

}
