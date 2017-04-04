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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;

@Entity
public class JobInstanceEntityV2 extends JobInstanceEntity {

    // JPA
    public JobInstanceEntityV2() { super(); }

    // in-memory
    public JobInstanceEntityV2(long instanceId) {
        super(instanceId);
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "UPDATETIME")
    private Date lastUpdatedTime;
    
    @Override
    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @Override
    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}
