// 1.5, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.persistent.htod;

/**
 * Thrown to indicate that some component of an ODG does not exist.
 *
 */
public class HashtableOnDiskException extends RuntimeException {
    
    private static final long serialVersionUID = -5948997263475181113L;
    
    /**
     * Constructs an NoSuchObjectException with the specified
     * detail message.
     */
    public  HashtableOnDiskException(String message) {
        super(message);
    }

    public  HashtableOnDiskException(String message, Exception e) {
        super(message);
        wrapped_exception = e;
    }

    private Exception wrapped_exception;
    public Exception getWrappedException() { return wrapped_exception; }
}


