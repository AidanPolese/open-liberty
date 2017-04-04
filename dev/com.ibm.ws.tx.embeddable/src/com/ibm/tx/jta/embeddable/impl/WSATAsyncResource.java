package com.ibm.tx.jta.embeddable.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2008 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* @(#) 1.44 SERV1/ws/code/was.transaction.impl/src/com/ibm/ws/Transaction/wstx/WSATAsyncResource.java, WAS.transactions, WAS855.SERV1, cf061521.02 9/10/08 05:48:06 [6/12/15 06:28:22]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date(YYMMDD) Programmer    Defect       Description                                                  */
/*  -----------  ----------    ------       -----------                                                  */
/*  03/01/31     dmatthew      LI1922       creation                                                     */
/*  04/06/10     dmatthew      205640.1     WS-Addressing coreq changes                                  */
/*  04/07/01     dmatthew      199789.1     WSAddressing repackaging                                     */
/*  04/07/08     dmatthew      210276.1     WSAddressing HA changes                                      */
/*  04/08/13     dmatthew      215995       WSAT fault code                                              */
/*  04/08/23     dmatthew      224452.1     WSAddressing coreq                                           */
/*  04/08/20     dmatthew      220510       WSAT HA code drop                                            */
/*  04/09/20     hursdlg       233078       Log more information                                         */
/*  04/09/28     johawkes      235214       Fix trace group and imports                                  */
/*  04/10/07     johawkes      235471.2     Change registration                                          */
/*  04/10/07     maples        233147.1     Updated wsat, wscoor and wsa namespace                       */
/*  04/10/18     awilkins      235214.1     Servicability - improve trace                                */
/*  05/01/13     kaczyns       249345       Add SUUID as per serialscan target                           */
/*  05/01/18     johawkes      249940.1     Refactor WSATControlSet                                      */
/*  05/01/24     johawkes      250784       Handle volatile participants correctly                       */
/*  05/02/16     dmatthew      251554       Fix WSAT outbound                                            */
/*  19/02/05     johawkes      LIDB3605-32  Secure protocol messages                                     */
/*  05/03/03     dmatthew      257583       WSAT log raw mapping data not serialized java objects        */
/*  05/05/16     hursdlg       274187       Make SUID match 601/602                                      */
/*  05/06/14     dmatthew      279131       WS-Addressing changes                                        */
/*  05/08/02     johawkes      LIB3462-28.1 WS-A interface changes                                       */
/*  05/08/12     johawkes      LIB3462-28.4 More WS-A interface changes                                  */
/*  05/09/16     johawkes      LIB3462-3.NS7 Set namespace date on EPRs                                  */
/*  05/09/30     johawkes      309089       Refactor EAL4 support                                        */
/*  05/10/04     johawkes      310062       Set destination EPR                                          */
/*  05/10/10     johawkes      308638       Don't set wsa action                                         */
/*  05/10/18     hursdlg       LIDB3187-3   z/os wsat based off 3187 jts code                            */
/*  06/01/26     hursdlg       338736.1     Move z/os srerialization to z/os code                        */
/*  06/02/23     dmatthew      348864       explicitly set fragile on EPRs                               */
/*  06/05/08     johawkes      367307       Use virtual host name                                        */
/*  06/06/12     kaczyns       370212       z/OS requires prepare to be authorized for naming lookup     */
/*  07/01/05     dmatthew      PK37117      Fix HA                                                       */
/*  07/09/14     johawkes      LIDB2778-12  WS-TX 1.1                                                    */
/*  07/09/20     hursdlg       LIDB2778-12  WS-TX 1.1 point 5 feature                                    */
/*  07/09/25     johawkes      LIDB2778-12  .03                                                          */
/*  07/10/04     johawkes      LIDB2778-12  .06                                                          */
/*  08/04/04     hursdlg       509776       Heuristic support                                            */
/*  08/05/14     hursdlg       414814       Unique ref parm for WSAT                                     */
/*  08/09/09     hursdlg       545849       Drive RMFAIL if HA retry needed                              */
/* ***************************************************************************************************** */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.XAResourceFactory;
import com.ibm.tx.jta.XAResourceNotAvailableException;
import com.ibm.tx.jta.impl.XARecoveryDataHelper;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This class defines a 2PC participant and allows the TransactionManager to communicate with it via web services
 * 
 * This is the thing we're gonna construct from what we're given and is what we're gonna log
 */

public final class WSATAsyncResource implements Serializable
{
    /**  */
    private static final long serialVersionUID = 4244509484588694781L;

    private static transient final TraceComponent tc = Tr.register(WSATAsyncResource.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private final Xid _xid;
    protected final String _XAResourceFactoryFilter;
    protected final Serializable _XAResourceFactoryKey;

    public WSATAsyncResource(String factoryFilter, Serializable key, Xid xid)
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "WSATAsyncResource", new Object[] { factoryFilter, key, xid });

        _xid = xid;
        _XAResourceFactoryFilter = factoryFilter;
        _XAResourceFactoryKey = key;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "WSATAsyncResource", this);
    }

    String getTxIdentifier()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getTxIdentifier", _xid);
        return _xid.toString();
    }

    // This is what we call to get the answer back. All the wsat happens here
    // It's gonna be called asynchronously
    public int prepareOperation() throws XAException, XAResourceNotAvailableException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "prepareOperation", new Object[] { this });

        final int retVal = getXAResource().prepare(_xid);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "prepareOperation", retVal);
        return retVal;
    }

    public void commitOperation() throws XAException, XAResourceNotAvailableException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "commitOperation", new Object[] { this });

        getXAResource().commit(_xid, false);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "commitOperation");
    }

    public void rollbackOperation() throws XAException, XAResourceNotAvailableException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "rollbackOperation", new Object[] { this });

        getXAResource().rollback(_xid);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "rollbackOperation");
    }

    public void forgetOperation() throws XAException, XAResourceNotAvailableException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "forgetOperation", new Object[] { this });

        getXAResource().forget(_xid);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "forgetOperation");
    }

    // Distributed logData call only - z/os encodes log data in WSATCRAsyncResource
    byte[] toLogData() throws javax.transaction.SystemException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "toLogData", this);

        byte[] logData = null;

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            logData = baos.toByteArray();
        } catch (Exception e)
        {
            FFDCFilter.processException(e, "com.ibm.ws.Transaction.wstx.WSATAsyncResource.toLogData", "279", this);

            final SystemException se = new SystemException();
            se.initCause(e);

            if (tc.isEntryEnabled())
                Tr.exit(tc, "toLogData", se);
            throw se;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "toLogData", logData);
        return logData;
    }

    // Distributed logData call only - z/os encodes log data in WSATCRAsyncResource
    static WSATAsyncResource fromLogData(byte[] bytes) throws javax.transaction.SystemException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "fromLogData", bytes);

        WSATAsyncResource resource = null;
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        try
        {
            final ObjectInputStream ois = new ObjectInputStream(bais);
            final Object obj = ois.readObject();
            resource = (WSATAsyncResource) obj;
        } catch (Exception e)
        {
            FFDCFilter.processException(e, "com.ibm.ws.Transaction.wstx.WSATAsyncResource.fromLogData", "307");

            final SystemException se = new SystemException();
            se.initCause(e);

            if (tc.isEntryEnabled())
                Tr.exit(tc, "fromLogData", se);
            throw se;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "fromLogData", resource);
        return resource;
    }

    String describe()
    {
        return _XAResourceFactoryKey.toString();
    }

    Serializable getKey() {
        return _XAResourceFactoryKey;
    }

    private XAResource getXAResource() throws XAResourceNotAvailableException {
        try {
            final XAResourceFactory factory = XARecoveryDataHelper.lookupXAResourceFactory(_XAResourceFactoryFilter);

            if (factory == null) {
                throw new XAResourceNotAvailableException();
            }

            return factory.getXAResource(_XAResourceFactoryKey);
        } catch (XAResourceNotAvailableException e) {
            throw e;
        } catch (Exception e) {
            throw new XAResourceNotAvailableException(e);
        }
    }
}