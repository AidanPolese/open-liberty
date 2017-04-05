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
 * 166318.5         190603 nyoung   Integrate MatchSpace with MP.
 * 171415           100703 gatfora  Removal of complile time warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.12        121103 nyoung   Remove support for BooleanValue and NumericValue
 * 166318.14        011203 auerbach Optimized LIKE processing and generalized TOPIC
 * 191828           300304 nyoung   Rework Matcher remove processing.
 * SIB0155.mspac.1  120606 nyoung   Repackage MatchSpace RAS.
 * SIB0155.msp.4    151106 nyoung   Enable OSGImin support.
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

// Import required classes.
import java.util.List;

import com.ibm.ws.sib.matchspace.utils.MatchSpaceConstants;
import com.ibm.ws.sib.matchspace.utils.Trace;
import com.ibm.ws.sib.matchspace.utils.TraceUtils;
import com.ibm.ws.sib.matchspace.BadMessageFormatMatchingException;
import com.ibm.ws.sib.matchspace.MatchSpaceKey;
import com.ibm.ws.sib.matchspace.Conjunction;
import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.SimpleTest;
import com.ibm.ws.sib.matchspace.MatchingException;
import com.ibm.ws.sib.matchspace.MatchTarget;
import com.ibm.ws.sib.matchspace.EvalCache;
import com.ibm.ws.sib.matchspace.SearchResults;
import com.ibm.ws.sib.matchspace.selector.impl.EvaluatorImpl;
import com.ibm.ws.sib.matchspace.selector.impl.OrdinalPosition;

public class NumericMatcher extends EqualityMatcher 
{
  // Standard trace boilerplate
  private static final Class cclass = NumericMatcher.class;
  private static Trace tc = TraceUtils.getTrace(NumericMatcher.class,
      MatchSpaceConstants.MSG_GROUP_LISTS);

  /** A class to institute proper hashCode() and equals() behavior when doing
   * hash table operations on Numbers, following the rules in EvaluatorImpl.  
   */
  private static class Wrapper {
    private Object wrapped;
    private int hashCode;
    Wrapper(Object wrapped) { 
      this.wrapped = wrapped;
      hashCode = EvaluatorImpl.hash(wrapped);
    }
    public boolean equals(Object obj) {
      if (obj instanceof Wrapper) obj = ((Wrapper) obj).wrapped;
      return EvaluatorImpl.equals(wrapped, obj);
    }
    public int hashCode() {
      return hashCode;
    }
  }
  
  // This implementation uses CheapRangeTable (linear search
  // for including ranges) because it was found to be very nearly as good in practice
  // as the old RangeTable implementation used in early prototypes.    
  private CheapRangeTable ranges = new CheapRangeTable();

  // Constructor

  public NumericMatcher(Identifier id) {
    super(id);
    if (tc.isEntryEnabled())
      tc.entry(cclass, "NumericMatcher", "identifier: "+ id);
    if (tc.isEntryEnabled())
	  tc.exit(cclass, "NumericMatcher", this);          
  }


  //------------------------------------------------------------------------------
  // Method: NumericMatcher.handlePut
  //------------------------------------------------------------------------------

  void handlePut(SimpleTest test, Conjunction selector, MatchTarget object,
                 InternTable subExpr) throws MatchingException
  {

    if (tc.isEntryEnabled())
	  tc.entry(this,cclass, "handlePut",
               new Object[] {test,selector,object, subExpr}	
	  );
    Object value = test.getValue();
    if (value != null)
      // equality test.
      handleEqualityPut(new Wrapper(value), selector, object, subExpr);
    else {  
      // Inequality or range, goes in CheapRangeTable
      ContentMatcher next = (ContentMatcher) ranges.getExact(test);
      // Create a new Matcher if called for
      ContentMatcher newNext = nextMatcher(selector, next);
      // Record the subscription
      newNext.put(selector, object, subExpr);
      // See if Matcher must be replaced, and, in that case, where.
      if (newNext != next)
        if (next == null)
          ranges.insert(test, newNext);
        else
          ranges.replace(test, newNext);
    }
    if (tc.isEntryEnabled())
	  tc.exit(this,cclass, "handlePut");        
  }


  //------------------------------------------------------------------------------
  // Method: NumericMatcher.handleGet
  //------------------------------------------------------------------------------

  void handleGet(Object value, MatchSpaceKey msg, EvalCache cache, Object contextValue,
      SearchResults result) throws MatchingException, BadMessageFormatMatchingException
  {
    if (tc.isEntryEnabled())
	  tc.entry(this,cclass, "handleGet", new Object[] {value,msg,cache,contextValue,result});
    if (!(value instanceof Number)) { // was NumericValue
      return;
    }
    List inexact = ranges.find((Number) value); // was NumericValue

    for (int i = 0; i < inexact.size(); i++)
      ((ContentMatcher) inexact.get(i)).get(null, msg, cache, contextValue, result);
    if (haveEqualityMatches())
      handleEqualityGet(new Wrapper(value), msg, cache, contextValue, result);

    if (tc.isEntryEnabled())
	   tc.exit(this,cclass, "handleGet");      
  }


  //------------------------------------------------------------------------------
  // Method: NumericMatcher.handleRemove
  //------------------------------------------------------------------------------

  void handleRemove(SimpleTest test, Conjunction selector,
                    MatchTarget object, InternTable subExpr, OrdinalPosition parentId) throws MatchingException
  {
    if (tc.isEntryEnabled())
	  tc.entry(this,cclass, "handleRemove", new Object[]{test,selector,object,subExpr});
	    	
    // The following returns the comparand if test is an equality test, null otherwise
    Object value = test.getValue();
    if (value != null)
      handleEqualityRemove(new Wrapper(value), selector, object, subExpr, parentId);
    else {
      // Inequality or range, was in CheapRangeTable
      ContentMatcher next = (ContentMatcher) ranges.getExact(test);
      // Delete the subscription, also deleting a Matcher if called for and replacing with
      // descendent or null
      ContentMatcher newNext = (ContentMatcher) next.remove(selector,
        object, subExpr, ordinalPosition);
      // See if Matcher was replaced or removed, and reflect that change in the right place
      if (newNext != next)
        if (newNext != null)
          ranges.replace(test, newNext);
        else
          ranges.remove(test);
    }
    if (tc.isEntryEnabled())
	  tc.exit(this,cclass, "handleRemove");          
  }


  //------------------------------------------------------------------------------
  // Method: BooleanMatcher.isEmpty
  //------------------------------------------------------------------------------

  boolean isEmpty() {
    return super.isEmpty() && ranges.isEmpty();
  }
} // NumericMatcher
