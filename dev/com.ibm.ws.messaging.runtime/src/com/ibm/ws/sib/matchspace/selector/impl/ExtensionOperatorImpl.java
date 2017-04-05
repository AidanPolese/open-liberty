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

import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.Selector;

public class ExtensionOperatorImpl extends OperatorImpl
{

	/** Make a new extension operator
  *
  * @param op1 the sole operand
  **/

  public ExtensionOperatorImpl(Identifier op1[]) 
  {
    this.operands = op1;
    assignType();
    if (type == INVALID)
      return;
    numIds = op1.length;
  }
  
  public ExtensionOperatorImpl(Selector op1[]) 
  {
    this.operands = op1;
    assignType();
    if (type == INVALID)
      return;
    numIds = op1.length;
  }

  public ExtensionOperatorImpl(int op, Selector op1) 
  {
    super(op,op1);
  }  
  
  public ExtensionOperatorImpl(int op, Selector op1, Selector op2) 
  {
    super(op,op1,op2);
  }  
  
  public void assignType()
  {
	  type = Selector.EXTENSION;
  }
 
  public String toString()
  {
  	String theString = "extensionOP: ";
  	for(int i=0;i<operands.length;i++)
  		theString = theString + "/" + operands[i];
	  return theString; 
  }
  
  /**
   * The Selector is extended if operating in the XPath domain
   */
  public boolean isExtended()
  {
    return true;
  }    
}
