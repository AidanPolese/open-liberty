/* ============================================================================
 * @(#) 1.7 SERV1/ws/code/runtime.fw/src/com/ibm/ws/exception/ComponentDisabledException.java, WAS.runtime.fw, WAS80.SERV1, kk1041.02 3/29/07 15:02:54 [10/22/10 00:56:32]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change activity:
 *
 * Reason          Date     userid    Description
 * -------         ------   --------  -----------------------------------------
 * LIDB3133-2.2.1  040301   tmusta    Adding SPI tags
 * LIDB1966        040310   tmusta    Improve Server Startup
 * LIDB3706-6      050214   dzavala   Add SUID
 * LIDB3491-1      070328   ericvn    Provide supported interfaces
 * ============================================================================ */

package com.ibm.ws.exception;

/**
 * A <code>ComponentDisabledException</code> is used to signal to the runtime
 * framework that a component exists in the runtime framework but is currently
 * disabled (for reasons unspecified). The component which throws this event
 * will be exempt from further lifecycle events.
 * 
 * <p>
 * <b>Note:</b> Configuration analyzers or a server activation plan are preferential ways to avoid using this exception. For example, it is inappropriate to throw this exception to
 * prevent component startup based on server type.
 * 
 * @see com.ibm.wsspi.runtime.component.WsComponent#initialize(Object)
 * 
 * @ibm-spi
 */
public class ComponentDisabledException extends WsRuntimeFwException { // LIDB1966

    private static final long serialVersionUID = -6923053474821316164L;

    /**
     * Constructs a new <code>ComponentDisabledException</code> with <code>null</code> as its detail message. The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}
     */
    public ComponentDisabledException() {}

    /**
     * Constructs a new <code>ComponentDisabledException</code> with the specified
     * detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}
     * 
     * @param msg
     *            the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public ComponentDisabledException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>ComponentDisabledException</code> with the specified
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
    public ComponentDisabledException(Throwable t) {
        super(t);
    }

    /**
     * Constructs a new <code>ComponentDisabledException</code> with the specified
     * detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail message.
     * 
     * @param msg
     *            the detail message (which is saved for later retrieval
     *            by the {@link #getMessage()} method).
     * @param t
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ComponentDisabledException(String msg, Throwable t) {
        super(msg, t);
    }
}