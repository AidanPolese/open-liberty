/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal;

import javax.ejb.EJBObject;
import javax.ejb.Handle;

import com.ibm.websphere.csi.CSIException;
import com.ibm.websphere.csi.StatefulSessionHandleFactory;
import com.ibm.ws.ejb.portable.HandleImpl;

public class SessionHandleFactoryImpl implements StatefulSessionHandleFactory {
    @Override
    public Handle create(EJBObject object) throws CSIException {
        return new HandleImpl(object);
    }
}
