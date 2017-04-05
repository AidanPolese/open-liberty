/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package javax.ejb;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is a custom implementation of EJBException that has behavior similar to
 * the RI as of EJB 3.0 (customers are known to rely on the exact format of the
 * getMessage return value) but with improved handling of the "cause exception",
 * which is distinct from the Throwable cause. If the Throwable cause has not
 * been set via initCause, then this class generally behaves as if the Throwable
 * cause were the "cause exception".
 */
public class EJBException extends RuntimeException {
    private static final long serialVersionUID = 796770993296843510L;
    private Exception causeException;

    public EJBException() {}

    public EJBException(Exception causeException) {
        this.causeException = causeException;
    }

    public EJBException(String message) {
        super(message);
    }

    public EJBException(String message, Exception causeException) {
        super(message);
        this.causeException = causeException;
    }

    public Exception getCausedByException() {
        return causeException;
    }

    @Override
    public Throwable getCause() {
        return super.getCause() != null ? super.getCause() : getCausedByException();
    }

    @Override
    public String getMessage() {
        if (causeException == null) {
            return super.getMessage();
        }

        StringBuilder sb = new StringBuilder();

        if (super.getMessage() != null) {
            sb.append(super.getMessage());
            sb.append("; ");
        }

        sb.append("nested exception is: ");
        sb.append(causeException.toString());

        return sb.toString();
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        if (causeException == null || super.getCause() == null || causeException == super.getCause()) {
            super.printStackTrace(ps);
        } else {
            synchronized (ps) {
                ps.println(this);
                causeException.printStackTrace(ps);
                super.printStackTrace(ps);
            }
        }
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        if (causeException == null || super.getCause() == null || causeException == super.getCause()) {
            super.printStackTrace(pw);
        } else {
            synchronized (pw) {
                pw.println(this);
                causeException.printStackTrace(pw);
                super.printStackTrace(pw);
            }
        }
    }

    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
}
