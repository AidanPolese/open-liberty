/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010,2011 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  yy-mm-dd  Developer  Defect  Description                                         */
/*  --------  ---------  ------  -----------                                         */
/*  10-01-28  johawkes   F743-3356.1 Rudimentary initial implementation              */
/*  10-03-15  johawkes   643417  Use container-wide LPS switch                       */
/*  11-11-24  johawkes   723423  Repackaging                                         */
/* ********************************************************************************* */

package com.ibm.tx.jta.embeddable.config;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProvider;
import com.ibm.tx.config.RuntimeMetaDataProvider;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class EmbeddableRuntimeMetaDataProviderImpl implements RuntimeMetaDataProvider
{
    private static final TraceComponent tc = Tr.register(EmbeddableRuntimeMetaDataProviderImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private final ConfigurationProvider _cp;

    public EmbeddableRuntimeMetaDataProviderImpl(ConfigurationProvider configurationProvider)
    {
        _cp = configurationProvider;
    }

    @Override
    public int getTransactionTimeout()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getTransactionTimeout", 0);
        return 0;
    }

    @Override
    public boolean isClientSideJTADemarcationAllowed()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "isClientSideJTADemarcationAllowed", Boolean.FALSE);
        return false;
    }

    @Override
    public boolean isHeuristicHazardAccepted()
    {
        // Don't support per module LPS enablement in embeddable container
        // so just use configuration provider setting derived from properties
        final boolean ret = _cp.isAcceptHeuristicHazard();

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "isHeuristicHazardAccepted", ret);
        return ret;
    }

    @Override
    public boolean isUserTransactionLookupPermitted(String name)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "isUserTransactionLookupPermitted", Boolean.TRUE);
        return true;
    }
}