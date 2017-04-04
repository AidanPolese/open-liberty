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

package com.ibm.ws.javaee.dd.commonbnd;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIFlatten;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;resource-ref>.
 */
@DDIdAttribute
public interface ResourceRef {

    @DDAttribute(name = "name", type = DDAttributeType.String)
    @DDXMIRefElement(name = "bindingResourceRef", referentType = com.ibm.ws.javaee.dd.common.ResourceRef.class, getter = "getName")
    String getName();

    @DDAttribute(name = "binding-name", type = DDAttributeType.String)
    @DDXMIAttribute(name = "jndiName")
    String getBindingName();

    @DDElement(name = "authentication-alias")
    AuthenticationAlias getAuthenticationAlias();

    @DDElement(name = "custom-login-configuration")
    @DDXMIFlatten
    CustomLoginConfiguration getCustomLoginConfiguration();

    /* Default-auth is for backward compatibility and should not be used. */
    @LibertyNotInUse
    @DDAttribute(name = "userid", elementName = "default-auth", type = DDAttributeType.String)
    @DDXMIAttribute(name = "userId", elementName = "defaultAuth",
                    elementXMITypeNamespace = "commonbnd.xmi", elementXMIType = "BasicAuthData")
    String getDefaultAuthUserid();

    @LibertyNotInUse
    @DDAttribute(name = "password", elementName = "default-auth", type = DDAttributeType.String)
    @DDXMIAttribute(name = "password", elementName = "defaultAuth",
                    elementXMITypeNamespace = "commonbnd.xmi", elementXMIType = "BasicAuthData")
    String getDefaultAuthPassword();
}
