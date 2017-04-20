/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejb;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;

@Singleton
@DependsOn({ TestDependsOn.DEPENDS_ON_1, TestDependsOn.DEPENDS_ON_2 })
public class TestDependsOn {
    public static final String DEPENDS_ON_1 = "TestSingleton";
    public static final String DEPENDS_ON_2 = "TestSingletonNamed";
}
