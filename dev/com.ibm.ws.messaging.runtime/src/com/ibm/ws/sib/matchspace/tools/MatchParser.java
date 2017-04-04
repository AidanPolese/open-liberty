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
 * 166318.9         160903 nyoung   First version - Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * SIB0136a.msp.1   021106 nyoung   Stage 1 implementation of XPath Selector support. 
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.     
 * ===========================================================================
 */

package com.ibm.ws.sib.matchspace.tools;

import com.ibm.ws.sib.matchspace.impl.Matching;
import com.ibm.ws.sib.matchspace.Selector;

/** The MatchParser class provides parsing support for a superset of the JMS selector
 * syntax, returning a Selector tree.  Except for the superset features, the language
 * accepted is that of the JMS specification.
 *
 * MatchParser language features not in JMS:
 *
 * (1) Identifiers can be quoted with " and may contain any character except unescaped ".
 *
 * (2) Identifiers (unquoted) may contain the field separator character '.'.
 *
 * (3) Set predicates allow an arbitrary expression on the left (not restricted to
 * identifier) and a list of arbitrary expressions on the right (not restricted to string
 * literals).
 *
 * (4) There is support for lists.  This support is accessed through the use of [ ]
 * characters and is described in detail elsewhere.
 *
 * The superset features can be turned off by setting the 'strict' flag, causing the
 * parser to recognize only the JMS syntax.
 **/

public interface MatchParser 
{

  /** Return the Selector tree associated with a primed parser.
   *
   * @return a selector tree.  If the parse was successful, the top node of the tree will
   * be of BOOLEAN type; otherwise, it will be of INVALID type.
   **/
  public Selector getSelector(String Selector);
  
  /**
   * Allow the setting of the Matching instance into the parser
   * 
   * @param matching
   */
  public void setMatching(Matching matching);

}
