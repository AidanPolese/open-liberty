/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.persistence.jpa;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity
public class JobExecutionEntityV2 extends JobExecutionEntity {

    //@Column(name = "VERSION")
    //private int version = 2;

    @ElementCollection
    @CollectionTable(name = "JOBPARAMETER", joinColumns = @JoinColumn(name = "FK_JOBEXECID"))
    private Set<JobParameter> jobParameterElements;

    // For JPA
    public JobExecutionEntityV2() {
        super();
    }

    // For in-memory persistence
    public JobExecutionEntityV2(long jobExecId) {
        super(jobExecId);
    }

    @Override
    public void setJobParameters(Properties jobParameters) {
        this.jobParameters = trimJESParameters(jobParameters);

        if (this.jobParameters != null) {
            Set<JobParameter> params = new HashSet<JobParameter>();
            for (Map.Entry param : this.jobParameters.entrySet()) {
                JobParameter newParam = new JobParameter();
                newParam.setParameterName((String) param.getKey());
                newParam.setParameterValue((String) param.getValue());
                params.add(newParam);
            }
            jobParameterElements = params;
        }
    }

}
