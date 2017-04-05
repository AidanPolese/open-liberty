/*
 * @(#) 1.4 SERV1/ws/code/wsexception/src/com/ibm/ws/exception/WsRuntimeException.java, WAS.wsexception, WASX.SERV1, kk0826.07 4/21/06 09:36:06 [6/30/08 14:25:30]
 * 
 * COMPONENT_NAME: WAS.wsexception
 *
 * ORIGINS: 27         (used for IBM originated files)
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2001, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason    Version  Date        User id     Description
 * ----------------------------------------------------------------------------
 * LIDB799.1  5.0    10-09-2001   stopyro    Part created.
 * D200273.1  6.0    06-08-2004   ericvn     We are using now using JDK 1.4   
 * D217143    6.0    07-19-2004   ericvn     Add back to support cross-release
 * D363517.6  6.1    04-21-2006   ericvn     Add to websphere-apis.jar        
 *
 */
package com.ibm.ws.exception;

/**
 * The <code>WsRuntimeException</code> class is a subclass of <code>java.lang.RuntimeException</code> that provides support for exception
 * chaining semantics that will be available in the 1.4 timeframe. Exceptions
 * that
 * desire to inherit these semantics while running on pre 1.4 JDKs should extend
 * this class.
 * <p>
 * It should be noted that this class will provide only the exception chaining semantics available in 1.4. This is a subset of all the new functionality. introduced in 1.4. Most of
 * that functionality requires support from the JVM that is not available on earlier editions.
 * <p>
 * Exceptions that subclass this exception only need to add the various constructors and inherit everything else from this class.
 * <p>
 * See the javadoc for the JDK 1.4 java.lang.Throwable for a full description of the exception chaining functionality.
 * 
 * @ibm-private-in-use
 */
@SuppressWarnings("deprecation")
public class WsRuntimeException extends RuntimeException implements WsNestedException {

    // D217143 - Add instance variables
    static final long serialVersionUID = 4158532309708079100L;
    private Throwable ivCause = this;
    private transient boolean causeInitialized = false;

    // D200273.1 - Use functionality of RuntimeException as we are now on JDK 1.4
    // We still need to implement the constructors to call RuntimeException
    /**
     * Constructs a new WsRuntimeException with <code>null</code> as its
     * detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public WsRuntimeException() {
        super();
    }

    /**
     * Constructs a new WsRuntimeException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * 
     * @param message
     *            the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public WsRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new WsRuntimeException with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for WsRuntimeExceptions
     * that are little more than wrappers for other throwables.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public WsRuntimeException(Throwable cause) {
        super(cause);
        causeInitialized = true; // D217143 - 6.0 cause to print on 5.0
        ivCause = cause; // D217143 - 6.0 cause to print on 5.0
    }

    /**
     * Constructs a new WsRuntimeException with the specified detail message and
     * cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this WsRuntimeException's detail message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval
     *            by the {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public WsRuntimeException(String message, Throwable cause) {
        super(message, cause);
        causeInitialized = true; // D217143 - 6.0 cause to print on 5.0
        ivCause = cause; // D217143 - 6.0 cause to print on 5.0
    }

    // D217143 - Start additional methods

    /**
     * Return the Throwable that is considered the root cause of this WsException.
     * Null is returned if the root cause is nonexistent or unknown. The root
     * cause is the throwable that caused this WsException to get thrown.
     * <p>
     * The Throwable that is returned is either the Throwable supplied via one of the appropriate constructors, or that set via the {@link #initCause(Throwable)} method. While it
     * is
     * typically unnecessary to override this method, a subclass can override it to return a cause set by some other means, such as a legacy exception chaining infrastructure.
     * <p>
     * 
     * @return the Throwable that is the cause of this WsException, or null if
     *         the cause is nonexistent or unknown.
     */
    public Throwable getCause() {
        try {
            if (!causeInitialized && ivCause != this) {
                super.initCause(ivCause);
            }
        } catch (Throwable t) {
        } finally {
            causeInitialized = true;
        }
        return super.getCause();
    }

    /**
     * Initialize the cause field for this WsException to the specified value.
     * The cause is the Throwable that caused this WsException to get thrown.
     * <p>
     * This method can be called at most once. It is generally called from within a constructor that takes a Throwable, or immediately after constructing this object with a
     * constructor that does not accept a \ Throwable. Thus, if a constructor that takes Throwable as a parameter is used to construct this object, it cannot be called at all.
     * <p>
     * 
     * @param cause
     *            the Throwable which caused this WsException to be thrown.
     *            Null is tolerated.
     * @return a reference to this <code>Throwable</code> instance.
     * @exception IllegalArgumentException
     *                if the specified cause is this
     *                WsException. An exception cannot be its own cause.
     * @exception IllegalStateException
     *                if this WsException was created with a
     *                constructor that specified a cause, or this method has already
     *                been
     *                called on this object.
     */
    public synchronized Throwable initCause(Throwable cause) throws IllegalStateException, IllegalArgumentException {
        super.initCause(cause);
        ivCause = cause;
        causeInitialized = true;
        return this;
    }

    // D217143 End additional methods

}
