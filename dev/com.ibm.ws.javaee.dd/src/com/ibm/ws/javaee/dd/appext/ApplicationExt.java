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

package com.ibm.ws.javaee.dd.appext;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.app.Application;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDConstants;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDVersion;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIVersionAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;application-ext>.
 */
@DDRootElement(name = "application-ext",
               versions = {
                            @DDVersion(versionString = "1.0", version = 10, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
                            @DDVersion(versionString = "1.1", version = 11, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
               })
@DDIdAttribute
@DDXMIRootElement(name = "ApplicationExtension",
                  namespace = "applicationext.xmi",
                  version = 9,
                  primaryDDType = Application.class,
                  primaryDDVersions = { "1.2", "1.3", "1.4" },
                  refElementName = "application")
public interface ApplicationExt extends DeploymentDescriptor {

    static final String XML_EXT_NAME = "META-INF/ibm-application-ext.xml";
    static final String XMI_EXT_NAME = "META-INF/ibm-application-ext.xmi";

    enum ClientModeEnum {
        ISOLATED,
        FEDERATED,
        SERVER_DEPLOYED
    }

    /**
     * @return version="..." attribute value
     */
    @LibertyNotInUse
    @DDAttribute(name = "version", type = DDAttributeType.String)
    @DDXMIVersionAttribute
    String getVersion();

    /**
     * @return true if client-mode="..." attribute is specified
     * @see #getClientMode
     */
    boolean isSetClientMode();

    /**
     * @return client-mode="..." attribute value if specified
     * @see #isSetClientMode
     */
    @LibertyNotInUse
    @DDAttribute(name = "client-mode", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "clientMode")
    ClientModeEnum getClientMode();

    /**
     * @return &lt;module-extension> as a read-only list
     */
    @LibertyNotInUse
    @DDElement(name = "module-extension")
    @DDXMIElement(name = "moduleExtensions")
    List<ModuleExtension> getModuleExtensions();

    /**
     * @return true if &lt;reload-interval value="..."/> is specified
     * @see #getReloadInterval
     */
    boolean isSetReloadInterval();

    /**
     * @return &lt;reload-interval value="..."/> if specified
     * @see #isSetReloadInterval
     */
    @LibertyNotInUse
    @DDAttribute(name = "value", elementName = "reload-interval", type = DDAttributeType.Long)
    @DDXMIAttribute(name = "reloadInterval", nillable = true)
    long getReloadInterval();

    /**
     * @return true if &lt;shared-session-context value="..."/> is specified
     * @see #isSharedSessionContext
     */
    boolean isSetSharedSessionContext();

    /**
     * @return &lt;shared-session-context value="..."/> if specified
     * @see #isSetSharedSessionContext
     */
    @DDAttribute(name = "value", elementName = "shared-session-context", type = DDAttributeType.Boolean)
    @DDXMIAttribute(name = "sharedSessionContext")
    boolean isSharedSessionContext();

}
