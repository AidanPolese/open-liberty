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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 186967.7.6       250304 millwood Support TRM link interfaces            
 * 250746           190105 gatfora  Remove unthrown exception declarations
 * 266910.1         210405 nottinga Added undefine method
 * ============================================================================
 */

package com.ibm.ws.sib.processor.test;

import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.sib.trm.links.mql.MQLinkManager;
import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * @author millwood
 *
 */
public class UTMQLinkManager implements MQLinkManager
{
  private Set links = new HashSet();

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.trm.links.mql.MQLinkManager#define(com.ibm.ws.sib.utils.SIBUuid12)
   */
  public void define(SIBUuid12 arg0)
  {
    links.add(arg0);    
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.trm.links.mql.MQLinkManager#isDefined(com.ibm.ws.sib.utils.SIBUuid12)
   */
  public boolean isDefined(SIBUuid12 arg0)
  {
    return links.contains(arg0);
  }

  /**
   * @see MQLinkManager#undefine(SIBUuid12)
   */
  public void undefine(SIBUuid12 linkUuid)
  {
    links.remove(linkUuid);
  }
}