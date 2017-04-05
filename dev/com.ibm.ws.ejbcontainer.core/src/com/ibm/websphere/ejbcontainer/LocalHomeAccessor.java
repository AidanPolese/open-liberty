/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2005, 2008
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.ejbcontainer;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This class decouples the internal WebSphere local home naming conventions
 * from application programs that wish to directly interact with the local
 * home namespace for Enterprise Java Beans (EJB) version 2.1 and earlier
 * applications. <p>
 * 
 * For EJB version 3.0 and later applications, javax.naming.Context.lookup()
 * in the ejblocal: namespace must be used instead. <p>
 * 
 * @author IBM Corp.
 * @since WAS 6.1
 * @ibm-api
 * @deprecated Use Context.lookup() in ejblocal: namespace.
 */
@Deprecated
public final class LocalHomeAccessor
{
    private static final String localHomePrefixString = "local:ejb/";
    private static InitialContext theRootContext = null;

    /**
     * Look up a local home using its global home name in JNDI.
     * The semantics are identical to javax.naming.Context.lookup(). <p>
     * 
     * @param target The global JNDI name assigned to the bean.
     * @return The local home for this bean.
     */
    public static Object lookup(String target) throws NamingException
    {
        if (theRootContext == null) {
            theRootContext = new InitialContext();
        }

        return theRootContext.lookup(localHomePrefixString + target);
    }

    /**
     * Return the string used internally as the prefix for local EJB
     * home names.
     * 
     * @return The string used as the local EJB home name prefix.
     */
    public static String getLocalHomePrefixString()
    {
        return localHomePrefixString;
    }

    /**
     * Do not allow instance creation.
     */
    private LocalHomeAccessor()
    {
        // Intentionally left blank
    }

}
