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
 * A callback object whose methods are called by the TCPChannel
 * upon the completion (or error) of a writeAsynch request.
 * 
 * @ibm-spi
 */
public interface TCPWriteCompletedCallback {
    /**
     * Called when the request has completeted successfully.
     * 
     * @param vc
     *            vitual connection associated with this request.
     * @param wsc
     *            the TCPWriteRequestContext associated with this request.
     */
    public void complete(VirtualConnection vc, TCPWriteRequestContext wsc);

    /**
     * Called back if an exception occurres while processing the request.
     * The implementer of this interface can then decide how to handle this
     * exception.
     * 
     * @param vc
     *            vitual connection associated with this request.
     * @param wsc
     *            the TCPWriteRequestContext associated with this request.
     * @param ioe
     *            the exception.
     */
    public void error(VirtualConnection vc, TCPWriteRequestContext wsc, IOException ioe);

}
