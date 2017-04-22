/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package javax.rmi.CORBA;

/**
 * One of our tests needs to override a system class. {@link Util} is a pretty safe bet to be present on all JDKs, since it is a required API.
 * Also, this can safely be overridden since it is not in a <code>java.*</code> package.
 */

public class Util {
    public static final boolean IMPOSTER = true;
}
