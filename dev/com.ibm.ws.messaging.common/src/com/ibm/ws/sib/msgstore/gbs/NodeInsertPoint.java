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
 * This is the point within a node AFTER which
 * a new key is to be inserted.
 *
 * @author Stewart L. Palmer
 */

public class NodeInsertPoint
{
  boolean isDuplicate()
  { return _isDuplicate; }

  int insertPoint()
  { return _insertPoint; }

  void markDuplicate(
    int         ipt)
  {
    setInsertPoint(ipt);
    _isDuplicate = true;
  }

  void setInsertPoint(
    int         ipt)
  {
    _insertPoint = (short) ipt;
    if (ipt != (int) _insertPoint)
      {
        String x =
        "Insert point won't fit in a short.  Supplied insert point = " + ipt;
        throw new IllegalArgumentException(x);
      }
    _isDuplicate = false;
  }

  public String toString()
  {
    String x;
    if (isDuplicate())
      x = "*duplicate* at " + insertPoint();
    else
      x = insertPoint() + "";
    return x;
  }

  private boolean _isDuplicate;
  private short   _insertPoint = -100;
}
