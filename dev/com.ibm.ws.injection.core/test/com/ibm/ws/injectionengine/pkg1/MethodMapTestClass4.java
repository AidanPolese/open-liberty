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
package com.ibm.ws.injectionengine.pkg1;

import com.ibm.ws.injectionengine.pkg2.MethodMapTestClass3;

public class MethodMapTestClass4 extends MethodMapTestClass3 {
    @SuppressWarnings("all")
    @Override
    void pkg1OverridePkg1() { /* empty */}

    @SuppressWarnings("all")
    void pkg1NotOverridePkg2OverridePkg2() { /* empty */}

    @Override
    public void pkg1OverridePkg1AndPublicPkg2() { /* empty */}

    public void pkg1OverridePublicPkg2OverridePkg2() { /* empty */}
}
