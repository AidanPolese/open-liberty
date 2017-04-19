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
package com.ibm.ws.anno.test.data;

import com.ibm.ws.anno.test.data.sub.SubBase;

/**
 *
 */
public class DerivedBase extends SubBase {
    protected int public1;
    public int protected2;
    private int private2;

    int package1;
}
