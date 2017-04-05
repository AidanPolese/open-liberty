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
 * 430211           030407 ajw      XPath10ParserImpl not available
 * 504438           180308 nyoung   XPath support does not handle namespace prefixes 
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.tools;

import java.util.Map;

import com.ibm.ws.sib.matchspace.Selector;

public interface XPath10Parser extends MatchParser
{
  /**
   * This method retains the behaviour of the first implementation of XPath support,
   * where the entire expression is wrapped in a single identifier.
   * 
   * @param selector
   * @return
   */
  Selector parseWholeSelector(String selector); 
  
  /**
   * Set a mapping of prefix to namespace into the XPath10Parser thus allowing
   * it to support query expressions that include namespaces.
   * 
   * @param namespaceMappings
   */
  void setNamespaceMappings(Map namespaceMappings);
}
