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
//HISTORY
//~~~~~~~
// 
//Change ID    Date      Author    Abstract
//---------    --------  --------  ---------------------------------------
//305918       09/16/05  clanzen   Add chain data property key
//======================================================================== */

package com.ibm.ws.channelfw.internal;

/**
 * This purpose of this interface is to consolidate Strings used throughout
 * the Channel Framework to prevent future changes from rippling to all
 * files.
 */
public interface ChannelFrameworkConstants {

    /** Trace group id used for the framework */
    String BASE_TRACE_NAME = "ChannelFramework";
    /** Resource bundle used for the framework */
    String BASE_BUNDLE = "com.ibm.ws.channelfw.internal.resources.ChannelfwMessages";

    /** Property name used by connector channels to store the hostname. */
    String HOST_NAME = "hostname";

    /**
     * Property name used by connector channels to store the port speicified in
     * the config.
     */
    String PORT = "port";

    /**
     * Property name used by connector channels to store the port actually being
     * used.
     */
    String LISTENING_PORT = "listeningPort";

    /**
     * Property put into ChannelData property map when calling
     * ChannelFactory.findOrCreateChannel().
     */
    String CHAIN_DATA_KEY = "chainData";
}
