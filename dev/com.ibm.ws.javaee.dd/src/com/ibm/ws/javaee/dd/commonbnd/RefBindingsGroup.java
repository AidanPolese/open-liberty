// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011,2013
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

package com.ibm.ws.javaee.dd.commonbnd;

import java.util.List;

import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIElement;

/**
 * Represents the refBindingsGroup type from the ibm-common-bnd XSD.
 */
public interface RefBindingsGroup {

    @DDElement(name = "ejb-ref")
    @DDXMIElement(name = "ejbRefBindings")
    List<EJBRef> getEJBRefs();

    @DDElement(name = "resource-ref")
    @DDXMIElement(name = "resRefBindings")
    List<ResourceRef> getResourceRefs();

    @DDElement(name = "resource-env-ref")
    @DDXMIElement(name = "resourceEnvRefBindings")
    List<ResourceEnvRef> getResourceEnvRefs();

    @DDElement(name = "message-destination-ref")
    @DDXMIElement(name = "messageDestinationRefBindings")
    List<MessageDestinationRef> getMessageDestinationRefs();

    @DDElement(name = "data-source")
    // No XMI metadata: this element was not supported prior to EE 6.
    List<DataSource> getDataSources();

    @DDElement(name = "env-entry")
    // No XMI metadata: this element was not supported prior to EE 6.
    List<EnvEntry> getEnvEntries();
}
