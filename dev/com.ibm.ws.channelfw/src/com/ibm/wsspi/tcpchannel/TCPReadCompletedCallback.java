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
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.

package com.ibm.wsspi.tcpchannel;

import java.io.IOException;

import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * A callback object whose methods are called by the TCPChannel upon
 * the completion (or error) of a readAsynch request.
 * 
 * @ibm-spi
 */
public interface TCPReadCompletedCallback {

    /**
     * Called when the request has completeted successfully.
     * 
     * @param vc
     *            vitual connection associated with this request.
     * @param rsc
     *            the TCPReadRequestContext associated with this request.
     */
    void complete(VirtualConnection vc, TCPReadRequestContext rsc);

    /**
     * Called back if an exception occurres while processing the request.
     * The implementer of this interface can then decide how to handle this
     * exception.
     * 
     * @param vc
     *            vitual connection associated with this request.
     * @param rsc
     *            the TCPReadRequestContext associated with this request.
     * @param ioe
     *            the exception.
     */
    void error(VirtualConnection vc, TCPReadRequestContext rsc, IOException ioe);

}
