// 1.1, 8/31/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache.exception;

/**
 * The Dynamic cache service has not started. The servlet or object caching operation will be aborted.
 */
public class DynamicCacheServiceNotStarted extends DynamicCacheException {

    static final long serialVersionUID = -8035956532047048631L;
    
    /**
     * Constructs a DynamicCacheServiceNotStarting with the specified detail message.
     */
    public DynamicCacheServiceNotStarted(String message) {
        super(message);
    }

}
