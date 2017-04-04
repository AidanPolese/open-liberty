// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.pmi;

/**
 * @ibm-api
 */
public class PmiException extends java.lang.Exception {

    private static final long serialVersionUID = 6554934609377950521L;

    /**
     * PmiException may be thrown by PmiClient if something goes wrong.
     */
    public PmiException(String s) {
        super(s);
    }

    /**
     * PmiException may be thrown by PmiClient if something goes wrong.
     */
    public PmiException() {
        super();
    }
}
