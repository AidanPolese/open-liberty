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
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

import java.util.ArrayList;
import java.util.Iterator;

/** 
 * This class represents the evaluation context for Set Value Matcher classes.
 * 
 * It comprises a list of wrapped nodes that are maintained in WrappedNodeResults 
 * objects.
 *   
 **/
public class SetValEvaluationContext 
{

  private ArrayList contextNodeList = null;
  
  /**
   * Construct a new empty context
   */
  public SetValEvaluationContext()
  {
    contextNodeList = new ArrayList();
  }
  
  /**
   * Construct a new context with a single wrapped node
   * 
   * @param wrapper
   */
  public SetValEvaluationContext(WrappedNodeResults wrapper)
  {
    contextNodeList = new ArrayList();
    
    contextNodeList.add(wrapper); 
  }
  
  public void addNode(WrappedNodeResults wrapper) 
  {
    contextNodeList.add(wrapper);  
  }  

  public void addUnwrappedNode(Object node) 
  {
      // Create an object to hold the wrapped node
      WrappedNodeResults wrapper = new WrappedNodeResults(node);
      contextNodeList.add(wrapper); 
   }    
  
  public void addWrappedNodeList(ArrayList wrappedNodeList) 
  {
    Iterator iter = wrappedNodeList.iterator();

    while(iter.hasNext())
    {
      WrappedNodeResults wrapper = (WrappedNodeResults)iter.next();
      contextNodeList.add(wrapper); 
    }
  }    
  
  public ArrayList getWrappedNodeList()
  {
    return contextNodeList;
  }

  public String toString() 
  {
    String asString = "";
    StringBuffer sb = null;
    Iterator iter = contextNodeList.iterator();

    while(iter.hasNext())
    {
      WrappedNodeResults wrapper = (WrappedNodeResults)iter.next();
      if(sb == null)
      {
        // Instantiate a StringBuffer
        sb = new StringBuffer("<");
      }
      else
      {
        sb.append(" <");       
      }
      sb.append(wrapper.toString());
      sb.append(">"); 
    }
    // We've finished
    if (sb != null)
    {
      asString = sb.toString();
    }
    return asString;
  }  
  
}
