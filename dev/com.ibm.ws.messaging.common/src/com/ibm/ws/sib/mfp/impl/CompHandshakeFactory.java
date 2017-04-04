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
 * --------        ------ -------- -------------------------------------------
 * 172521.1        040105 baldwint Schema propagation
 * 189857          040206 susana   Add 'No FFDC code needed' comment
 * 195445.26       040514 susana   Change message prefix from SIFP to CWSIF
 * 240085          051018 kgoodson FFDC on class init failure
 * 442933          070601 susana   Add trace guard
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.impl;

import com.ibm.ws.sib.mfp.MfpConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;

/**
 * A singleton CompHandshake implementation is created at static initialization
 * and can be obtained and used by the Comms component during connection managment.
 */

public abstract class CompHandshakeFactory {
  private static TraceComponent tc = SibTr.register(CompHandshakeFactory.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  private static Object instance = null;
  private static Exception createException = null;

  static {
  
    // Create the singleton implementation
    try {
      createHandshakeInstance();
    } catch (Exception e) {
      FFDCFilter.processException(e, "com.ibm.ws.sib.mfp.CompHandshakeFactory.<clinit>", "53");
      createException = e;
    }
  }

  /**
   * Get the singleton CompHandshake object which wiull be called by
   * the Comms component during connection setup and closedown.
   *
   * @return The CompHandshake implementation.  Note the return type here is Object (to
   * avoid circular dependencies in the naff build process) but it will always be an
   * instance of <code>com.ibm.ws.sib.comms.CompHandshake</code>.
   * @exception Exception The method rethrows any Exception caught during
   * creaaton of the singleton object.
   */
  public static Object getInstance() throws Exception {
    // If instance creation failed, throw on the Exception
    if (instance == null)
      throw createException;

    // Otherwise, return the singleton
    return instance;
  }

  /**
   * Create the singleton ComponentHandshake instance.
   *
   * @exception Exception The method rethrows any Exception caught during
   * creaton of the singleton object.
   */
  private static void createHandshakeInstance() throws Exception {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createHandshakeInstance");
    try {
      instance = Class.forName(MfpConstants.COMP_HANDSHAKE_CLASS).newInstance();
    } catch (Exception e) {
      FFDCFilter.processException(e, "com.ibm.ws.sib.mfp.CompHandshakeFactory.createHandshakeInstance", "88");
      SibTr.error(tc, "UNABLE_TO_CREATE_COMPHANDSHAKE_CWSIF0051", e);
      throw e;
    }
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createHandshakeInstance");
  }
}
