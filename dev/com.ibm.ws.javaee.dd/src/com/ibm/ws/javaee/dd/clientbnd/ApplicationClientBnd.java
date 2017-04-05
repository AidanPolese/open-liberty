/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.javaee.dd.clientbnd;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.client.ApplicationClient;
import com.ibm.ws.javaee.dd.commonbnd.MessageDestination;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDConstants;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDVersion;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredAttributes;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIVersionAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;application-client-bnd>.
 */
@DDRootElement(name = "application-client-bnd",
               versions = {
                            @DDVersion(versionString = "1.0", version = 10, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
                            @DDVersion(versionString = "1.1", version = 11, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
                            @DDVersion(versionString = "1.2", version = 12, namespace = DDConstants.WEBSPHERE_EE_NS_URI)
               })
@DDIdAttribute
@DDXMIRootElement(name = "ApplicationClientBinding",
                  namespace = "clientbnd.xmi",
                  version = 9,
                  primaryDDType = ApplicationClient.class,
                  primaryDDVersions = { "1.2", "1.3", "1.4" },
                  refElementName = "applicationClient")
@DDXMIIgnoredAttributes(@DDXMIIgnoredAttribute(name = "appName", type = DDAttributeType.String))
public interface ApplicationClientBnd extends DeploymentDescriptor, ClientRefBindingsGroup {

    static final String XML_BND_NAME = "META-INF/ibm-application-client-bnd.xml";
    static final String XMI_BND_NAME = "META-INF/ibm-application-client-bnd.xmi";

    /**
     * @return version="..." attribute value
     */
    @LibertyNotInUse
    @DDAttribute(name = "version", type = DDAttributeType.String)
    @DDXMIVersionAttribute
    String getVersion();

    /**
     * @return &lt;message-destination> as a read-only list
     */
    @LibertyNotInUse
    @DDElement(name = "message-destination")
    List<MessageDestination> getMessageDestinations();

}
