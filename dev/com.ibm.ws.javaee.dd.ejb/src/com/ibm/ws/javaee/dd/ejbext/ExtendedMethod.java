/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.javaee.dd.ejbext;

import com.ibm.ws.javaee.dd.commonext.Method;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;extended-method>.
 */
@LibertyNotInUse
public interface ExtendedMethod extends Method {

    @DDAttribute(name = "ejb", type = DDAttributeType.String)
    @DDXMIRefElement(name = "enterpriseBean", referentType = com.ibm.ws.javaee.dd.ejb.EnterpriseBean.class, getter = "getName")
    String getEJB();
}
