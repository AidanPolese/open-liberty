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
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.selector.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.14        011203 auerbach Optimized LIKE processing and generalized TOPIC
 * 199185           220404 gatfora  Fix Javadoc.
 * 207007.1         150604 nyoung   SelectionCriteria replaces selector and 
 *                                  discriminator on Core SPI.
 * SIB0136a.msp.1   021106 nyoung   Stage 1 implementation of XPath Selector support.
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * 504438           180308 nyoung   XPath support does not handle namespace prefixes        
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.ValueAccessor;

/** This class represents one Identifier within a Selector expression */

public final class IdentifierImpl extends SelectorImpl implements Identifier 
{

  /** The Identifier's String name.  This is initialized at construction and may be
   * modified by a Resolver.  After that, it is read-only.
   **/

  public String name;

  public String fullName;
  
  /** The Identifier's ValueAccessor, if any.  This is set optionally by a resolver and is
   * used by an MatchSpaceKey to speed up retrieval of the value, assuming the MatchSpaceKey
   * recognizes the subtype of ValueAccessor employed.
   **/

  public ValueAccessor accessor;

  /** The Identifier's ordinal position in the schema or pseudo-schema for its topic.
   * MatchSpace uses this to organize the search tree (lower numbered Identifiers are
   * tested nearer to the top of the tree).  It is set optionally by a Resolver.  A
   * Resolver that sets ordinalPosition at all must do so consistently so that no two
   * Identifiers with different names in the same Conjunction have the same
   * ordinalPosition.
   **/

  public OrdinalPosition ordinalPosition;

  /** The schema to which this Identifier belongs. 
   * <p>0L indicates the default schema (to
   * unschematized JMS messages belong).  This value is set by Resolvers and is used to
   * prevent two Identifiers from different schemas from comparing equal just because they
   * have the same name
   **/
  public long schemaId;

  /** If this Identifier is a case label for a variant, points to the Identifier for the
   * name of the variant.  Otherwise, null.  A case label may be turned into some other
   * expression (eg, a Literal) by the Resolver or may be recognized by the MatchSpaceKey at
   * evaluation time.  The caseOf field must set explicitly (typically by a parser).  The
   * constructors and methods of the selector tree classes, as well as the Transformer and
   * Evaluator, all ignore this field.
   **/

  public Identifier caseOf;
  
  /** The domain of the selector with which the identifier is associated.  
   * This attribute was introduced for use by JetStream and is used in conjunction
   * with a JetStream specific Resolver.
   * 
   * Other MatchSpace users may ignore this attribute.
   * 
   **/
  public int selectorDomain;
  
  /** This is used in the XPath10 selector domain, to keep track of the location
   * step with which this identifier is associated.  
   *
   **/
  private int step;
  
  /** Holds a compiled expression (e.g XPath) for a MatchSpaceKey to process */
  private Object compiledExpression = null;
    
  /** Create a new Identifier
  *
  * @param name the name to associate with the Identifier
  **/  
  public IdentifierImpl(String name)
  {
    this.name = name;
    type = UNKNOWN;
    numIds = 1;
    step = 0;
  }

  /** Create an Identifier during decoding of a byte[] form (used only by
   * Selector.decode).
   **/

  public IdentifierImpl(ObjectInput buf) throws IOException
  {
    name = buf.readUTF();
    type = UNKNOWN;
    numIds = 1;
    step = 0;
  }

  /** Encode this Identifier into a ObjectOutput (used only by Selector.encode).  Note, at
   * present we are saving only the name, not the ValueAccessor or ordinalPosition.
   **/

  public void encodeSelf(ObjectOutput buf) throws IOException
  {
    buf.writeUTF(name);
  }

  // Overrides

  public boolean equals(Object o)
  {
    if (o instanceof Identifier && super.equals(o))
      return ((Identifier) o).getName().equals(name)
        && ((Identifier) o).getSchemaId() == schemaId;
    else
      return false;
  }

  public int hashCode()
  {
    return name.hashCode();
  }

  public String toString()
  {
    if(selectorDomain == 2)
      return fullName;
    else
      return name;    
  }

  /**
   * Returns the accessor.
   * @return ValueAccessor
   */
  public ValueAccessor getAccessor()
  {
    return accessor;
  }

  /**
   * Returns the name.
   * @return String
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the type.
   * @return int
   */
  public int getType()
  {
    return type;
  }

  /**
   * Sets the accessor.
   * @param accessor The accessor to set
   */
  public void setAccessor(ValueAccessor accessor)
  {
    this.accessor = accessor;
  }

  /**
   * Sets the name.
   * @param name The name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.matchspace.Identifier#setFullName(java.lang.String)
   */
  public void setFullName(String name)
  {
    this.fullName = name;
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.matchspace.Identifier#getFullName()
   */
  public String getFullName()
  {
    if(selectorDomain == 2)
      return fullName;
    else
      return name;
  }
  
  /**
   * Sets the type.
   * @param type The type to set
   */
  public void setType(int type)
  {
    this.type = type;
  }

  /**
   * Returns the schemaId.
   * @return long
   */
  public long getSchemaId() 
  {
	return schemaId;
  }

  /**
   * Returns the ordinalPosition.
   * @return int
   */
  public Object getOrdinalPosition() 
  {
	return ordinalPosition;
  }

  /**
   * Returns the caseOf.
   * @return Identifier
   */
  public Identifier getCaseOf() 
  {
	return caseOf;
  }

  /**
   * Sets the caseOf.
   * @param caseOf The caseOf to set
   */
  public void setCaseOf(Identifier caseOf) 
  {
	this.caseOf = caseOf;
  }

  /**
   * Sets the ordinalPosition.
   * @param ordinalPosition The ordinalPosition to set
   */
  public void setOrdinalPosition(Object ordinalPosition) 
  {
	this.ordinalPosition = (OrdinalPosition) ordinalPosition;
  }
  
  /**
   * Sets the selector domain.
   * @param domain The value to set
   */
  public void setSelectorDomain(int domain)
  {
    this.selectorDomain = domain;
    if(domain == 2)
      extended = true;
    else
      extended = false;
  }
  
  /**
   * Returns the selector domain.
   * @return int
   */
  public int getSelectorDomain()
  {
    return selectorDomain; 
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.matchspace.Identifier#getCompiledExpression()
   */
  public Object getCompiledExpression() 
  {
    return compiledExpression;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.matchspace.Identifier#setCompiledExpression(java.lang.Object)
   */
  public void setCompiledExpression(Object expression) 
  {
    compiledExpression = expression;
  }

  public int getStep() 
  {
    return step;
  }

  public void setStep(int step) 
  {
    this.step = step;
  }
  
  /**
   * The Selector is extended if operating in the XPath domain
   */
  public boolean isExtended()
  {
    return extended;
  }  
}
