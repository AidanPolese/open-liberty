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

import javax.persistence.Embeddable;

@Embeddable
public class JobParameter {

    private String name = "";
    private String value = "";

    public String getParameterName() {
        return name;
    }

    public void setParameterName(String newName) {
        name = newName;
    }

    public String getParameterValue() {
        return value;
    }

    public void setParameterValue(String newValue) {
        value = newValue;
    }

}
