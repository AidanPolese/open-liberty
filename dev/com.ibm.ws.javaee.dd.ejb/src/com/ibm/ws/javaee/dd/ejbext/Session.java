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
package com.ibm.ws.javaee.dd.ejbext;

import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIFlatten;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIType;

/**
 * Represents a &lt;enterpriseBean> object of type session.
 */
@DDXMIType(name = "SessionExtension", namespace = "ejbext.xmi")
public interface Session extends EnterpriseBean {

    /**
     * @returns &lt;time-out> element, return null if not specified.
     */
    @DDElement(name = "time-out")
    @DDXMIFlatten
    TimeOut getTimeOut();

}
