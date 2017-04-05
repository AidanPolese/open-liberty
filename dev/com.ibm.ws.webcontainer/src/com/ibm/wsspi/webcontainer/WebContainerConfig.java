// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer;

import java.util.Properties;


/**
 *
* 
* WebContainerConfig is used to get WebContainer level config data such as
* default virtual host name and current caching status.
* @ibm-private-in-use
 */
public interface WebContainerConfig {

	/**
	 * Returns the defaultVirtualHostName.
	 * @return String
	 */
	public String getDefaultVirtualHostName();

	/**
	 * Returns the enableServletCaching.
	 * @return boolean
	 */
	public boolean isEnableServletCaching();

	public Properties getLocaleProps ();
	
	public Properties getConverterProps ();

	public boolean isArdEnabled();
	
	public int getArdIncludeTimeout();
	public int getMaximumExpiredEntries();
	public int getMaximumResponseStoreSize();

	void setUseAsyncRunnableWorkManager(boolean useAsyncRunnableWorkManager);

	void setAsyncRunnableWorkManagerName(String asyncRunnableWorkManagerName);

	void setNumAsyncTimerThreads(int numAsyncTimerThreads);

	boolean isUseAsyncRunnableWorkManager();

	String getAsyncRunnableWorkManagerName();

	int getNumAsyncTimerThreads();

	void setDefaultAsyncServletTimeout(long defaultAsyncServletTimeout);

	long getDefaultAsyncServletTimeout();


}
