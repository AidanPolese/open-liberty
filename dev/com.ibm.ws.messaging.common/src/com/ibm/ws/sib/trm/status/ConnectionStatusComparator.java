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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        030721 vaughton Original
 * ============================================================================
 */

package com.ibm.ws.sib.trm.status;

/*
 * This class compares ConnectionStatus objects
 */

public final class ConnectionStatusComparator implements java.util.Comparator {

  // If o1 comes before o2 return -1
  // If o1 == o2 return 0
  // If o1 comes after o2 return 1

  public int compare (Object o1, Object o2) {

    String re1 = ((ConnectionStatus)o1).getRemoteEngineName();
    String re2 = ((ConnectionStatus)o2).getRemoteEngineName();

    return re1.compareTo(re2);
  }

}
