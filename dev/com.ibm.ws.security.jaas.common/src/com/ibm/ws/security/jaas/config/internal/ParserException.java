/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.config.internal;

public class ParserException extends java.security.GeneralSecurityException
{
    private static final long serialVersionUID = 43255370417322370L;

    public ParserException(String msg) {
        super(msg);
    }

    public ParserException(int lineno, String msg) {
        super("line " + lineno + ": " + msg);
    }

    public ParserException(int lineno, String expected, String result) {
        super("line " + lineno + ": expected '" + expected + "', found '" + result + "'");
    }
}
