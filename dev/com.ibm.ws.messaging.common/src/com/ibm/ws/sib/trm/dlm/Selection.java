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
 * LIDB2117        041103 vaughton Original
 * 290290.1        051101 gelderd  Improved entry/exit trace for sib.trm
 * 365894          060707 matrober Indicate reason for non-authoritative selection
 * 499369          080220 djvines  Use autoboxing for trace
 * 500222          080225 sibcopyr Automatic update of trace guards 
 * ============================================================================
 */

package com.ibm.ws.sib.trm.dlm;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.trm.TrmConstants;
import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This class wrappers a messaging engine uuid with additional information
 * about how the selected messaging engine was selected by the Destination
 * Location Manager.
 */

public final class Selection {

  private static final TraceComponent tc = SibTr.register(Selection.class, TrmConstants.MSG_GROUP, TrmConstants.MSG_BUNDLE);

  private SIBUuid8 uuid;
  private boolean  authoritative;
  private String nonAuthoritativeReason = null;

  public Selection (SIBUuid8 u, boolean a) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "Selection", new Object[] { u, a });

    uuid = u;
    authoritative = a;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "Selection", this);
  }

  /**
   * Alternative constructor for non-authoritative choices which allows a reason
   * string to be provided which describes why the choice was non-authoritative.
   * @param u UUID of the messaging engine that this selection represents
   * @param nonAuthDescr Description of why this choice is not authoritative.
   */
  public Selection (SIBUuid8 u, String nonAuthDescr)
  {
    this(u, false);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "Selection", new Object[] { u, nonAuthDescr });
    nonAuthoritativeReason = nonAuthDescr;
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "Selection", this);
  }

  /**
   * Method used to retrieve the selected messaging engine uuid
   *
   * @return SIBUuid8 the uuid of the selected messaging engine
   */

  public SIBUuid8 getUuid() {
    return uuid;
  }

  /**
   * Method used to find out whether the selected messaging engine uuid was
   * authoritively selected or not (so is a best guess).
   *
   * @return boolean true if the answer is authoritative
   */

  public boolean isAuthoritative() {
    return authoritative;
  }

  // Utility methods

  public String toString () {
    return "uuid="+uuid.toString()+",authoritative="+authoritative+
      ((nonAuthoritativeReason == null) ? "" : ",nonAuthoritativeReason=["+nonAuthoritativeReason+"]");
  }

}
