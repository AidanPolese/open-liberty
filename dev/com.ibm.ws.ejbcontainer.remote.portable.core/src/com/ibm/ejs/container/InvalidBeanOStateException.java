/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This exception is thrown whenever a <code>BeanO</code> method is
 * invoked on a <code>BeanO</code> in an invalid state. <p>
 * 
 * During normal operation, this exception will not be thrown. It
 * indicates that an internal error occurred. <p>
 */

public class InvalidBeanOStateException
                extends ContainerInternalError
{
    private static final long serialVersionUID = -8193296452469430090L;

    /**
     * The current state of the <code>BeanO</code> when this exception
     * was thrown. <p>
     */

    public final String currentState;

    /**
     * The expected state of the <code>BeanO</code> when this exception
     * was thrown. <p>
     */

    public final String expectedState;

    public final String msg; // LIDB2775-23.0

    /**
     * Create a new <code>InvalidBeanOStateException</code>
     * instance with associated current and expected states. <p>
     * 
     * @param current the <code>String</code> describing the state
     *            this <code>BeanO</code> was in when this exception was
     *            thrown <p>
     * 
     * @param expected the <code>String</code> describing the state
     *            this <code>BeanO</code> was supposed to be in when this
     *            exception was thrown <p>
     */
    public InvalidBeanOStateException(String current, String expected) {

        super();
        msg = ""; // LIDB2775-23.0
        currentState = current;
        expectedState = expected;

    } // InvalidBeanOStateException

    // LIDB2775-23.0 Begins
    public InvalidBeanOStateException(String msg) {
        super();
        this.msg = msg;
        currentState = "";
        expectedState = "";
    }

    // LIDB2775-23.0 Ends

    public String toString() {
        if (msg == null || msg.equals("")) // LIDB2775-23.0 LI3706-7
        {
            return "InvalidBeanOStateException(current = " + currentState +
                   ", expected = " + expectedState + ")";
        } else { // LIDB2775-23.0
            return "InvalidBeanOStateException(" + msg + ")"; // LIDB2775-23.0
        }

    } // toString

} // InvalidBeanOStateException
