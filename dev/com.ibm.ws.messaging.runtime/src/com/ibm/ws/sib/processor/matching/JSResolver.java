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
 * 207007.1         150604 nyoung   SelectionCriteria replaces selector and 
 *                                  discriminator on Core SPI.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.matching;

import com.ibm.wsspi.sib.core.SelectorDomain;
import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.Selector;
import com.ibm.ws.sib.matchspace.tools.Resolver;
import com.ibm.ws.sib.matchspace.tools.PositionAssigner;

/**
 * @author Neil Young
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JSResolver implements Resolver 
{
  private SelectorDomain selectorDomain;
  
  JSResolver(SelectorDomain domain)
  {
    selectorDomain = domain;
  }
    
  // Implement resolve
  public Selector resolve(Identifier id, PositionAssigner positionAssigner) {
    positionAssigner.assign(id);
    // set the selector domain into the identifier
    id.setSelectorDomain(selectorDomain.toInt());
    return id;
  }
}
