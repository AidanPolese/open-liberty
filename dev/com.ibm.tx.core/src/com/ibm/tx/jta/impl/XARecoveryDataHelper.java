package com.ibm.tx.jta.impl;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011, 2012 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect    Description                                                        */
/*  --------  ----------    ------    -----------                                                        */
/*  07-09-11  johawkes      715979    Liberty: share XAResourceFactory info through Bundle Registry      */
/*  13-01-12  nyoung        726178    Liberty: 58809 Null results in XARecoveryDataHelper                */
/*  23-01-12  nyoung        726454    Liberty: 59461 Find XAResourceFactory using filter                 */
/*  01-02-12  johawkes      727155    Don't need ffdc if filter isn't valid                              */
/* ***************************************************************************************************** */

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.XAResourceFactory;
import com.ibm.tx.jta.util.TxBundleTools;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

public class XARecoveryDataHelper
{
	private static final TraceComponent tc = Tr.register(XARecoveryDataHelper.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

	public static XAResourceFactory lookupXAResourceFactory(String filter)
	{
		if (tc.isEntryEnabled()) Tr.entry(tc, "lookupXAResourceFactory", filter);

		final BundleContext bundleContext = TxBundleTools.getBundleContext();
		
		if (bundleContext == null)
		{
			if (tc.isEntryEnabled()) Tr.exit(tc, "lookupXAResourceFactory", null);
			return null;
		}

		ServiceReference[] results = null;

		try
		{
			results = bundleContext.getServiceReferences(XAResourceFactory.class.getCanonicalName(), filter);
		}
		catch (InvalidSyntaxException e)
		{
		    // Wasn't a filter
			if (tc.isEntryEnabled()) Tr.exit(tc, "lookupXAResourceFactory", "not a filter");
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
				Tr.exit(tc, "lookupXAResourceFactory", null);
			return null;
		}

		if (tc.isDebugEnabled())
        	Tr.debug(tc, "Found " + results.length + " service references in the registry");

		final XAResourceFactory xaresFactory = (XAResourceFactory) bundleContext.getService(results[0]);
		if (tc.isEntryEnabled()) Tr.exit(tc, "lookupXAResourceFactory", xaresFactory);
		return xaresFactory;
	}
}
