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
 * Reason   Date   Origin   Description
 * -------- ------ -------- --------------------------------------------------
 *                          Version 1.12 copied from CMVC
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.util;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ibm.ws.sib.mfp.MfpConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

import com.ibm.websphere.ras.TraceComponent;

/**
 * A LiteIterator is a lightweight direct implementation of an Iterator over an Object
 * array.  Its remove method should not be called.
 */

public final class LiteIterator implements Iterator {
  private static TraceComponent tc = SibTr.register(LiteIterator.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  private int index = 0;
  private int length = 0;
  private Object array;

  public LiteIterator(Object array) {
    this.array = array;
    this.length = Array.getLength(array);
  }

  public boolean hasNext() {
    return index < length;
  }

  public Object next() throws NoSuchElementException {
    try {
      return Array.get(array, index++);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      // No FFDC code needed
      throw new NoSuchElementException(e.getMessage());
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
