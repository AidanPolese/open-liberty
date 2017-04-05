// 1.1, 10/6/06
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.cache.exception;

/**
 * Signals that the disk offload feature for the cache instance is not enabled to perform this operation.
 *
 */
public class DiskOffloadNotEnabledException extends DynamicCacheException {

    private static final long serialVersionUID = -5082196191715410157L;

    /**
     * Constructs a DiskOffloadNotEnabledException with the specified
     * detail message.
     */
    public DiskOffloadNotEnabledException(String message) {
        super(message);
    }

}


