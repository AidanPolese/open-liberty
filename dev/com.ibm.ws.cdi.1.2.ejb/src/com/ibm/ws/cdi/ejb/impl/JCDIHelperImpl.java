/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.ejb.impl;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.ejbcontainer.JCDIHelper;

public class JCDIHelperImpl implements JCDIHelper {

    public static final JCDIHelper instance = new JCDIHelperImpl();

    @Override
    public Class<?> getFirstEJBInterceptor(J2EEName j2eeName, Class<?> ejbImpl) {
        return WeldSessionBeanInterceptorWrapper.class;
    }

    @Override
    public Class<?> getEJBInterceptor(J2EEName j2eeName, Class<?> ejbImpl) {
        return EJBCDIInterceptorWrapper.class;
    }

}
