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
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * SIB0136b.msp.2   220207 nyoung   More meaningful trace for XPath Selector support.
 * 432107           200407 nyoung   Performance Regression in sib/mfp with java/util classes
 * 504438           250308 nyoung   XPath support does not handle namespace prefixes  
 * 509852           030408 sibcopyr Automatic update of trace guards 
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.ws.sib.matchspace.BadMessageFormatMatchingException;
import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.MatchSpaceKey;
import com.ibm.ws.sib.matchspace.impl.Matching;
import com.ibm.ws.sib.matchspace.MatchingException;
import com.ibm.ws.sib.matchspace.utils.MatchSpaceConstants;
import com.ibm.ws.sib.matchspace.utils.Trace;
import com.ibm.ws.sib.matchspace.utils.TraceComponent;
import com.ibm.ws.sib.matchspace.utils.TraceUtils;

public class SetValBooleanMatcher extends BooleanMatcher 
{
  // Standard trace boilerplate
  private static final Class cclass = SetValBooleanMatcher.class;
  private static Trace tc = TraceUtils.getTrace(SetValBooleanMatcher.class,
    MatchSpaceConstants.MSG_GROUP_LISTS); 
  
  /** Make a new EqualityMatcher
   * @param id the identifier to use
   */
  SetValBooleanMatcher(Identifier id) 
  {
    super(id);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      tc.entry(cclass, "SetValBooleanMatcher", "identifier: "+ id);
      tc.exit(cclass, "SetValBooleanMatcher", this);
    }
  }
  
  /** Override default implementation of getValue()
   * 
   * @param msg
   * @param contextValue
   * @throws MatchingException
   * @throws BadMessageFormatMatchingException
   */
  Object getValue(
      MatchSpaceKey msg,
      Object contextValue)
  throws MatchingException, BadMessageFormatMatchingException
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      tc.entry(
        cclass,
        "getValue",
        "msg: " + msg + "contextValue: " + contextValue);    
    // The value which we'll return    
    Boolean resultBool = Boolean.FALSE; 

    // May need to call MFP multiple times, if our context has multiple nodes
    if(contextValue != null)
    {
      // Currently this must be a list of nodes
      if (contextValue instanceof SetValEvaluationContext)
      {
        SetValEvaluationContext evalContext = (SetValEvaluationContext)contextValue;
        
        // Get the node list
        ArrayList wrappedParentList = evalContext.getWrappedNodeList();
        // If the list is empty, then we have yet to get the document root
        if(wrappedParentList.isEmpty())
        {
    	    //Set up a root (document) context for evaluation
        	Object docRoot = Matching.getEvaluator().getDocumentRoot(msg);
            
          // Create an object to hold the wrapped node
          WrappedNodeResults wrapper = new WrappedNodeResults(docRoot);
          // Set the root into the evaluation context
          evalContext.addNode(wrapper);
        } 
        
        // Iterate over the nodes
        Iterator iter = wrappedParentList.iterator();

        while(iter.hasNext())
        {
          WrappedNodeResults nextWrappedNode = (WrappedNodeResults)iter.next();       
          Object nextParentNode = nextWrappedNode.getNode();
          
          String debugString = ""; 
          if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            tc.debug(this,cclass, "getValue", "Evaluation context node: " + nextWrappedNode);  
          
          // If no cached value we'll need to call MFP
          Boolean tempBool = nextWrappedNode.getEvalBoolResult(id.getName());
            
          if(tempBool == null)
          {
            // Call MFP to get the results for this node
            tempBool = (Boolean) msg.getIdentifierValue(id,
                                                        false,
                                                        nextParentNode, 
                                                        false); // false means dont return a list

            // Add result to cache for next time
            nextWrappedNode.addEvalBoolResult(id.getName(), tempBool);
            
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
              debugString = "Result from MFP ";            
          }
          else
          {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
              debugString = "Result from Cache ";        
          }   
          
          // Useful debug
          if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
          {
            String asString = ", type: Boolean - <" + tempBool + ">";

            tc.debug(this,cclass, "getValue", debugString + "for identifier " + id.getName() + asString);
          }   
          
          if(tempBool.booleanValue())
          {
            resultBool = Boolean.TRUE;
            break;
          }     
 
        } // eof while
      } // eof instanceof XPathEvaluationContext
    } // eof contextValue not null
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      tc.exit(this,cclass, "getValue", resultBool);
    return resultBool;
  } // eof getValue()    
}
