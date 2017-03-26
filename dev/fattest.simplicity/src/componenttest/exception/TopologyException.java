/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package componenttest.exception;

public class TopologyException extends Exception {

    public TopologyException() {
        super();
    }

    public TopologyException(String message, Throwable cause) {
        super(message, cause);
    }

    public TopologyException(String message) {
        super(message);

    }

    public TopologyException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;

}
