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
package com.ibm.example.jca.anno;

import java.util.TreeMap;

import javax.resource.cci.MappedRecord;

/**
 * Example MappedRecord where entries are kept in a TreeMap.
 */
@SuppressWarnings("rawtypes")
public class MappedRecordImpl extends TreeMap implements MappedRecord {
    private static final long serialVersionUID = 5653529590057147554L;

    private String recordName;
    private String recordShortDescription;

    @Override
    public String getRecordName() {
        return recordName;
    }

    @Override
    public String getRecordShortDescription() {
        return recordShortDescription;
    }

    @Override
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    @Override
    public void setRecordShortDescription(String recordShortDescription) {
        this.recordShortDescription = recordShortDescription;
    }
}
