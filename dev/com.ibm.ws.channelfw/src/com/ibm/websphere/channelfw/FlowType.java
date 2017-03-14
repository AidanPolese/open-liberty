//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// @(#) 1.6 SERV1/ws/code/channelfw/src/com/ibm/websphere/channel/framework/FlowType.java, WAS.channelfw, WASX.SERV1 7/2/04 10:06:20 [8/28/04 13:40:13]
//
package com.ibm.websphere.channelfw;

import java.io.Serializable;

/**
 * The FlowType class is an enumerated type used among the entire framework to specify
 * whether this is Inbound or Outbound connections.
 * <p>
 * <ul>
 * <li><b> Inbound </b> - Inbound chain types are the equivalent to server
 * transports. These server transports initiate
 * connections at the lowest layer, generally from
 * a physical network connection from a client or
 * a virtual connection from another piece of code
 * trying to push data to a server.
 * <li><b> Outbound </b> - Outbound chains are the equivalent to client
 * transports. These client transports create new
 * connections and send them off to a server transport
 * which is often remote on another network.
 * </ul>
 * 
 * @ibm-api
 */
public class FlowType implements Comparable<FlowType>, Serializable {

    /**
     * INBOUND (server side) FlowType.
     */
    public static final FlowType INBOUND;

    /**
     * OUTBOUND (client side) FlowType.
     */
    public static final FlowType OUTBOUND;

    /**
     * list of values
     */
    private static FlowType _values[];

    /** Serialization ID string */
    private static final long serialVersionUID = -147726133665159700L;

    static {
        _values = new FlowType[2];
        _values[0] = (INBOUND = new FlowType(0));
        _values[1] = (OUTBOUND = new FlowType(1));
    }

    /**
     * ordinal for this flow type
     */
    private final int _ordinal;

    // -------------------------------------------------------------------------
    // Private Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param ordinal
     */
    private FlowType(int ordinal) {
        this._ordinal = ordinal;
    }

    /**
     * Fetch the FlowType for a particular integer.
     * 
     * @param ordinal The integer to find a FlowType for.
     * @return FlowType, null if it does match a defined FlowType
     */
    public static FlowType getKey(int ordinal) {
        if (ordinal >= 0 && ordinal < _values.length) {
            return _values[ordinal];
        }
        return null;
    }

    /**
     * Get the ordinal of a particular FlowType.
     * 
     * @return int
     */
    public int getOrdinal() {
        return this._ordinal;
    }

    /*
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(FlowType o) {
        if (o == null) {
            return -1;
        }
        return this._ordinal - o._ordinal;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || !(o instanceof FlowType)) {
            return false;
        }
        return hashCode() == o.hashCode();
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this._ordinal;
    }
}
