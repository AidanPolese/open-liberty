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
 * 166318.3         090603 nyoung   Change Matcher signature, include trace 
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 171415           100703 gatfora  Removal of complile time warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.14        011203 auerbach Optimized LIKE processing and generalized TOPIC
 * 191828           300304 nyoung   Rework Matcher remove processing.
 * SIB0155.mspac.1  120606 nyoung   Repackage MatchSpace RAS.
 * SIB0155.msp.4    151106 nyoung   Enable OSGImin support.
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

// Import required classes.
import com.ibm.ws.sib.matchspace.selector.impl.OrdinalPosition;
import com.ibm.ws.sib.matchspace.utils.MatchSpaceConstants;
import com.ibm.ws.sib.matchspace.utils.Trace;
import com.ibm.ws.sib.matchspace.utils.TraceUtils;
import com.ibm.ws.sib.matchspace.BadMessageFormatMatchingException;
import com.ibm.ws.sib.matchspace.MatchSpaceKey;
import com.ibm.ws.sib.matchspace.Conjunction;
import com.ibm.ws.sib.matchspace.MatchingException;
import com.ibm.ws.sib.matchspace.MatchTarget;
import com.ibm.ws.sib.matchspace.EvalCache;
import com.ibm.ws.sib.matchspace.SearchResults;

/** The CacheingMatcher is always the Matcher at the boundary between an EqualityMatcher
 * with cacheing enabled and its non-equality children.  It has two roles, one as a 
 * matcher, which simply delegates to its child, and one as a cooperating partner (with 
 * CacheingSearchResults) in implementing the cacheing scheme.  In the latter role, it
 * adds its child Matcher to the list of non-equality Matchers that are consistent with
 * a particular value of the root Identifier.  It also maintains a 'content' flag that
 * tracks whether there are any tests below this point in the tree that can invalidate
 * result cacheing.
 **/

class CacheingMatcher extends ContentMatcher
{
  // Standard trace boilerplate
  
  private static final Class cclass = CacheingMatcher.class;
  private static Trace tc = TraceUtils.getTrace(CacheingMatcher.class,
      MatchSpaceConstants.MSG_GROUP_LISTS);
 
  /** Constructor */
  CacheingMatcher(OrdinalPosition ordinalPosition, ContentMatcher child) {
    super(ordinalPosition);
    if (tc.isEntryEnabled())
      tc.entry(cclass, "CacheingMatcher", "ordinalPosition: "+ ordinalPosition + 
        ",child: "+ child);
      
    vacantChild = child;

    if (tc.isEntryEnabled())
	    tc.exit(cclass, "CacheingMatcher", this);    
  }

  // Delegate put
  public void put(Conjunction selector, MatchTarget object,
                  InternTable subExpr)
  throws MatchingException
  {
    if (tc.isEntryEnabled())
	    tc.entry(this,cclass, "put",
               new Object[] {selector,object, subExpr}	
	);
    vacantChild = Factory.createMatcher(ordinalPosition, selector, vacantChild);
    vacantChild.put(selector, object, subExpr);

    if (tc.isEntryEnabled())
	    tc.exit(this,cclass, "put");
  }

  /** get delegates and also caches and reports whether there are any tests below
   * this point in the tree.
   */
  public void get(Object rootVal,
                  MatchSpaceKey msg,
                  EvalCache cache,
                  Object contextValue,
                  SearchResults result)
    throws MatchingException,BadMessageFormatMatchingException
  {
    if (tc.isEntryEnabled())
	    tc.entry(this,cclass, "get", new Object[]{rootVal,msg,cache,result});

    if (result instanceof CacheingSearchResults)
      ((CacheingSearchResults) result).setMatcher(vacantChild);
    vacantChild.get(null, msg, cache, contextValue, result);

    if (tc.isEntryEnabled())
	    tc.exit(this,cclass, "get");
  }

  /** Remove just delegates */
  public ContentMatcher remove(Conjunction selector, MatchTarget object, InternTable subExpr, OrdinalPosition parentId)
    throws MatchingException
  {
    if (tc.isEntryEnabled())
	    tc.entry(this,cclass, "remove","selector: "+selector+", object: "+object);

    vacantChild = vacantChild.remove(selector, object, subExpr, ordinalPosition);
    ContentMatcher result = this;
    if (vacantChild == null)
      result = null;

    if (tc.isEntryEnabled())
	    tc.exit(this,cclass, "remove","result: " + result);

    return result;
  }
}
