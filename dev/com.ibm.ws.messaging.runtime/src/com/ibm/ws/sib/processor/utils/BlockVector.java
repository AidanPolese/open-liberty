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
 * ---------------  ------ -------- --------------------------------------------
 * 171905.1         170703 gatfora  Initial implementation
 * 172296           220703 gatfora  Repackaging utility classes
 * 199212           210404 gatfora  Fixing javadoc
 * 199574           050405 gatfora  Remove use of ArrayLists.
 * 208958           110604 gatfora  Refactor LinkedList/ArrayList use
 * LIDB3706-5.247  180105 gatfora  Include a serialVersionUid for all serializable objects
 * ============================================================================
 */

package com.ibm.ws.sib.processor.utils;

// Import required classes.
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import java.util.ArrayList;

/**
 * Extends an ArrayList and adds block adds and remove operations.
 */
public final class BlockVector extends ArrayList
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 4154717074489339834L;

  private static final TraceComponent tc =
    SibTr.register(
      BlockVector.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  /**
   * Deletes the components in [firstindex, firstindex+ count-1]. Each component in
   * this vector with an index greater than firstindex+count-1
   * is shifted downward.
   * The firstindex+count-1 must be a value less than the current size of the vector.
   *
   * @exception  ArrayIndexOutOfBoundsException  if the indices was invalid.
   */
  public final void removeElementsAt(int firstindex, int count)
  {
    if (tc.isEntryEnabled())
      SibTr.entry(
        tc,
        "removeElementsAt",
        new Object[] { new Integer(firstindex), new Integer(count)});

    removeRange(firstindex, firstindex + count);

    if (tc.isEntryEnabled())
      SibTr.exit(tc, "removeElementsAt");
  }

  /**
   * Inserts count null values at index, moving up all the components at index
   * and greater. 
   * 
   * index cannot be greater than the size of the Vector.
   * 
   * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
   */
  public final void insertNullElementsAt(int index, int count)
  {
    if (tc.isEntryEnabled())
      SibTr.entry(
        tc,
        "insertNullElementsAt",
        new Object[] { new Integer(index), new Integer(count)});

    for (int i = index; i < index + count; i++)
      add(i, null);

    if (tc.isEntryEnabled())
      SibTr.exit(tc, "insertNullElementsAt");
  }

}
