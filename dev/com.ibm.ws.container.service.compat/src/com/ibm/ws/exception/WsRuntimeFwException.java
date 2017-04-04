/* ============================================================================
 * @(#) 1.5 SERV1/ws/code/runtime.fw/src/com/ibm/ws/exception/WsRuntimeFwException.java, WAS.runtime.fw, WAS80.SERV1, kk1041.02 3/13/08 13:14:47 [10/22/10 00:56:42]
 *  
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change activity:
 *
 * Reason          Date     userid    Description
 * -------         ------   --------  -----------------------------------------
 * LIDB3706-6      050214   dzavala   Add SUID  
 * D363517.1       060421   ericvn    Add to websphere-apis.jar        
 * LIDB3491-1      070328   ericvn    Provide supported interfaces
 * D410379         080313   bkail     Add reported member variable
 * ============================================================================ */
package com.ibm.ws.exception;

/**
 * A <code>WsRuntimeFwException</code> and its subclasses are conditions that
 * the runtime framework might want to catch.
 * <p>
 * <b>Note:</b> Since the Application Server only supports JDK 1.4 and newer JDKs, new exceptions should extend <code>java.lang.Exception</code> which supports exception chaining.
 * 
 * @ibm-spi
 */
public class WsRuntimeFwException extends WsException {

    private static final long serialVersionUID = -8155373574187853057L;

    transient boolean reported; // D410379

    /**
     * Constructs a new <code>WsRuntimeFwException</code> with <code>null</code> as its detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}
     */
    public WsRuntimeFwException() {
        super();
    }

    /**
     * Constructs a new <code>WsRuntimeFwException</code> with the specified
     * detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}
     * 
     * @param s
     *            the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public WsRuntimeFwException(String s) {
        super(s);
    }

    /**
     * Constructs a new <code>WsRuntimeFwException</code> with the specified
     * cause and a detail message of <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of <tt>cause</tt>).
     * 
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables.
     * 
     * @param t
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public WsRuntimeFwException(Throwable t) {
        super(t);
    }

    /**
     * Constructs a new <code>WsRuntimeFwException</code> with the specified
     * detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail message.
     * 
     * @param s
     *            the detail message (which is saved for later retrieval
     *            by the {@link #getMessage()} method).
     * @param t
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public WsRuntimeFwException(String s, Throwable t) {
        super(s, t);
    }

}
