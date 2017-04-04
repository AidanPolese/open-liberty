package com.ibm.tx.jta.util;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect   Description                                                         */
/*  --------  ----------    ------   -----------                                                         */
/*  07-09-11  nyoung        715979   Liberty: share XAResourceFactory info through Bundle Registry       */
/* ***************************************************************************************************** */


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

/**
 * This class provides the implementation of an OSGi Bundle Activator. It allows the transactions bundle
 * to gain access to the BundleContext when the OSGi framework starts.
 *
 */
public class TxBundleTools implements BundleActivator
{
	private static final TraceComponent tc = Tr.register(TxBundleTools.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

	static BundleContext _bc;

	@Override
	public void start(BundleContext bundleContext) throws Exception
	{
		if (tc.isDebugEnabled()) Tr.debug(tc, "start", bundleContext);
		_bc = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception
	{
		if (tc.isDebugEnabled()) Tr.debug(tc, "stop", bundleContext);
		_bc = null;
	}

	public static BundleContext getBundleContext()
	{
		if (tc.isDebugEnabled()) Tr.debug(tc, "getBundleContext", _bc);
		return _bc;
	}
}
