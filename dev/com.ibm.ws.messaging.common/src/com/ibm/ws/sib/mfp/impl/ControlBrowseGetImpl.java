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
 * 178364          031106 gatfora  Original
 * 178364.2        031231 susana   Tidy up code
 * 186248.1.6      040520 susana   Remove EnvelopeType part 2
 * 207007.3        040610 baldwint Add selectorDomain attribute
 * 180483.15       040630 baldwint Add disciminator attribute
 * 215177          040423 susana   Change Control Messages to single part messages
 * LIDB3706-5.228  050125 susana   Add serialVersionUID
 * 442933          070531 susana   Add trace guard
 * 599149          090701 pbroad   Add minimal ME<->ME comms trace 
 * 605141          090825 pbroad   Use StringBuilder instead of StringBuffer for trace summary line
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.mfp.MessageDecodeFailedException;
import com.ibm.ws.sib.mfp.MfpConstants;
import com.ibm.ws.sib.mfp.control.ControlBrowseGet;
import com.ibm.ws.sib.mfp.control.ControlMessageType;
import com.ibm.ws.sib.mfp.schema.ControlAccess;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 *  ControlBrowseGetImpl extends ControlMessageImpl and hence JsMessageImpl,
 *  and is the implementation class for the ControlBrowseGet interface.
 */
public class ControlBrowseGetImpl extends ControlMessageImpl implements ControlBrowseGet {

  private final static long serialVersionUID = 1L;

  private final static TraceComponent tc = SibTr.register(ControlBrowseGetImpl.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  /**
   *  Constructor for a new Control Browse Get Message.
   *
   *  This constructor should never be used except by JsMessageImpl.createNew().
   *  The method must not actually do anything.
   */
  public ControlBrowseGetImpl() {
  }

  /**
   *  Constructor for a new Control Browse Get Message.
   *  To be called only by the ControlMessageFactory.
   *
   *  @param flag No-op flag to distinguish different constructors.
   *
   *  @exception MessageDecodeFailedException Thrown if such a message can not be created
   */
  public ControlBrowseGetImpl(int flag) throws MessageDecodeFailedException {
    super(flag);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>");
    setControlMessageType(ControlMessageType.BROWSEGET);
  }

  /**
   *  Constructor for an inbound message.
   *  (Only to be called by a superclass make method.)
   *
   *  @param inJmo The JsMsgObject representing the inbound message
   */
  public ControlBrowseGetImpl(JsMsgObject inJmo) {
    super(inJmo);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>, inbound jmo ");
  }

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#getBrowseID()
   */
  public final long getBrowseID() {
    return jmo.getLongField(ControlAccess.BODY_BROWSEGET_BROWSEID);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#getSequenceNumber()
   */
  public final long getSequenceNumber() {
    return jmo.getLongField(ControlAccess.BODY_BROWSEGET_SEQUENCENUMBER);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#getFilter()
   */
  public final String getFilter() {
    return (String)jmo.getField(ControlAccess.BODY_BROWSEGET_FILTER);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#getSelectorDomain()
   */
  public int getSelectorDomain() {
    return jmo.getIntField(ControlAccess.BODY_BROWSEGET_SELECTORDOMAIN);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#getControlDiscriminator()
   */
  public String getControlDiscriminator() {
    return (String)jmo.getField(ControlAccess.BODY_BROWSEGET_DISCRIMINATOR);
  }

  /*
   * Get summary trace line for this message 
   * 
   *  Javadoc description supplied by ControlMessage interface.
   */
  public void getTraceSummaryLine(StringBuilder buff) {
    
    // Get the common fields for control messages
    super.getTraceSummaryLine(buff);
    
    buff.append(",browseID=");
    buff.append(getBrowseID());
    
    buff.append(",sequenceNumber=");
    buff.append(getSequenceNumber());
    
    buff.append(",filter=");
    buff.append(getFilter());
    
    buff.append(",selectorDomain=");
    buff.append(getSelectorDomain());
    
    buff.append(",controlDiscriminator=");
    buff.append(getControlDiscriminator());
    
  }

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#setBrowseID(long)
   */
  public final void setBrowseID(long value) {
    jmo.setLongField(ControlAccess.BODY_BROWSEGET_BROWSEID, value);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#setSequenceNumber(long)
   */
  public final void setSequenceNumber(long value) {
    jmo.setLongField(ControlAccess.BODY_BROWSEGET_SEQUENCENUMBER, value);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#setFilter(java.lang.String)
   */
  public final void setFilter(String value) {
    jmo.setField(ControlAccess.BODY_BROWSEGET_FILTER, value);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#setSelectorDomain(int)
   */
  public void setSelectorDomain(int value) {
    jmo.setIntField(ControlAccess.BODY_BROWSEGET_SELECTORDOMAIN, value);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlBrowseGet#setControlDiscriminator(java.lang.String)
   */
  public void setControlDiscriminator(String value) {
    jmo.setField(ControlAccess.BODY_BROWSEGET_DISCRIMINATOR, value);
  }
}
