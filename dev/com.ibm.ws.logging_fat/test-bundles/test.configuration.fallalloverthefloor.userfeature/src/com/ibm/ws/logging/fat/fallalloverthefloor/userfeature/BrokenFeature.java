/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.fat.fallalloverthefloor.userfeature;

import java.util.Map;

/**
 *
 */
public class BrokenFeature {

    /**
     * A method which, after some indirection, throws an exception.
     */
    protected void activate(Map<String, Object> properties) throws Exception {
        thinkAboutThrowingAnException();

    }

    /**
     * @throws ConfigurationReceivedException
     */
    private void thinkAboutThrowingAnException() throws ConfigurationReceivedException {
        reallyThrowAnException();
    }

    /**
     * @throws ConfigurationReceivedException
     */
    private void reallyThrowAnException() throws ConfigurationReceivedException {
        System.out.println("The user feature is about to throw an exception.");
        throw new ConfigurationReceivedException();
    }

    protected static class ConfigurationReceivedException extends Exception {

        /**  */
        private static final long serialVersionUID = 1L;

    }
}
