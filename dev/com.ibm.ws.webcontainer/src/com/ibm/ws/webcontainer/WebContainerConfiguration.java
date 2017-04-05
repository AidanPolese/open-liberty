// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//351467        03/01/06    todkap              CodeReview: 4408 needs to add ResourceDiagnostic support    WASCC.web.webcontainer
//363663        04/18/06    todkap              LIDB4408: add webcontainer property disablePooling
//LIDB3518-1.2    06/26/07       mmolden          ARD
//

//Code added as part of LIDB 2283-4
package com.ibm.ws.webcontainer;
// Begin 277095
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wsspi.webcontainer.WebContainerConfig;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;


public class WebContainerConfiguration implements WebContainerConfig
{
    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.WebContainerConfiguration";
	
	private static Properties _localeProps = null;
	private static Properties _jvmProps = null;
	// End 277095
	
	private boolean enableServletCaching = false;
	private String defaultVirtualHostName = null;
	//          LIDB3518-1.2    06/26/07       mmolden          ARD
	private boolean ardEnabled = false;
	private int ardIncludeTimeout = 0;
	//          LIDB3518-1.2    06/26/07       mmolden          ARD
	private boolean poolingDisabled = false;
	private int maximumExpiredEntries;
	private int maximumResponseStoreSize;
	private boolean useAsyncRunnableWorkManager;
	private String asyncRunnableWorkManagerName;
	
	// use values of 2 and 30000, which are the defaults in the tWAS WCCM models
	private int numAsyncTimerThreads = 2;
	private long defaultAsyncServletTimeout = 30000;

	public WebContainerConfiguration()
	{
	}

	/**
	 * Returns the defaultVirtualHostName.
	 * @return String
	 */
	public String getDefaultVirtualHostName() {
		return defaultVirtualHostName;
	}

	/**
	 * Returns the enableServletCaching.
	 * @return boolean
	 */
	public boolean isEnableServletCaching() {
		return enableServletCaching;
	}

	/**
	 * Sets the defaultVirtualHostName.
	 * @param defaultVirtualHostName The defaultVirtualHostName to set
	 */
	public void setDefaultVirtualHostName(String defaultVirtualHostName) {
		this.defaultVirtualHostName = defaultVirtualHostName;
	}

	/**
	 * Sets the enableServletCaching.
	 * @param enableServletCaching The enableServletCaching to set
	 */
	public void setEnableServletCaching(boolean enableServletCaching) {
		this.enableServletCaching = enableServletCaching;
	}
	
	// Begin 277095
	public Properties getLocaleProps (){
		return _localeProps;
		
	}
	
	public Properties getConverterProps () {
		return _jvmProps;
	}

	
	/**
	 * @param props The _jvmProps to set.
	 */
	public static void setConverterProps(Properties props) {
		_jvmProps = props;
	}
	/**
	 * @param props The _localeProps to set.
	 */
	public static void setLocaleProps(Properties props) {
		_localeProps = props;
	}
	// End 277095
	
	/**
	 * @return Returns the poolingDisabled.
	 */
	public boolean isPoolingDisabled() {
		return this.poolingDisabled;
	}
	/**
	 * @param disable The poolingdisabled to set.
	 */
	public void setPoolingDisabled(boolean disable) {
		this.poolingDisabled = disable;
	}
//          LIDB3518-1.2    06/26/07       mmolden          ARD
	/**
	 * @return Returns the ardEnabled.
	 */
	public boolean isArdEnabled() {
		return ardEnabled;
	}

	/**
	 * @param ardEnabled The ardEnabled to set.
	 */
	public void setArdEnabled(boolean ardEnabled) {
		this.ardEnabled = ardEnabled;
	}

	/**
	 * @return Returns the ardIncludeTimeout.
	 */
	public int getArdIncludeTimeout() {
		return ardIncludeTimeout;
	}

	/**
	 * @param ardIncludeTimeout The ardIncludeTimeout to set.
	 */
	public void setArdIncludeTimeout(int ardIncludeTimeout) {
		this.ardIncludeTimeout = ardIncludeTimeout;
	}
//          LIDB3518-1.2    06/26/07       mmolden          ARD
	
	public int getMaximumExpiredEntries(){
		return maximumExpiredEntries;
	}
	

	public int getMaximumResponseStoreSize(){
		return maximumResponseStoreSize;
	}

	public void setMaximumExpiredEntries(int maximumExpiredEntries) {
		this.maximumExpiredEntries = maximumExpiredEntries;
	}
	
	public void setMaximumResponseStoreSize(int maximumResponseStoreSize) {
		this.maximumResponseStoreSize = maximumResponseStoreSize;
	}

	@Override
	public void setUseAsyncRunnableWorkManager(
			boolean useAsyncRunnableWorkManager) {
		this.useAsyncRunnableWorkManager = useAsyncRunnableWorkManager;
	}

	@Override
	public void setAsyncRunnableWorkManagerName(
			String asyncRunnableWorkManagerName) {
		this.asyncRunnableWorkManagerName = asyncRunnableWorkManagerName;
	}

	@Override
	public void setNumAsyncTimerThreads(int numAsyncTimerThreads) {
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  
            logger.logp(Level.FINE, CLASS_NAME,"setNumAsyncTimerThreads", "numAsyncTimerThreads --> " + numAsyncTimerThreads);

		if (numAsyncTimerThreads>=1)
			this.numAsyncTimerThreads = numAsyncTimerThreads;
		else 
            logger.logp(Level.WARNING, CLASS_NAME,"setNumAsyncTimerThreads", "trying.to.set.number.of.async.timer.threads.to.less.than.one");
			
	}

	@Override
	public boolean isUseAsyncRunnableWorkManager() {
		return useAsyncRunnableWorkManager;
	}
	
	@Override
	public String getAsyncRunnableWorkManagerName() {
		return asyncRunnableWorkManagerName;
	}
	
	@Override
	public int getNumAsyncTimerThreads() {
		return numAsyncTimerThreads;
	}

	@Override
	public void setDefaultAsyncServletTimeout(long defaultAsyncServletTimeout2) {
		this.defaultAsyncServletTimeout = defaultAsyncServletTimeout2;
	}
	
	@Override
	public long getDefaultAsyncServletTimeout() {
		return defaultAsyncServletTimeout;
	}
}
