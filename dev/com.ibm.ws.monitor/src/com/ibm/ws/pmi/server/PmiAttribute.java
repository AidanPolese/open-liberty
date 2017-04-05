// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
/**
 * 
 * History:
 */

package com.ibm.ws.pmi.server;

public class PmiAttribute implements java.io.Serializable {
    private static final long serialVersionUID = 4849590234346163606L;
    String name;
    long value;

    public PmiAttribute(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public long getValue() {
        return value;
    }

    /**
     * Define all the names available in PmiAttribute.
     */
    public final static String NUM_THREADS_IN_POOL = "NumThreadsInPool",
                         NUM_CREATED_THREADS = "NumCreatedThreads",
                         CONNECTION_POOL_SIZE = "ConnectionPoolSize";

}
