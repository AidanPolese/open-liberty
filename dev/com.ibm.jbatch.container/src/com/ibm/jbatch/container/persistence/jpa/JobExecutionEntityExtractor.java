/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.jbatch.container.persistence.jpa;


import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

/**
 *
 */
public class JobExecutionEntityExtractor extends ClassExtractor {

    /** {@inheritDoc} */
    @Override
    public Class extractClassFromRow(Record record, Session session) {
//        try {
//            Vector result = session.executeSelectingCall(new SQLCall("SELECT * FROM JBATCH.JOBPARAMETER"));
//        } catch (Exception e) {
//            return JobExecutionEntity.class;
//        }
        return JobExecutionEntityV2.class;
    }

}
