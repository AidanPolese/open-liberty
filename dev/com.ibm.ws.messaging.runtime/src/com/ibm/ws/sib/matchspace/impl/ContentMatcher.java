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
 * TBD              260303 astley   First version
 * 166318.3         090603 nyoung   Change Matcher signature
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.14        011203 auerbach Optimized LIKE processing and generalized TOPIC
 * 191828           300304 nyoung   Rework Matcher remove processing.
 * 199185           220404 gatfora  Fix Javadoc.
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

import com.ibm.ws.sib.matchspace.MatchSpaceKey;
import com.ibm.ws.sib.matchspace.Conjunction;
import com.ibm.ws.sib.matchspace.MatchTarget;
import com.ibm.ws.sib.matchspace.MatchingException;
import com.ibm.ws.sib.matchspace.BadMessageFormatMatchingException;
import com.ibm.ws.sib.matchspace.EvalCache;
import com.ibm.ws.sib.matchspace.SearchResults;
import com.ibm.ws.sib.matchspace.selector.impl.OrdinalPosition;

abstract class ContentMatcher
{

  /** Ordinal position of this Matcher */

  OrdinalPosition ordinalPosition;

  /** Pointer to subsidiary matchers for those subscriptions that don't care about tests
   * at this level.
   **/

  ContentMatcher vacantChild;

  /** Create a new ContentMatcher for a given ordinalPosition */

  ContentMatcher(OrdinalPosition ordinalPosition)
  {
    this.ordinalPosition = ordinalPosition;
  }

  /** Implementation of put method (handles vacantChild; overriding methods handle
   * everything else).
   **/

  void put(
    Conjunction selector,
    MatchTarget object,
    InternTable subExpr)
    throws MatchingException
  {
    vacantChild = nextMatcher(selector, vacantChild);
    vacantChild.put(selector, object, subExpr);
  }

  /** Implementation of get method (handles vacantChild; overriding methods handle
   * everything else).
   **/

  void get(
    Object rootValue, 
    MatchSpaceKey msg,
    EvalCache cache,
    Object contextValue,
    SearchResults result)
    throws MatchingException, BadMessageFormatMatchingException
  {
      vacantChildGet(null, msg, cache, contextValue, result);
  }

  /** Implementation of remove method (handles vacantChild; overriding methods handle
   * everything else).
   **/

  ContentMatcher remove(
    Conjunction selector,
    MatchTarget object,
    InternTable subExpr,
    OrdinalPosition parentId)
    throws MatchingException
  {
    if (vacantChild == null)
      throw new IllegalStateException();
    vacantChild =
      vacantChild.remove(selector, object, subExpr, ordinalPosition);
    return vacantChild;
  }
  
  /** Predicate saying whether the subtree rooted at this matcher has any tests in
   * it.  Defaults to true.  Overridden in DifficultMatcher, which sometimes answers
   * false if the DifficultMatcher degenerates to just alwaysMatch cases.
   */
  
  boolean hasTests() {
    return true;
  }
  
  /** Determine the next matcher to which a put operation should be delegated.
   * Except when cacheing is active, this method delegates to Factory.createMatcher.
   * It is overridden in EqualityMatcher to wrap newly created Matchers in a
   * CacheingMatcher when appropriate
   * 
   * @param selector  
   * @param oldMatcher
   */
  ContentMatcher nextMatcher(Conjunction selector, ContentMatcher oldMatcher) {
    return Factory.createMatcher(ordinalPosition, selector, oldMatcher);
  }
  
  void vacantChildGet(
      Object rootValue, 
      MatchSpaceKey msg,
      EvalCache cache,
      Object contextValue,
      SearchResults result)
      throws MatchingException, BadMessageFormatMatchingException
    {
      if (vacantChild != null)
        vacantChild.get(null, msg, cache, contextValue, result);
    }  
}
