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
 * LIDB2117        030916 vaughton New MP/TRM interface
 * 290290.1        051101 gelderd  Improved entry/exit trace for sib.trm
 * 499369          080221 djvines  Use autoboxing for trace
 * 500222          080225 sibcopyr Automatic update of trace guards 
 * ============================================================================
 */

package com.ibm.ws.sib.trm.topology;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.trm.TrmConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import java.io.UnsupportedEncodingException;

/**
 * This class represents a LinkCellule. A LinkCellule is constructed from the
 * names of the two subnets which the LinkCellule acts as an interface between.
 * LinkCellule names are directional in that the LinkCellule that links subnets
 * 'Alpha' and 'Beta' is called [Alpha:Beta] while LinkCellule [Beta:Alpha]
 * exists at the other end of the link.
 */

public final class LinkCellule extends Cellule {

  private static final String className = LinkCellule.class.getName();
  private static final TraceComponent tc = SibTr.register(LinkCellule.class, TrmConstants.MSG_GROUP, TrmConstants.MSG_BUNDLE);
  private static final TraceNLS nls = TraceNLS.getTraceNLS(TrmConstants.MSG_BUNDLE);

  private static final String DELIM = ":";

  private final String subnet1;
  private final String subnet2;

  /**
   * Constructor
   *
   * @param subnet1 name of source subnet
   * @param subnet2 name of target subnet
   */

  public LinkCellule (String subnet1, String subnet2) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "LinkCellule", new Object[] { subnet1, subnet2 });

    if (subnet1 == null) {
      throw new NullPointerException(nls.getFormattedMessage(
          "NULL_USED_TO_CREATE_CWSIT0012", new Object[] { "String (subnet1)",
              "LinkCellule" }, null));
    }

    if (subnet2 == null) {
      throw new NullPointerException(nls.getFormattedMessage(
          "NULL_USED_TO_CREATE_CWSIT0012", new Object[] { "String (subnet2)",
              "LinkCellule" }, null));
    }

    this.subnet1 = subnet1;
    this.subnet2 = subnet2;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "LinkCellule", this);
  }

  /**
   * Constructor used to recreate a LinkCellule from a byte[] previously
   * obtained using the getBytes() method.
   *
   * UTF-8 encoding is used to construct the LinkCellule.
   *
   * @param b saved byte array
   */

  public LinkCellule (byte[] b) throws InvalidBytesException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "LinkCellule", new Object[] { b });

    if (b[0] == Cellule.LINKCELLULE) {

      String cellule = null;
      try {
        cellule = new String(b, 1, b.length-1, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        FFDCFilter.processException(e, className + ".<init>", "1", this);
      }

      // Locate the delimiter character and separate out the two subnet names

      final int os = cellule.indexOf(DELIM);

      if (os > 0) {
        subnet1 = cellule.substring(0,os);
        subnet2 = cellule.substring(os+1);
      } else {
        if (os == 0) {
          subnet1 = "";
          subnet2 = cellule.substring(os+1);
        } else {                    // os < 0 means DELIM not found!
          subnet1 = cellule;
          subnet2 = "";
        }
      }

    } else {
      throw new InvalidBytesException(nls.getFormattedMessage(
          "INVALID_BYTE_VALUE_CWSIT0053", new Object[] {
              Cellule.LINKCELLULE, b[0] }, null));
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "LinkCellule", this);
  }

  /**
   * Return a byte[] representation of the LinkCellule. The returned bytes can be
   * used to create a new LinkCellule object representing the same LinkCellule.
   *
   * UTF-8 encoding is used to construct the byte array.
   */

  public byte[] getBytes() {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "getBytes");

    byte[] b = null;
    try {
      b = string().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      FFDCFilter.processException(e, className + ".getBytes", "2", this);
    }

    byte[] c = new byte[b.length+1];

    c[0] = Cellule.LINKCELLULE;
    for (int i=0; i < b.length; i++) {
      c[i+1] = b[i];
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "getBytes", c);
    return c;
  }

 // Utility methods

  public String getSubnet1 () {
    return subnet1;
  }

  public String getSubnet2 () {
    return subnet2;
  }

  private String string () {
    return subnet1 + DELIM + subnet2;
  }

  public boolean equals (Object o) {
    boolean rc = false;

    if (o instanceof LinkCellule) {
      LinkCellule l = (LinkCellule)o;
      rc = subnet1.equals(l.subnet1) && subnet2.equals(l.subnet2);
    }

    return rc;
  }

  private int hashcode;

  public int hashCode () {

    if (hashcode == 0) {
      hashcode = string().hashCode();
    }

    return hashcode;
  }

  public String toString () {
    return "[" + string() + "]";
  }

}
