// 1.1, 10/6/06
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.cache.exception;

/**
 * Signals that a generic cache exception has occurred. This class is the base class for 
 * the specific exceptions thrown by Dynamic cache. If a DynamicCacheException occurs while
 * writing the cache entry to disk cache, the cache entry and its related entries are removed
 * from the memory and disk cache. 
 */
public abstract class DynamicCacheException extends Exception {

    private static final long serialVersionUID = -4998690760275949098L;

    /**
     * Constructs a DynamicCacheException with the specified detail message.
     */
    public DynamicCacheException(String message) {
        super(message);
    }

}


