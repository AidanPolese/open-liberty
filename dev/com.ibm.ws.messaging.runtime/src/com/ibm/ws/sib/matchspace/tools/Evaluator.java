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
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * 432107           200407 nyoung   Performance Regression in sib/mfp with java/util classes       
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.tools;

import java.util.ArrayList;

import com.ibm.ws.sib.matchspace.EvalCache;
import com.ibm.ws.sib.matchspace.Selector;
import com.ibm.ws.sib.matchspace.MatchSpaceKey;
import com.ibm.ws.sib.matchspace.BadMessageFormatMatchingException;

/** The Evaluator is used to evaluate Selector trees, returning a value */

public interface Evaluator {

  /** Evaluates a selector tree
   *
   * @param sel the selector tree to evaluate
   *
   * @param bind the MatchSpaceKey to use in evaluating identifiers and caching partial
   * results
   *
   * @param permissive if true, evaluation should observe "permissive" mode in which there
   * are implicit casts between strings and numbers and between strings and booleans
   * according to the rules for numeric and boolean literals.  If false, evaluation
   * follows the normal JMS rules (numerics are promoted but there are otherwise no
   * implicit casts).
   *
   * @return the result, which will be a String, a BooleanValue, a NumericValue, or null.
   * Null is used for "missing" Numeric or String values.  BooleanValue.NULL is used for
   * missing Boolean values.
   *
   * @exception BadMessageFormatMatchingException when the method is unable to determine a
   * value because the message (or other object) from which the value must be extracted is
   * corrupted or ill-formed.
   **/

  public Object eval(Selector sel, MatchSpaceKey msg, EvalCache cache, Object contextValue, boolean permissive)
    throws BadMessageFormatMatchingException;


  /** Evaluates a selector tree without resolving identifiers (usually applied only to
   * Selector subtrees with numIds == 0).
   *
   * @param sel the selector tree to evaluate
   *
   * @return the result, which will be a String, a BooleanValue, a NumericValue, or null.
   * Null is used for "missing" Numeric or String values.  BooleanValue.NULL is used for
   * missing Boolean values.
   **/

  public Object eval(Selector sel);

  /**
   * Get a DOM document root from a message. This method will return null unless driven
   * where XPath support is implemented.
   * 
   * @param childNodeList
   * @return
   * @throws BadMessageFormatMatchingException 
   */
  public Object getDocumentRoot(MatchSpaceKey msg) throws BadMessageFormatMatchingException;


  /**
   * Retrieve text associated with a node, for debug.
   * 
   * @param node
   * @return
   */
  public String getNodeText(Object node);

  /**
   * Cast an ArrayList of Nodes to an ArrayList of Numbers.
   * 
   * @param childNodeList
   * @return
   */
  public ArrayList castToNumberList(ArrayList childNodeList);

  /**
   * Cast an ArrayList of Nodes to an ArrayList of Strings.
   * 
   * @param childNodeList
   * @return
   */
  public ArrayList castToStringList(ArrayList childNodeList);

}
