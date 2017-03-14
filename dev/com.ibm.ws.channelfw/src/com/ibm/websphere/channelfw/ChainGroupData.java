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
//@(#) 1.5 SERV1/ws/code/channelfw/src/com/ibm/websphere/channel/framework/ChainGroupData.java, WAS.channelfw, WASX.SERV1 5/26/04 08:38:09 [8/28/04 13:40:10]

package com.ibm.websphere.channelfw;

import java.io.Serializable;

/**
 * ChainGroupData provides information specifically about a logical group of Transport Chains
 * and the attributes of that grouping.
 * 
 * @ibm-api
 */
public interface ChainGroupData extends Serializable {

    /**
     * Get the name of this chain group.
     * 
     * @return String
     */
    String getName();

    /**
     * Get the list of chains in this group.
     * 
     * @return ChainData[]
     */
    ChainData[] getChains();

    /**
     * Returns whether or not the input chain is included in this group.
     * 
     * @param chainName Name of a Transport Chain.
     * @return boolean Whether or not the input chain is included in this group.
     */
    boolean containsChain(String chainName);
}
