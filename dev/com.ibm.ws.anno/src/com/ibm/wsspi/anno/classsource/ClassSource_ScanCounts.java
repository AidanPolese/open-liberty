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
package com.ibm.wsspi.anno.classsource;

public interface ClassSource_ScanCounts {

    public enum ResultField {
        ENTRY("Entry"), // NON_CONTAINER + CONTAINER

        NON_CONTAINER("NonContainer"), // NON_CLASS + CLASS
        CONTAINER("Container"), // ROOT_CONTAINER + NON_ROOT_CONTAINER

        ROOT_CONTAINER("RootContainer"),
        NON_ROOT_CONTAINER("NonRootContainer"),

        NON_CLASS("NonClass"),
        CLASS("Class"), // DUPLICATE_CLASS + UNPROCESSED_CLASS + PROCESSED_CLASS

        DUPLICATE_CLASS("DuplicateClass"),
        UNPROCESSED_CLASS("UnprocessedClass"),
        PROCESSED_CLASS("ProcessedClass");

        //

        protected String tag;

        private ResultField(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    int NUM_RESULT_FIELDS = ResultField.values().length;

    //

    void addResults(ClassSource_ScanCounts seep);

    void increment(ResultField resultField);

    void increment(int resultField);

    //

    int getResult(ResultField resultField);

    int getResult(int resultField);
}
