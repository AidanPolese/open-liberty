package com.ibm.tx.jta.embeddable.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2008 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* @(#) 1.43 SERV1/ws/code/was.transaction.impl/src/com/ibm/ws/Transaction/wstx/WSATRecoveryCoordinator.java, WAS.transactions, WAS855.SERV1, cf061521.02 8/26/08 09:03:36 [6/12/15 06:28:21]                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect       Description                                                     */
/*  --------  ----------    ------       -----------                                                     */
/*  03/01/31  dmatthew      LI1922       creation                                                        */
/*  04/07/01  dmatthew      199789.1     WSAddressing repackaging                                        */
/*  04/07/08  dmatthew      210276.1     WSAddressing HA changes                                         */
/*  04/08/13  dmatthew      215995       WSAT fault code                                                 */
/*  04/09/15  hursdlg       229924       Bypass AttributedURI toString                                   */
/*  04/09/07  dmatthew      228062       registration thread model change                                */
/*  04/09/28  johawkes      235214       Fix trace group and imports                                     */
/*  04/10/07  johawkes      235471.2     Change registration                                             */
/*  04/10/07  maples        233147.1     Updated wsat, wscoor and wsa namespace                          */
/*  04/10/18  awilkins      235214.1     Servicability - improve trace                                   */
/*  04/09/28  johawkes      233868       Prevent oneway in WSATSystemContextHandler                      */
/*  05/01/13  kaczyns       249345       Add SUUID as per serialscan target                              */
/*  05/01/18  johawkes      249940.1     Refactor WSATControlSet                                         */
/*  05/01/24  johawkes      250784       Handle volatile participants correctly                          */
/*  19/02/05  johawkes      LIDB3605-32  Secure protocol messages                                        */
/*  05/05/16  hursdlg       274187       Make SUID match 601/602                                         */
/*  05/06/14  dmatthew      279131       WS-Addressing changes                                           */
/*  05/08/02  johawkes      LIB3462-28.1 WS-A interface changes                                          */
/*  05/08/12  johawkes      LIB3462-28.4 More WS-A interface changes                                     */
/*  05/09/16  johawkes      LIB3462-3.NS7 Set namespace date on EPRs                                     */
/*  05/09/30  johawkes      309089       Refactor EAL4 support                                           */
/*  05/10/04  johawkes      310062       Set destination EPR                                             */
/*  05/10/10  johawkes      308638       Don't set wsa action                                            */
/*  05/10/18  hursdlg       LIDb3187-3   z/os wsat based off 3187 jts code                               */
/*  05/10/27  johawkes      316435.1     getGlobalGlobalID                                               */
/*  05/11/03  johawkes      319724       resend prepared on timeout not replay                           */
/*  06/01/17  johawkes      338736       Use proper target for responses                                 */
/*  06/01/26  hursdlg       338736.1     Move z/os specifics to WSATCRRecoveryCoordinator                */
/*  06/02/23  dmatthew      348864       explicitly set fragile on EPRs                                  */
/*  06/04/07  johawkes      360812       Make right EPR secure in coord()                                */
/*  06/07/23  johawkes      LIDB4401-36.02 WSFP base enablement                                          */
/*  07/01/05  dmatthew      PK37117      Fix HA                                                          */
/*  07/03/26  dmatthew      PK41882      CICS interop - send replyTo on replayOperation                  */
/*  07/09/14  johawkes      LIDB2778-12  WS-TX 1.1                                                       */
/*  07/09/20  hursdlg       LIDB2778-12  WS-TX 1.1 point 5 feature                                       */
/*  07/09/25  johawkes      LIDB2778-12  .03                                                             */
/*  07/10/04  johawkes      LIDB2778-12  .06                                                             */
/*  08/04/14  hursdlg       509776.1     Heuristics support                                              */
/*  08/05/14  hursdlg       414814       Encode branch on fault epr for zos                              */
/*  08/08/26  johawkes      545531       Ensure WSATServices in initialized in resendPrepared            */
/* ***************************************************************************************************** */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.transaction.SystemException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.util.TxBundleTools;
import com.ibm.tx.remote.RecoveryCoordinator;
import com.ibm.tx.remote.RecoveryCoordinatorFactory;
import com.ibm.tx.remote.RecoveryCoordinatorNotAvailableException;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public final class WSATRecoveryCoordinator implements RecoveryCoordinator, Serializable
{
    // 601 SUID based on private final data and non-public ctor
    private static final long serialVersionUID = 5500037426315245114L; /* @274187C */

    private static transient final TraceComponent tc = Tr.register(WSATRecoveryCoordinator.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private final String _recoveryCoordinatorFactoryFilter;
    private final Serializable _recoveryCoordinatorInfo;
    private final String _globalId;

    private transient RecoveryCoordinator rc;

    /**
     * @param recoveryCoordinatorFactoryFilter
     * @param recoveryCoordinatorInfo
     * @param globalId
     */
    public WSATRecoveryCoordinator(String recoveryCoordinatorFactoryFilter, Serializable recoveryCoordinatorInfo, String globalId) {
        _recoveryCoordinatorFactoryFilter = recoveryCoordinatorFactoryFilter;
        _recoveryCoordinatorInfo = recoveryCoordinatorInfo;
        _globalId = globalId;
    }

    // As called after recovery on distributed platform
    public static WSATRecoveryCoordinator fromLogData(byte[] bytes) throws SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "fromLogData", bytes);

        WSATRecoveryCoordinator wsatRC = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            final ObjectInputStream ois = new ObjectInputStream(bais);
            final Object obj = ois.readObject();
            wsatRC = (WSATRecoveryCoordinator) obj;
        } catch (Throwable e) {
            FFDCFilter.processException(e, "com.ibm.ws.Transaction.wstx.WSATRecoveryCoordinator.fromLogData", "67");

            final Throwable se = new SystemException().initCause(e);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "fromLogData", se);
            throw (SystemException) se;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "fromLogData", wsatRC);
        return wsatRC;
    }

    public String getGlobalId()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getGlobalId", _globalId);
        return _globalId;
    }

    // Only called on distributed - z/OS logging is done by WSATCRRecoveryCoordinator
    public byte[] toLogData() throws javax.transaction.SystemException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "toLogData", this);

        byte[] logData = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            logData = baos.toByteArray();
        } catch (Exception e) {
            FFDCFilter.processException(e, "com.ibm.ws.Transaction.wstx.WSATRecoveryCoordinator.toLogData", "118", this);
            if (tc.isEventEnabled())
                Tr.event(tc, "Exception caught in toLogData " + e);

            final Throwable se = new SystemException().initCause(e);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "toLogData", se);
            throw (SystemException) se;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "toLogData", logData);
        return logData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.tx.remote.RecoveryCoordinator#replayCompletion(java.lang.String)
     */
    @Override
    public void replayCompletion(String globalId) {
        if (rc == null) {
            // Find a factory to create the RecoveryCoordinator
            try {
                rc = getRecoveryCoordinator();
            } catch (RecoveryCoordinatorNotAvailableException e) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "replayCompletion", e);
            }
        }

        if (rc != null) {
            rc.replayCompletion(globalId);
        }
    }

    private RecoveryCoordinator getRecoveryCoordinator() throws RecoveryCoordinatorNotAvailableException {
        try {
            final RecoveryCoordinatorFactory factory = lookupRecoveryCoordinatorFactory(_recoveryCoordinatorFactoryFilter);

            if (factory == null) {
                throw new RecoveryCoordinatorNotAvailableException();
            }

            return factory.getRecoveryCoordinator(_recoveryCoordinatorInfo);
        } catch (RecoveryCoordinatorNotAvailableException e) {
            throw e;
        } catch (Exception e) {
            throw new RecoveryCoordinatorNotAvailableException(e);
        }
    }

    public static RecoveryCoordinatorFactory lookupRecoveryCoordinatorFactory(String filter)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "lookupRecoveryCoordinatorFactory", filter);

        final BundleContext bundleContext = TxBundleTools.getBundleContext();

        if (bundleContext == null)
        {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "lookupRecoveryCoordinatorFactory", null);
            return null;
        }

        ServiceReference[] results = null;

        try
        {
            results = bundleContext.getServiceReferences(RecoveryCoordinatorFactory.class.getCanonicalName(), filter);
        } catch (InvalidSyntaxException e) {
            // Wasn't a filter
            if (tc.isEntryEnabled())
                Tr.exit(tc, "lookupRecoveryCoordinatorFactory", "not a filter");
            return null;
        }

        if (results == null || results.length <= 0) {
            if (results == null) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Results returned from registry are null");
            } else {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Results of length " + results.length + " returned from registry");
            }
            if (tc.isEntryEnabled())
                Tr.exit(tc, "lookupRecoveryCoordinatorFactory", null);
            return null;
        }

        if (tc.isDebugEnabled())
            Tr.debug(tc, "Found " + results.length + " service references in the registry");

        final RecoveryCoordinatorFactory recoveryCoordinatorFactory = (RecoveryCoordinatorFactory) bundleContext.getService(results[0]);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "lookupRecoveryCoordinatorFactory", recoveryCoordinatorFactory);
        return recoveryCoordinatorFactory;
    }
}