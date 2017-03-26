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
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * MongoDB config element
 */
public class MongoDBElement extends ConfigElement {
    private String databaseName, jndiName, mongoRef;

    private MongoElement nestedMongo;

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseName the databaseName to set
     */
    @XmlAttribute
    public void setDatabaseName(String d) {
        databaseName = d;
    }

    /**
     * @return the jndiName
     */

    public String getJndiName() {
        return jndiName;
    }

    /**
     * @param jndiName the jndiName to set
     */
    @XmlAttribute
    public void setJndiName(String j) {
        jndiName = j;
    }

    /**
     * @return the mongoRef
     */
    public String getMongoRef() {
        return mongoRef;
    }

    /**
     * @param mongoRef the mongoRef to set
     */
    @XmlAttribute
    public void setMongoRef(String m) {
        mongoRef = m;
    }

    public MongoElement getMongo() {
        return nestedMongo;
    }

    @XmlElement
    public void setMongo(MongoElement mongo) {
        nestedMongo = mongo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MongoDBElement [databaseName=" + databaseName + ", jndiName=" + jndiName + ", mongoRef=" + mongoRef + ", nestedMongo=" + nestedMongo + "]";
    }

}
