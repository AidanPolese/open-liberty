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
 * Reason     Date   Origin   Description
 * --------   ------ -------- -------------------------------------------------
 * 162584     030328 auerbach Original
 * 177749.1   030925 auerbach Add support for WDO over JMF
 * 199196     040422 baldwint Javadoc fixes
 * 427963     070322 susana   Remove unused changed, atypicalChanges & JSField priority functions
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/**
 * The JMFFieldDef interface is a superinterface for those nodes that have accessors
 * assigned to them, namely, JMFPrimitiveType, JMFEnumType, JMFDynamicType, and
 * JMFVariantType.
 */

public interface JMFFieldDef extends JMFType {

  /**
   * Retrieve the accessor associated with this field relative to its public schema
   * (ignoring variant boxes)
   */
  public int getAccessor();

  /**
   * Retrieve the accessor associated with this field relative to some enclosing schema,
   * which may be its public schema or the schema of an enclosing variant box.
   *
   * @param schema the enclosing schema for which an accessor is desired
   * @return the desired accessor or -1 if the schema argument is not a valid enclosing
   *   schema
   */
  public int getAccessor(JMFSchema schema);

}
