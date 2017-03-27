/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package test.server;

public abstract class BaseTest {

    public static final long TIMEOUT = 30 * 1000;

    protected String name;

    public BaseTest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String[] getServiceClasses();

}
