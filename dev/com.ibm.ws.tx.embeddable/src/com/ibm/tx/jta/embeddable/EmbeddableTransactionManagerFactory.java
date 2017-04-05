package com.ibm.tx.jta.embeddable;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2010 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* @(#) 1.6 SERV1/ws/code/tx.embeddable/src/com/ibm/tx/jta/EmbeddableTransactionManagerFactory.java, WAS.transactions, WASX.SERV1 5/12/10 08:45:39 [6/27/11 12:14:30]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Developer  Defect         Description                                                      */
/*  --------  ---------  ------         -----------                                                      */
/*  09-11-03  johawkes   F743-305.1     Creation                                                         */
/*  09-11-09  johawkes   F743-305.1     EJB 3.1                                                          */
/*  09-12-08  johawkes   631451         Use loadClassFromKey                                             */
/*  10-01-06  johawkes   632997         Try to do the right thing in a client                            */
/*  10-05-12  johawkes   587993         Fix trace                                                        */
/* ***************************************************************************************************** */

//import static com.ibm.tx.jta.TransactionManagerFactory._tranManager;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.ExtendedTransactionManager;
import com.ibm.tx.jta.embeddable.impl.EmbeddableTranManagerSet;
import com.ibm.tx.ltc.embeddable.impl.EmbeddableLocalTranCurrentSet;
import com.ibm.ws.LocalTransaction.LocalTransactionCurrent;
import com.ibm.ws.Transaction.UOWCurrent;
import com.ibm.ws.tx.embeddable.EmbeddableWebSphereTransactionManager;

/**
 * This class provides access for app server components to the TransactionManager.
 * An instance can be obtained through the static method getTransactionManager().
 * In addition to the javax.transaction.TransactionManager interface, the returned
 * instance also provides methods to enlist and delist resources with transactions,
 * as defined in the WebSphereTransactionManager interface.
 * 
 * <p> This class is private to WAS.
 * Any use of this class outside the WAS Express/ND codebase
 * is not supported.
 * 
 */
public class EmbeddableTransactionManagerFactory extends com.ibm.tx.jta.TransactionManagerFactory {
    private static final TraceComponent tc = Tr.register(EmbeddableTransactionManagerFactory.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    protected static LocalTransactionCurrent _localTranCurrent;

    private static final String clientTMKey = "com.ibm.ws.transaction.NonRecovWSTxManager";
    private static final String tmImplFactoryKey = "com.ibm.tx.jta.embeddable.transactionManager";
    private static final String ltcsImplFactoryKey = "com.ibm.ws.transaction.LocalTranCurrent";

    //
    // This is a factory class that should not be instantiated - police use of the constructor.
    //
    protected EmbeddableTransactionManagerFactory() {}

    /**
     * This method returns the underlying implementation of the TransactionManager. Use of this object
     * replaces use of the old JTSXA instance and static methods.
     */
    public static EmbeddableWebSphereTransactionManager getTransactionManager() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getTransactionManager");

        if (_tranManager == null) {
            loadEmbeddableTranManager(tmImplFactoryKey);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getTransactionManager", _tranManager);
        return (EmbeddableWebSphereTransactionManager) _tranManager;
    }

    private static synchronized void loadEmbeddableTranManager(String key) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "loadEmbeddableTranManager", key);

        // TODO this method gets called to load two different things but sets static variables?
        // TODO HOLLY LIBERTY use services
        _tranManager = EmbeddableTranManagerSet.instance();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "loadEmbeddableTranManager", new Object[] { key, _tranManager });
    }

    public static LocalTransactionCurrent getLocalTransactionCurrent() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getLocalTransactionCurrent");

        if (_localTranCurrent instanceof EmbeddableLocalTranCurrentSet) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "getLocalTransactionCurrent", _localTranCurrent);
            return _localTranCurrent;
        }

        loadEmbeddableLocalTranCurrent();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getLocalTransactionCurrent", _localTranCurrent);
        return _localTranCurrent;
    }

    private static synchronized void loadEmbeddableLocalTranCurrent() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "loadEmbeddableLocalTranCurrent");

        // TODO use services 
        //             final Class<?> c = ImplFactory.loadClassFromKey(ltcsImplFactoryKey);
        // TODO LIBERTY HOLLY don't just revert to hardcode
        _localTranCurrent = EmbeddableLocalTranCurrentSet.instance();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "loadEmbeddableLocalTranCurrent");
    }

    public static UOWCurrent getUOWCurrent() {
        final UOWCurrent uowc = (UOWCurrent) getTransactionManager();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getUOWCurrent", uowc);
        return uowc;
    }

    public static void setTransactionManager(EmbeddableTranManagerSet tm) {
        _tranManager = tm;
    }

    /**
     * Must only be called in a client
     * 
     * @return
     */
    public static ExtendedTransactionManager getClientTransactionManager() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getClientTransactionManager");

        if (_tranManager == null) {
            loadEmbeddableTranManager(clientTMKey);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getClientTransactionManager", _tranManager);
        return _tranManager;
    }
}