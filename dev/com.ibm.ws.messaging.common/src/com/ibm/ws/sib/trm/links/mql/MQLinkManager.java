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
 * LIDB2117        040319 vaughton Original
 * d266910         190405 mayur    Add undefine method
 * ============================================================================
 */

package com.ibm.ws.sib.trm.links.mql;

import com.ibm.ws.sib.trm.links.LinkException;
import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * The MQ link manager is used specifically for managing Jetstream MQ links.
 */

public interface MQLinkManager {

  /**
   * Define a new mq link
   *
   * @param linkUuid The uuid of the new link
   *
   * @throws LinkException if the linkuuid is already defined
   */

  void define (SIBUuid12 linkUuid) throws LinkException;

  // Start d266910  
  
  /**
   * Undefine an mq link
   *
   * @param linkUuid The uuid of mq link to be undefined
   */

  void undefine (SIBUuid12 linkUuid);

  // End d266910 

  /**
   * Is the mq link defined
   *
   * @param linkUuid The uuid of the link
   *
   * @return boolean true if the link is already defined
   */

  boolean isDefined (SIBUuid12 linkUuid);

}
