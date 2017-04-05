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
 * 166318.3         090603 nyoung   First Version, takes over from EvalContext
 * 166318.4         160603 nyoung   Move to sib.processor project
 * 174606           190803 gatfora  Removal of compile warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.     
 * * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

/** Interface to be implemented by callers of the Evaluator in order to handle the
 * evaluation of Identifiers and the cacheing of partial results.
 **/

public interface MatchSpaceKey {

  /** Evaluate an Identifier
   *
   * @param id the Identifier to evaluate
   *
   * @param ignoreType if true, the identifier value should be returned if it exists,
   * regardless of the expected type recorded in id argument.  If false, the actual type
   * of value that is returned must conform to the expected type recorded in the
   * identifier (Selector.STRING, BOOLEAN, NUMERIC, or UNKNOWN).  If the value of the
   * identifier does not conform, null should be returned (as if the identifier has no
   * value).
   *
   * @param contextValue for use in XPath processing. Allows the provision of a context 
   * against which an XPath expression can be evaluated. 
   * 
   * @param returnList for use in XPath processing. Specifies whether the caller expects
   * the return to be an ArrayList of nodes or a Boolean. 
   * 
   * @return the value of the Identifier, which should be of type String, BooleanValue,
   * Integer, Long, Float, or Double, or null if the value does not exist or would not be
   * type-correct as controlled by the ignoreType argument.
   *
   * @exception BadMessageFormatMatchingException when the method is unable to determine a
   * value because the message (or other object) from which the value must be extracted is
   * corrupted or ill-formed.
   **/

  public Object getIdentifierValue(Identifier id, 
                                   boolean ignoreType,
                                   Object contextValue,
                                   boolean returnList)
    throws BadMessageFormatMatchingException;

 /** 
  * Older version of the above method, retained for compatibility.
  *
  * @param id the Identifier to evaluate
  *
  * @param ignoreType if true, the identifier value should be returned if it exists,
  * regardless of the expected type recorded in id argument.  If false, the actual type
  * of value that is returned must conform to the expected type recorded in the
  * identifier (Selector.STRING, BOOLEAN, NUMERIC, or UNKNOWN).  If the value of the
  * identifier does not conform, null should be returned (as if the identifier has no
  * value).
  *
  * @return the value of the Identifier, which should be of type String, BooleanValue,
  * Integer, Long, Float, or Double, or null if the value does not exist or would not be
  * type-correct as controlled by the ignoreType argument.
  *
  * @exception BadMessageFormatMatchingException when the method is unable to determine a
  * value because the message (or other object) from which the value must be extracted is
  * corrupted or ill-formed.
  **/

 public Object getIdentifierValue(Identifier id, 
                                  boolean ignoreType)
   throws BadMessageFormatMatchingException;
 
  /**
   * Provided for use in XPath support where the MatchSpace calls MFP
   * in order to retrieve the top most Node in a DOM tree.
   * 
   * @return a root object for use as a contextValue in getIdentifierValue() calls.
   * 
   * @exception BadMessageFormatMatchingException when the method is unable to determine a
   * value because the message (or other object) from which the value must be extracted is
   * corrupted or ill-formed.
   */
  public Object getRootContext()
    throws BadMessageFormatMatchingException;
  
  /** A vacuous MatchSpaceKey that can be used to evaluate identifier-less subtrees for
   * optimization purposes.
   **/

  public MatchSpaceKey DUMMY = new MatchSpaceKey() 
  {
      public Object getIdentifierValue(Identifier id, 
                                       boolean ignoreType,
                                       Object contextValue, 
                                       boolean returnList) 
        { return null; }

      public Object getRootContext() 
      {
        return null;
      }

      public Object getIdentifierValue(Identifier id, boolean ignoreType) 
        throws BadMessageFormatMatchingException 
        {  return null; }
  };


  
}
