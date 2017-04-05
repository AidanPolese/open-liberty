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

import java.util.List;

import com.ibm.ws.javaee.dd.commonext.GlobalTransaction;
import com.ibm.ws.javaee.dd.commonext.LocalTransaction;
import com.ibm.ws.javaee.dd.commonext.ResourceRef;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIFlatten;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredElements;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents common items between Session and messageDriven beans.
 */
@DDIdAttribute
@DDXMIIgnoredElements({
                        @DDXMIIgnoredElement(name = "structure",
                                             attributes = @DDXMIIgnoredAttribute(name = "inheritenceRoot", type = DDAttributeType.Boolean)),
                        @DDXMIIgnoredElement(name = "internationalization",
                                             attributes = @DDXMIIgnoredAttribute(name = "invocationLocale", type = DDAttributeType.Enum,
                                                                                 enumConstants = { "CALLER", "SERVER" }))
})
public interface EnterpriseBean {

    /**
     * @return &lt;bean-cache> return null if not specified.
     */
    @DDElement(name = "bean-cache")
    @DDXMIElement(name = "beanCache")
    BeanCache getBeanCache();

    /**
     * @return &lt;local-transaction> return null if not specified.
     */
    @DDElement(name = "local-transaction")
    @DDXMIElement(name = "localTransaction")
    LocalTransaction getLocalTransaction();

    /**
     * @return &lt;global-transaction> return null if not specified.
     */
    @DDElement(name = "global-transaction")
    @DDXMIElement(name = "globalTransaction")
    GlobalTransaction getGlobalTransaction();

    /**
     * @return &lt;resource-ref> return emptyList if not specified.
     */
    @DDElement(name = "resource-ref")
    @DDXMIElement(name = "resourceRefExtensions")
    List<ResourceRef> getResourceRefs();

    /**
     * @return &lt;run-as-mode> return emptyList if not specified.
     */
    @LibertyNotInUse
    @DDElement(name = "run-as-mode")
    @DDXMIElement(name = "runAsSettings")
    List<RunAsMode> getRunAsModes();

    /**
     * @return &lt;start-at-app-start> return null if not specified.
     */
    @DDElement(name = "start-at-app-start")
    @DDXMIFlatten
    StartAtAppStart getStartAtAppStart();

    /**
     * @return name="..." Required!
     */
    @DDAttribute(name = "name", type = DDAttributeType.String, required = true)
    @DDXMIRefElement(name = "enterpriseBean", referentType = com.ibm.ws.javaee.dd.ejb.EnterpriseBean.class, getter = "getName")
    String getName();
}
