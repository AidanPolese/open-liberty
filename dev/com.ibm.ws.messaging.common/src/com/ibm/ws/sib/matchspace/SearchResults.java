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
 * 166318.3         090603 nyoung   Replace Gryphon utilities with JDK Equivalents 
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * 166318.14        011203 auerbach Optimized LIKE processing and generalized TOPIC
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

import java.util.List;

/**
 * This class provides a trivial abstract base class for building a
 * traversal's result set.
 *
 */
public interface SearchResults
{
  /*
   * Adds a reference to an object list to the result set's list
   * of object lists.
   *
   * @param objects an array FastVectors of objects, indexed by type
   */
  public abstract void addObjects(List[] objects);

  /*
   * Provides something that can potentially short-circuit the entire search.
   *
   * @param rootIdVal the value of the root Identifier for which the result set is built
   */
  public abstract Object provideCacheable(Object rootIdVal)
    throws MatchingException;

  /* Accepts something that was previously cached.  Only called when the match
   * circumstances are identical (the same topic being matched with no intervening changes
   * in subscriptions).
   *
   * @param cached the cached Object to be reused
   *
   * @return true if the cached object is acceptable.  We assume the primary reason for
   * returning false would be that the cached object is not of the expected type, which
   * can happen if the same MatchSpace is searched by two different kinds of SearchResults
   * object, both of which are trying to do caching.
   * */
  public abstract boolean acceptCacheable(Object cached);

  /** Discard contents preparatory to starting over again */

  public abstract void reset();
}
