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

import com.ibm.ws.javaee.dd.app.Module;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredAttributes;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIIgnoredRefElements;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIType;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;module-extension>.
 */
@LibertyNotInUse
@DDIdAttribute
@DDXMIType(name = { "ConnectorModuleExtension", "EjbModuleExtension", "JavaClientModuleExtension", "WebModuleExtension" }, namespace = "applicationext.xmi")
@DDXMIIgnoredAttributes(@DDXMIIgnoredAttribute(name = "dependentClasspath", type = DDAttributeType.String))
@DDXMIIgnoredRefElements(@DDXMIIgnoredRefElement(name = "applicationExtension"))
public interface ModuleExtension {

    /**
     * @return name="..." attribute value
     */
    @LibertyNotInUse
    @DDAttribute(name = "name", type = DDAttributeType.String)
    @DDXMIRefElement(name = "module", referentType = Module.class, getter = "getModulePath")
    String getName();

    /**
     * @return true if &lt;alt-bindings uri="..."/> is specified
     * @see #getAltBindings
     */
    boolean isSetAltBindings();

    /**
     * @return &lt;alt-bindings uri="..."/> if specified
     * @see #isSetAltBindings
     */
    @LibertyNotInUse
    @DDAttribute(name = "uri", elementName = "alt-bindings", type = DDAttributeType.String)
    @DDXMIAttribute(name = "altBindings")
    String getAltBindings();

    /**
     * @return true if &lt;alt-extensions uri="..."/> is specified
     * @see #getAltExtensions
     */
    boolean isSetAltExtensions();

    /**
     * @return &lt;alt-extensions uri="..."/> if specified
     * @see #isSetAltExtensions
     */
    @LibertyNotInUse
    @DDAttribute(name = "uri", elementName = "alt-extensions", type = DDAttributeType.String)
    @DDXMIAttribute(name = "altExtensions")
    String getAltExtensions();

    /**
     * @return true if &lt;alt-root uri="..."/> is specified
     * @see #getAltRoot
     */
    boolean isSetAltRoot();

    /**
     * @return &lt;alt-root uri="..."/> if specified
     * @see #isSetAltRoot
     */
    @LibertyNotInUse
    @DDAttribute(name = "uri", elementName = "alt-root", type = DDAttributeType.String)
    @DDXMIAttribute(name = "altRoot")
    String getAltRoot();

    // <!-- This element is for internal use only. -->

    /**
     * @return true if &lt;absolute-path path="..."/> is specified
     * @see #getAbsolutePath
     */
    boolean isSetAbsolutePath();

    /**
     * @return &lt;absolute-path path="..."/> if specified
     * @see #isSetAbsolutePath
     */
    @LibertyNotInUse
    @DDAttribute(name = "path", elementName = "absolute-path", type = DDAttributeType.String)
    @DDXMIAttribute(name = "absolutePath")
    String getAbsolutePath();

}
