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
 * Change activity :
 *
 * Reason         Date    Origin    Description
 * ------------  ------  --------  --------------------------------------------
 *   176001      090903  corrigk   Original
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.gbs;

/**
 * This is the comparator that is used when we want to find the first
 * record in the index whose value is greater than or equal to the
 * supplied key.  An equal compare is turned into a "less than".
 *
 * @author Stewart L. Palmer
 */

class SearchComparatorGE extends SearchComparator
{

  SearchComparatorGE(
    java.util.Comparator   comparator)
  { super(comparator); }

  public int compare(
    Object     o1,
    Object     o2)
  {
    int result = super.internalCompare(o1, o2);
    if (result == 0)
      result = -9;
    return result;
  }

  public int type()
  { return SearchComparator.GE; }

}
