/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.env;

import java.util.Hashtable;
import java.util.Properties;

import com.ibm.wsspi.security.wim.exception.WIMException;

/**
 * Interface for SSL utilities
 */
public interface ISSLUtil {
	/**
     * Set SSL properties on the thread 
	 */
    public void setSSLPropertiesOnThread(Properties props);
    
    /**
     * Get the SSL properties that are set on the thread
     */
	public Properties getSSLPropertiesOnThread();
    
	/**
	 * Set the SSL Alias
	 * 
	 * @param sslAlias
	 * @param ldapEnv
	 * @throws WIMException
	 */
    public void setSSLAlias(String sslAlias, Hashtable<?, ?> ldapEnv) throws WIMException ;
    
    /**
     * Reset the SSL Alias
     */
    public void resetSSLAlias();
}
