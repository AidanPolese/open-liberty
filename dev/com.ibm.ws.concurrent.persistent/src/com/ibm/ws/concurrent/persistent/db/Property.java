/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.concurrent.persistent.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * JPA entity for a property entry in the persistent store.
 */
@Entity
@Trivial
public class Property {
    /**
     * Property name.
     */
    @Column(length = 254, nullable = false)
    @Id
    public String ID;

    /**
     * Property value.
     */
    @Column(length = 254, nullable = false)
    public String VAL;

    public Property() {}

    Property(String id, String value) {
        ID = id;
        VAL = value;
    }
}
