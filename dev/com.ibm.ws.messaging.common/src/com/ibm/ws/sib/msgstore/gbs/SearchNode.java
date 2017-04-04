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
 * This defines the point at which a key was found.
 *
 *
 * @author Stewart L. Palmer
 */

class SearchNode
{
  SearchNode()
  {
    reset();
  }

  public String toString()
  {
    String x;
    x = "foundNode = "    + foundNode()    + "\n" +
        "foundIndex = "   + foundIndex();
    return x;
  }

  GBSNode foundNode()
  { return _foundNode; }

  int foundIndex()
  { return _foundIndex; }

  boolean wasFound()
  { return (_notFound == false); }

  /**
   * Return the reference to the contained Object if any.
   */
  Object key()
  { return _obj; }

  void setFound(
    GBSNode      foundNode,
    int          foundIndex)
  {
    _foundNode  = foundNode;
    _foundIndex = foundIndex;
    _notFound = false;
  }

  /**
   * Remember the location of a found key.
   *
   * @param obj The object that was found in the index.
   */
   void setLocation(
    Object      obj)
  {
    _obj = obj;
  }

  private Object       _obj;
  private GBSNode      _foundNode;
  private int          _foundIndex;
  private boolean      _notFound;

  void reset()
  {
    _obj = null;
    _foundNode = null;
    _foundIndex = 0;
    _notFound = true;
  }

}
