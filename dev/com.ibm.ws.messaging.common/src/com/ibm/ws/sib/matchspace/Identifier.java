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
 * 207007.1         150604 nyoung   SelectionCriteria replaces selector and 
 *                                  discriminator on Core SPI.
 * SIB0136a.msp.1   021106 nyoung   Stage 1 implementation of XPath Selector support. 
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * 504438           180308 nyoung   XPath support does not handle namespace prefixes                                 
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

/** This interface represents one Identifier within a Selector expression */

public interface Identifier extends Selector 
{
  /**
   * Returns the accessor.
   * @return ValueAccessor
   */
  public ValueAccessor getAccessor(); 

  /**
   * Returns the name.
   * @return String
   */
  public String getName();

  /**
   * Returns the type.
   * @return int
   */
  public int getType();

  /**
   * Sets the accessor.
   * @param accessor The accessor to set
   */
  public void setAccessor(ValueAccessor accessor); 

  /**
   * Sets the name.
   * @param name The name to set
   */
  public void setName(String name);

  /**
   * Sets the full name of an Identifier. The full name is only relevant in the XPath Selector Domain and 
   * incorporates the full path for the attribute or element referenced.
   * 
   * A full name is necessary so that we are able to distinguish between common names at the same depth in an XML
   * document.
   * 
   * @param name
   */
  public void setFullName(String name);
  
  /**
   * Return the full name of the Identifier. The full name is only relevant in the XPath Selector Domain.
   * 
   * @return
   */
  public String getFullName();
  
  /**
   * Sets the type.
   * @param type The type to set
   */
  public void setType(int type); 

  /**
   * Returns the schemaId.
   * @return long
   */
  public long getSchemaId();
  
  /**
   * Returns the ordinalPosition.
   * @return int
   */
  public Object getOrdinalPosition();
  
  /**
   * Returns the caseOf.
   * @return Identifier
   */
  public Identifier getCaseOf(); 

  /**
   * Sets the caseOf.
   * @param caseOf The caseOf to set
   */
  public void setCaseOf(Identifier caseOf);
  
  /**
   * Sets the ordinalPosition.
   * @param ordinalPosition The ordinalPosition to set
   */
  public void setOrdinalPosition(Object ordinalPosition);  
  
  /**
   * Sets the selector domain.
   * @param domain The value to set
   */
  public void setSelectorDomain(int domain); 
  
  /**
   * Returns the selector domain.
   * @return int
   */
  public int getSelectorDomain(); 
  
  /**
   * Sets a compiled expression into the Identifier.
   * @param domain The value to set
   */
  public void setCompiledExpression(Object expression);   
  /**
   * Returns the compiled expression.
   * @return int
   */
  public Object getCompiledExpression();   
  
  /**
   * Returns the location step.
   * @return int
   */
  public int getStep();  
}
