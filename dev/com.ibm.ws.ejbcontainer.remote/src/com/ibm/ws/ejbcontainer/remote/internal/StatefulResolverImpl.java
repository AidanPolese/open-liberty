/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.remote.internal;

import org.apache.yoko.orb.spi.naming.Resolver;
import org.omg.CORBA.TRANSIENT;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.ejs.container.EJSHome;

public class StatefulResolverImpl extends Resolver {
    private final BeanMetaData bmd;
    private final int interfaceIndex;

    public StatefulResolverImpl(BeanMetaData bmd, int interfaceIndex) {
        this.bmd = bmd;
        this.interfaceIndex = interfaceIndex;
    }

    @Override
    public org.omg.CORBA.Object resolve() {
        EJSHome home = bmd.homeRecord.getHomeAndInitialize();
        try {
            return (org.omg.CORBA.Object) home.createRemoteBusinessObject(interfaceIndex, null);
        } catch (Exception e) {
            TRANSIENT e2 = new TRANSIENT();
            e2.initCause(e);
            throw e2;
        }
    }
}
