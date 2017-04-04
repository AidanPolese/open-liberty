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
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.5         190603 nyoung   Integrate MatchSpace with MP.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.12        121103 nyoung   Remove support for BooleanValue and NumericValue
 * 199185           220404 gatfora  Fix Javadoc.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.sib.matchspace.SimpleTest;
import com.ibm.ws.sib.matchspace.selector.impl.EvaluatorImpl;
/**
 * This class maintains a table of ranged values with associated targets for those
 * values.  When given a value to search, it returns all targets that meet the range.
 *
 * @author Daniel Sturman
 */
public class CheapRangeTable {

  public static int numTables = 0;
  public static int numEntries = 0;

  int size = 0;

  RangeEntry[] ranges;

  /**
   * Create a new table.
   */
  public CheapRangeTable() {
    ranges = new RangeEntry[3];
  }


  /**
   * Insert a range and an associated target into the table.
   *
   * @param test a SimpleTest of kind==NUMERIC containing the range
   * @param target The target associated with the range.
   */
  public void insert(SimpleTest test, Object target) {
    if (size == ranges.length) {
      RangeEntry[] tmp = new RangeEntry[2*size];
      System.arraycopy(ranges,0,tmp,0,size);
      ranges = tmp;
    }

    ranges[size] = new RangeEntry(test, target);
    size++;
  }

  /** Retrieve the Object associated with an exactly defined range */

  public Object getExact(SimpleTest test) {
    for (int i = 0; i < size; i++) {
      if (ranges[i].correspondsTo(test))
        return ranges[i].target;
    }
    return null;
  }

  /** Replace the Object in a range that is known to exist */

  public void replace(SimpleTest test, Object target) {
    for (int i = 0; i < size; i++)
      if (ranges[i].correspondsTo(test)) {
        ranges[i].target = target;
        return;
      }
    throw new IllegalStateException();
  }

  /**
   * Find targets associated with all ranges including this value.
   *
   * @param value Value to search for.
   * @return List of all targets found.
   */
  public List find(Number value) { // was NumericValue
    List targets = new ArrayList(1);

    for (int i = 0; i < size; i++) {
      if (ranges[i].contains(value))
        targets.add(ranges[i].target);
    }

    return targets;
  }


  public boolean isEmpty() {
    return size == 0;
  }

  public void remove(SimpleTest test) {
    if (size == 0)
      throw new IllegalStateException();

    int toGo = -1;
    for (int i = 0; toGo < 0 && i < size; i++) {
      if (ranges[i].correspondsTo(test))
        toGo = i;
    }

    if (toGo < 0)
      throw new IllegalStateException();

    System.arraycopy(ranges,toGo+1,ranges,toGo,size-toGo-1);
    size--;
  }

  class RangeEntry {
    Number lower; // was NumericValue
    boolean lowIncl;
    Number upper; // was NumericValue
    boolean upIncl;
    Object target;
    RangeEntry(SimpleTest test, Object t) {
      lower = test.getLower();
      lowIncl = test.isLowIncl();
      upper = test.getUpper();
      upIncl = test.isUpIncl();
      target = t;
    }
    boolean correspondsTo(SimpleTest t) {
      if (lowIncl != t.isLowIncl() || upIncl != t.isUpIncl())
        return false;
      if (lower == null)
        if (t.getLower() != null)
          return false;
        else;
      else if (t.getLower() == null)
        return false;
      else if (!EvaluatorImpl.equals(lower, t.getLower()))
        return false;
      if (upper == null)
        if (t.getUpper() != null)
          return false;
        else;
      else if (t.getUpper() == null)
        return false;
      else if (!EvaluatorImpl.equals(upper, t.getUpper()))
        return false;
      return true;
    }
    boolean contains(Number v) {
      if (lower != null) {
        int comp = EvaluatorImpl.compare(lower,v);
        if (comp > 0 || !lowIncl && comp == 0)
          return false;
      }
      if (upper != null) {
        int comp = EvaluatorImpl.compare(upper, v);
        if (comp < 0 || !upIncl && comp == 0)
          return false;
      }
      return true;
    }
  }
}
