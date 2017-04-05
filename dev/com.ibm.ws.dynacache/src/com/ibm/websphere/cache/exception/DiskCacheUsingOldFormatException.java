// 1.1, 2/28/07
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache.exception;

/**
 * @author Andy Chow
 * 
 * Signals that the disk cache is using old format in data structure..
 */
public class DiskCacheUsingOldFormatException extends DynamicCacheException {  // LI4337-17
	
	private static final long serialVersionUID = -3760842684658943228L;

	/**
     * Constructs a DiskCacheEntrySizeOverLimitException with the specified detail message.
     */
	public DiskCacheUsingOldFormatException(String message) {
		super(message);
	}
}
