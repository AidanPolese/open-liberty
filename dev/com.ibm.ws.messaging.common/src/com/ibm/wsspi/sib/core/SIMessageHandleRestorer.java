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
 * 370507          060531 mphillip Original, based on JsMessageHandleFactory
 * 370507.1        060810 mphillip add specific nls messages
 * 443218          070601 susana   Add trace guard
 * ============================================================================
 */
package com.ibm.wsspi.sib.core;


import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.*;


/**
 * A singleton SIMessageHandleRestorer is created at static initialization
 * and is subsequently used for the restoration of SIMessageHandles from their
 * flattened formats.
 * This restorer is for by users of the core SPI.
 */
public abstract class SIMessageHandleRestorer {

  private static TraceComponent tc = SibTr.register(SIMessageHandleRestorer.class, com.ibm.ws.sib.utils.TraceGroups.TRGRP_MFPAPI, "com.ibm.ws.sib.mfp.CWSIFMessages");
  // message group stated as string to remove dependancy on sib.mfp

  private final static String SI_MESSAGE_HANDLE_RESTORER_CLASS     = "com.ibm.ws.sib.mfp.impl.JsMessageHandleRestorerImpl";
  private static SIMessageHandleRestorer instance = null;
  private static NoClassDefFoundError createException = null;

  static {
   
    /* Create the singleton factory instance                                  */
    try {
      createRestorerInstance();
    }
    catch (NoClassDefFoundError e) {
      FFDCFilter.processException(e, "com.ibm.ws.sib.mfp.SIMessageHandleRestorer.<clinit>", "50");
      createException = e;
    }
  }

  /**
   *  Get the singleton SIMessageHandleRestorer which is to be used for
   *  restoring SIMessageHandles from their flattened forms.
   *
   *  @return The SIMessageHandleRestorer
   */
  public static SIMessageHandleRestorer getInstance() {

    /* If instance creation failed, throw on the NoClassDefFoundError         */
    if (instance == null) {
      throw createException;
    }

    /* Otherwise, return the singleton                                        */
    return instance;
  }


  /**
   *  Restore a SIMessageHandle from a byte array.
   *
   *  @param data            The data to be restored as a SIMessageHandle.
   *
   *  @return SIMessageHandle The restored SIMessageHandle.
   *
   *  @exception IllegalArgumentException Thrown if the parameter is null or the data is not restorable.
   */
  public abstract SIMessageHandle restoreFromBytes(byte [] data) throws IllegalArgumentException;


  /**
   *  Restore a SIMessageHandle from a String.
   *
   *  @param data            The data to be restored as a SIMessageHandle
   *
   *  @return SIMessageHandle The restored SIMessageHandle.
   *
   *  @exception IllegalArgumentException Thrown if the parameter is null or the data is not restorable.
   */
  public abstract SIMessageHandle restoreFromString(String data) throws IllegalArgumentException;


  /**
   *  Create the singleton Restorer instance.
   */
  private static void createRestorerInstance() {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createRestorerInstance");
    try {
      Class cls = Class.forName(SI_MESSAGE_HANDLE_RESTORER_CLASS);
      instance = (SIMessageHandleRestorer) cls.newInstance();
    }
    catch (Exception e) {
      FFDCFilter.processException(e, "com.ibm.wsspi.sib.core.SIMessageHandleRestorer.createRestorerInstance", "100");
       SibTr.error(tc,"UNABLE_TO_CREATE_HANDLERESTORER_CWSIB0010",e);
      NoClassDefFoundError ncdfe = new NoClassDefFoundError(e.getMessage());
      ncdfe.initCause(e);
      throw ncdfe;
    }
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createRestorerInstance");
  }

}
