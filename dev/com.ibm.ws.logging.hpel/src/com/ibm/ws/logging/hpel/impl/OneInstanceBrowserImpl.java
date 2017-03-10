//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * 696303             8.0       03/23/2011     belyi      Original check-in
 */
package com.ibm.ws.logging.hpel.impl;

import java.util.Map;

import com.ibm.ws.logging.hpel.LogRepositoryBrowser;
import com.ibm.ws.logging.hpel.MainLogRepositoryBrowser;
import com.ibm.ws.logging.object.hpel.RepositoryPointerImpl;

/**
 * Implementation of the browser over instances when selected directory is an instance itself.
 */
public class OneInstanceBrowserImpl implements MainLogRepositoryBrowser {

	private final LogRepositoryBrowser browser;
	/**
	 * Initialize this main browser with a browser over files in the instance
	 * @param browser providing details about the instance
	 */
	public OneInstanceBrowserImpl(LogRepositoryBrowser browser) {
		this.browser = browser;
	}
	
	@Override
	public LogRepositoryBrowser find(RepositoryPointerImpl location,
			boolean ignoreTimestamp) {
		String[] instanceIds = location.getInstanceIds();
		if (instanceIds.length == 0) {
			return null;
		}
		LogRepositoryBrowser result = browser;
		for (int i=1; i<instanceIds.length && result!=null; i++) {
			Map<String, LogRepositoryBrowser> map = result.getSubProcesses();
			result = map.get(instanceIds[i]);
		}
		return result;
	}

	@Override
	public LogRepositoryBrowser findByMillis(long timestamp) {
		return timestamp<0 || browser.getTimestamp()<=timestamp ? browser : null;
	}

	@Override
	public LogRepositoryBrowser findNext(LogRepositoryBrowser current,
			long timelimit) {
		if (current == null) {
			return browser;
		} else {
			return null;
		}
	}

	@Override
	public LogRepositoryBrowser findNext(RepositoryPointerImpl location,
			long timelimit) {
		return null;
	}

}
