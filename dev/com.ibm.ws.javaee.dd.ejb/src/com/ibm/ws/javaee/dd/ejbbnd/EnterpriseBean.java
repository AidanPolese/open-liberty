/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejbbnd;

import com.ibm.ws.javaee.dd.commonbnd.RefBindingsGroup;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredElements;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;

/**
 * SuperInterface for &lt;message-driven> or &lt;session>.
 */
@DDIdAttribute
@DDXMIIgnoredElements(@DDXMIIgnoredElement(name = "cmpConnectionFactory"))
public interface EnterpriseBean extends RefBindingsGroup {

    /**
     * @return name="..." attribute value -- use is required!
     */
    @DDAttribute(name = "name", type = DDAttributeType.String, required = true)
    @DDXMIRefElement(name = "enterpriseBean", referentType = com.ibm.ws.javaee.dd.ejb.EnterpriseBean.class, getter = "getName")
    String getName();

}
