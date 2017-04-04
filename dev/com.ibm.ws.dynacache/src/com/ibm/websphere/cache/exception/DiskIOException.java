// 1.1, 10/6/06
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.cache.exception;

/**
 * Signals that a disk IO exception has occurred when writing the cache entry to the disk cache.
 * When this exception is encountered, the Dynamic cache will automatically turn off the 
 * disk offload feature.
 */
public class DiskIOException extends DynamicCacheException {

    private static final long serialVersionUID = 1390857807469146766L;

    /**
     * Constructs a DiskIOException with the specified detail message.
     */
    public DiskIOException(String message) {
        super(message);
    }

}


