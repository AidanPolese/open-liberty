/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.metadata;

import com.ibm.ejs.csi.BasicGlobalTranConfigDataImpl;
import com.ibm.tx.jta.embeddable.GlobalTransactionSettings;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.javaee.dd.commonext.GlobalTransaction;
import com.ibm.ws.javaee.dd.ejbext.EnterpriseBean;

public class GlobalTranConfigDataImpl extends BasicGlobalTranConfigDataImpl implements GlobalTransactionSettings {

    private static final TraceComponent tc = Tr.register(GlobalTranConfigDataImpl.class);

    public GlobalTranConfigDataImpl(EnterpriseBean enterpriseBeanExtension) {

        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();

        if (enterpriseBeanExtension != null) {
            if (isTraceOn && tc.isDebugEnabled())
                Tr.debug(tc, "CTOR was passed non-null EnterpriseBeanExtension object for config data");

            GlobalTransaction globalTransaction = enterpriseBeanExtension.getGlobalTransaction();

            if (globalTransaction != null) {
                if (isTraceOn && tc.isDebugEnabled())
                    Tr.debug(tc, "We have a globlTransaction object, so use the 5.0 or later config data");

                timeout = globalTransaction.getTransactionTimeOut();
                isSendWSAT = globalTransaction.isSendWSATContext();
            }
        }
    }
}
