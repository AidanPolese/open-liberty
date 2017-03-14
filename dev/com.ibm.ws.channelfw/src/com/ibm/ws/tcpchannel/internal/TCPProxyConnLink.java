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
// 03/14/06 bgower      350394          Remove TCPProxyResponse's dependance on TCPConnLink

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;

import com.ibm.wsspi.channelfw.base.OutboundConnectorLink;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 *
 *
 */
public abstract class TCPProxyConnLink extends OutboundConnectorLink implements TCPConnectionContext // @350394A
{
    abstract protected boolean isAsyncConnect();

    abstract protected boolean isSyncError();

    abstract protected void connectFailed(IOException e);
}
