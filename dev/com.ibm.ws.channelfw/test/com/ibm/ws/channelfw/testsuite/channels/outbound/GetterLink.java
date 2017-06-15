/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.channelfw.testsuite.channels.outbound;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.OutboundProtocolLink;

/**
 * Test outbound application link object.
 */
@SuppressWarnings("unused")
public class GetterLink extends OutboundProtocolLink {

    /**
     * Constructor.
     */
    public GetterLink() {
        super();
    }

    protected void postConnectProcessing(VirtualConnection conn) {
        // nothing
    }

    public Object getChannelAccessor() {
        return null;
    }

}
