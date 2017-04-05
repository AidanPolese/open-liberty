/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5639-D57,5630-A36,5630-A37,Copyright IBM Corp. 2012
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
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

import com.ibm.ws.sib.matchspace.Selector;
import com.ibm.ws.sib.matchspace.SimpleTest;


public class ExtendedSimpleTestImpl extends SimpleTestImpl
{
	//Identifier wrappedIdentifiers[] = null;
	
	public ExtendedSimpleTestImpl(Selector sel) 
	{
	  super(sel);
	  
	  // If kind is ID and the selector is not of type CHILD, 
	  // re-map to NOTNULL, which gives the desired behaviour for XPath support
	  
	  // As a result of the remapping, predicates such as [employee] and
	  // [employee>100] can be correctly combined.
//      if (sel instanceof Identifier)
//      {
//        identifier = (Identifier) sel;
//        if (kind == ID && identifier.getType() != Selector.CHILD)
//        {
////          kind = NOTNULL;
//        }
//      }      
    }
  // If kind is ID treat as if we have NOTNULL type, to give the desired 
  // behaviour for XPath support. Then predicates such as 
  // [salary] and [salary>100] can be correctly combined.  
  public boolean combine(SimpleTest other)
  {
    if (kind == ID)
    {
      absorb(other);
      return true;
    }
    else
      return super.combine(other);
  }
}
