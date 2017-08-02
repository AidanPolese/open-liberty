/*******************************************************************************
 * Copyright (c) 1997, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.http.channel.h2internal;

import com.ibm.ws.transport.access.TransportConnectionAccess;
import com.ibm.ws.transport.access.TransportConnectionUpgrade;

/**
 *
 */
public class H2UpgradeHandler implements TransportConnectionUpgrade {

    // HttpUpgradeHandler method
    public void destroy() {
        System.out.println("H2UpgradeHandler: inside destroy");
    }

    @Override
    // TransportConnectionUpgrade method
    public void init(TransportConnectionAccess x) {
        System.out.println("H2UpgradeHandler: inside init(TransportConnectionAccess x)");
    }

}
