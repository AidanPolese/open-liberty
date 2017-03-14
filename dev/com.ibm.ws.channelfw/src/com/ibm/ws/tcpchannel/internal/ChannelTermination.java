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
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------

package com.ibm.ws.tcpchannel.internal;

/**
 * When a TCP channel factory is being stopped, this termination handle is
 * called for each channel owned by that factory. The various channel types
 * must implement this interface and perform appropriate action during
 * termination, whatever that means for each type.
 */
public interface ChannelTermination {

    /**
     * Signal to the individual TCP channel to terminate.
     */
    void terminate();
}
