/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.pkg2;

import com.ibm.ws.injectionengine.pkg1.MethodMapTestClass1;

public class MethodMapTestClass2 extends MethodMapTestClass1 {
    @SuppressWarnings("all")
    void pkg2NotOverridePkg1() { /* empty */}

    @SuppressWarnings("all")
    void pkg1OverridePkg1() { /* empty */}

    void pkg1NotOverridePkg2OverridePkg2() { /* empty */}

    @SuppressWarnings("all")
    public void pkg1OverridePkg1AndPublicPkg2() { /* empty */}

    @SuppressWarnings("all")
    void pkg1OverridePublicPkg2OverridePkg2() { /* empty */}
}
