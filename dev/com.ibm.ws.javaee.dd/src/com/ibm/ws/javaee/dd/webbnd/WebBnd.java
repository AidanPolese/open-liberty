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

package com.ibm.ws.javaee.dd.webbnd;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.commonbnd.JASPIRef;
import com.ibm.ws.javaee.dd.commonbnd.MessageDestination;
import com.ibm.ws.javaee.dd.commonbnd.RefBindingsGroup;
import com.ibm.ws.javaee.dd.web.WebApp;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDConstants;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDVersion;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIFlatten;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredElements;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIVersionAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyModule;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;web-bnd>.
 */
@DDRootElement(name = "web-bnd",
               versions = {
                            @DDVersion(versionString = "1.0", version = 10, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
                            @DDVersion(versionString = "1.1", version = 11, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
                            @DDVersion(versionString = "1.2", version = 12, namespace = DDConstants.WEBSPHERE_EE_NS_URI)
               })
@DDIdAttribute
@DDXMIRootElement(name = "WebAppBinding",
                  namespace = "webappbnd.xmi",
                  version = 9,
                  primaryDDType = WebApp.class,
                  primaryDDVersions = { "2.2", "2.3", "2.4" },
                  refElementName = "webapp")
@DDXMIIgnoredElements({
                        @DDXMIIgnoredElement(name = "serviceRefBindings",
                                             list = true,
                                             attributes = @DDXMIIgnoredAttribute(name = "jndiName", type = DDAttributeType.String),
                                             refElements = @DDXMIIgnoredRefElement(name = "bindingServiceRef")),
                        @DDXMIIgnoredElement(name = "messageDestinations",
                                             list = true,
                                             attributes = @DDXMIIgnoredAttribute(name = "name", type = DDAttributeType.String))
})
@LibertyModule
public interface WebBnd extends DeploymentDescriptor, RefBindingsGroup {

    static final String XML_BND_NAME = "WEB-INF/ibm-web-bnd.xml";
    static final String XMI_BND_NAME = "WEB-INF/ibm-web-bnd.xmi";

    /**
     * @return version="..." attribute value
     */
    @LibertyNotInUse
    @DDAttribute(name = "version", type = DDAttributeType.String)
    @DDXMIVersionAttribute
    String getVersion();

    /**
     * @return &lt;virtual-host>, or null if unspecified
     */
    @DDElement(name = "virtual-host")
    @DDXMIFlatten
    VirtualHost getVirtualHost();

    /**
     * @return &lt;message-destination> as a read-only list
     */
    @LibertyNotInUse
    @DDElement(name = "message-destination")
    List<MessageDestination> getMessageDestinations();

    /**
     * @return &lt;jaspi-ref>, or null if unspecified
     */
    @LibertyNotInUse
    @DDElement(name = "jaspi-ref")
    @DDXMIElement(name = "jaspiRefBinding")
    JASPIRef getJASPIRef();
}
