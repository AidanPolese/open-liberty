// 1.1, 10/6/06
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.cache.exception;

/**
 * Signals that a runtime exception other than a disk IOException has occurred when 
 * writing the cache entry to the disk cache.
 */
public class MiscellaneousException extends DynamicCacheException {

    private static final long serialVersionUID = -733655168072791778L;

    /**
     * Constructs an MiscellaneousException with the specified detail message.
     */
    public MiscellaneousException(String message) {
        super(message);
    }

}


