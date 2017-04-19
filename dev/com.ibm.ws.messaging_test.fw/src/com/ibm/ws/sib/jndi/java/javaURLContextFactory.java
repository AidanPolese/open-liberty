/* ************************************************************************** 
 * COMPONENT_NAME: sib.mediations                                             
 *                                                                            
 *  ORIGINS: 27                                                               
 *


============================================================================
IBM Confidential OCO Source Materials

Copyright IBM Corp. 2012

The source code for this program is not published or otherwise divested
of its trade secrets, irrespective of what has been deposited with the
U.S. Copyright Office.
============================================================================
                                           
 *                                                                            
 *                                                                            
 *  Change activity:                                                           
 *
 * Reason         Date        Origin   Description
 * -------------- ----------- -------- -------------------------------------
 *                                     Version 1.4 copied from CMVC                           
 *                                                                            
 * **************************************************************************
 */
package com.ibm.ws.sib.jndi.java;

import java.util.Hashtable;

import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

/* ************************************************************************** */
/**
 * A Summary of a goes here. Followed by a paragraph of
 * general description.
 * 
 */
/* ************************************************************************** */
public class javaURLContextFactory implements ObjectFactory {

    /* -------------------------------------------------------------------------- */
    /*
     * getObjectInstance method
     * /* --------------------------------------------------------------------------
     */
    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     * @param urlInfo
     * @param name
     * @param nameCtx
     * @param environment
     * @return
     * @throws java.lang.Exception
     */
    public Object getObjectInstance(Object urlInfo, Name name, Context nameCtx, Hashtable environment) throws Exception {
        if (urlInfo == null) {
            return new javaURLContext(environment);
        }
        if (urlInfo instanceof String) {
            String url = (String) urlInfo;
            Context context = new javaURLContext(environment);
            try {
                return context.lookup(url);
            } finally {
                context.close();
            }
        }
        if (urlInfo instanceof String[]) {
            // Try each URL until either lookup succeeds or they all fail
            String[] urls = (String[]) urlInfo;
            if (urls.length == 0)
                throw new ConfigurationException("javaURLContextFactory: empty URL array");
            Context context = new javaURLContext(environment);
            try {
                NamingException ne = null;
                for (int i = 0; i < urls.length; i++) {
                    try {
                        return context.lookup(urls[i]);
                    } catch (NamingException e) {
                        ne = e;
                    }
                }
                throw ne;
            } finally {
                context.close();
            }
        }
        throw new ConfigurationException("javaURLContextFactory: cannot understand urlInfo:" + urlInfo);
    }
}
