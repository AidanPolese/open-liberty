/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.anno.classsource.internal;

import com.ibm.wsspi.anno.classsource.ClassSource_ScanCounts;

public class ClassSourceImpl_ScanCounts implements ClassSource_ScanCounts {

    public ClassSourceImpl_ScanCounts() {
        super();

        this.results = new int[ClassSource_ScanCounts.NUM_RESULT_FIELDS];
    }

    //

    protected final int[] results;

    @Override
    public int getResult(ResultField resultField) {
        return results[resultField.ordinal()];
    }

    @Override
    public int getResult(int resultField) {
        return results[resultField];
    }

    //

    @Override
    public void addResults(ClassSource_ScanCounts seep) {
        for (int resultNo = 0; resultNo < ClassSource_ScanCounts.NUM_RESULT_FIELDS; resultNo++) {
            results[resultNo] = seep.getResult(resultNo);
        }
    }

    @Override
    public void increment(ResultField resultField) {
        results[resultField.ordinal()]++;
    }

    @Override
    public void increment(int resultField) {
        results[resultField]++;
    }
}
