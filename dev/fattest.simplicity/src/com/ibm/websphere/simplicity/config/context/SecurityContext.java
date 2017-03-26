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
package com.ibm.websphere.simplicity.config.context;

import javax.xml.bind.annotation.XmlAttribute;

import com.ibm.websphere.simplicity.config.ConfigElement;

/**
 * Represents the <securityContext> element which can be nested under <contextService>
 */
public class SecurityContext extends ConfigElement {
    private String callerSubject;
    private String invocationSubject;

    public String getCallerSubject() {
        return callerSubject;
    }

    public String getInvocationSubject() {
        return invocationSubject;
    }

    @XmlAttribute(name = "callerSubject")
    public void setCallerSubject(String subject) {
        callerSubject = subject;
    }

    @XmlAttribute(name = "invocationSubject")
    public void setInvocationSubject(String subject) {
        invocationSubject = subject;
    }

    /**
     * Returns a string containing a list of the properties and their values.
     * 
     * @return String representing the data
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("securityContext{");
        if (getId() != null)
            buf.append("id=").append(getId()).append(' ');
        if (callerSubject != null)
            buf.append("callerSubject=").append(callerSubject).append(' ');
        if (invocationSubject != null)
            buf.append("invocationSubject=").append(invocationSubject).append(' ');
        buf.append("}");
        return buf.toString();
    }
}