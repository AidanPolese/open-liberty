// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F46946    WAS85     20110712 bkail    : New
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.javaee.dd.webext;

import java.util.List;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;servlet-cache-config>.
 */
@LibertyNotInUse
public interface ServletCacheConfig {

    @DDAttribute(name = "properties-group-name", type = DDAttributeType.String)
    @DDXMIAttribute(name = "propertiesGroupName")
    String getPropertiesGroupName();

    @DDAttribute(name = "name", elementName = "servlet", type = DDAttributeType.String)
    List<String> getServletNames();

    boolean isSetTimeout();

    @DDAttribute(name = "value", elementName = "timeout", type = DDAttributeType.Int)
    @DDXMIAttribute(name = "timeout")
    int getTimeout();

    boolean isSetPriority();

    @DDAttribute(name = "value", elementName = "priority", type = DDAttributeType.Int)
    @DDXMIAttribute(name = "priority")
    int getPriority();

    boolean isSetInvalidateOnly();

    @DDAttribute(name = "value", elementName = "invalidate-only", type = DDAttributeType.Boolean)
    @DDXMIAttribute(name = "invalidateOnly")
    boolean isInvalidateOnly();

    @DDAttribute(name = "name", elementName = "external-cache-group", type = DDAttributeType.String)
    @DDXMIAttribute(name = "externalCacheGroups")
    List<String> getExternalCacheGroupNames();

    @DDAttribute(name = "class", elementName = "id-generator", type = DDAttributeType.String)
    @DDXMIAttribute(name = "idGenerator")
    String getIdGenerator();

    @DDAttribute(name = "class", elementName = "metadata-generator", type = DDAttributeType.String)
    @DDXMIAttribute(name = "metadataGenerator")
    String getMetadataGenerator();

    @DDElement(name = "id-generation-properties")
    IdGenerationProperties getIdGenerationProperties();

}
