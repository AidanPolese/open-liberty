/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import java.security.AccessController;

import com.ibm.ejs.util.dopriv.SystemGetPropertyPrivileged;
import com.ibm.tx.jta.embeddable.GlobalTransactionSettings;
import com.ibm.websphere.csi.GlobalTranConfigData;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This class is an implementation of GlobalTranConfigData that does not
 * depend on WCCM.
 */
public class BasicGlobalTranConfigDataImpl
                implements GlobalTranConfigData, GlobalTransactionSettings
{
    private static final TraceComponent tc = Tr.register(BasicGlobalTranConfigDataImpl.class,
                                                         "EJBContainer",
                                                         "com.ibm.ejs.container.container");

    // matched the default componentTransactionTimeout value as specified in
    //  com.ibm.ejs.models.base.extensions.commonext.globaltran.impl.GlobalTransactionImpl
    protected int timeout = 0;
    protected boolean isSendWSAT = false;

    /**
     * Default constructor that is intended to be used by
     * a DefaultComponentMetaData object. Default values is used
     * for all config data.
     */
    public BasicGlobalTranConfigDataImpl()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "<init>");
    }

    @Override
    public int getTransactionTimeout()
    {
        return timeout;
    }

    @Override
    public boolean isSendWSAT()
    {
        return isSendWSAT;
    }

    @Override
    public String toString()
    {
        String separator = AccessController.doPrivileged(new SystemGetPropertyPrivileged("line.separator", "\n"));
        String sep = "                                 ";

        StringBuilder sb = new StringBuilder();
        sb.append(separator).append(sep).append("      ****** GLOBAL-TRANSACTION *******");
        sb.append(separator).append(sep).append("Timeout=").append(timeout);
        sb.append(separator).append(sep).append("isSendWSAT=").append(isSendWSAT);

        return sb.toString();
    }
}
