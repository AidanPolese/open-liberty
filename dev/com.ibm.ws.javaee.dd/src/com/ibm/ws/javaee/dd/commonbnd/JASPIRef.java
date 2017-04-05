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
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;jaspi-ref>.
 */
@LibertyNotInUse
@DDIdAttribute
public interface JASPIRef {

    enum UseJASPIEnum {
        yes,
        no,
        inherit
    }

    @DDAttribute(name = "provider-name", type = DDAttributeType.String)
    @DDXMIAttribute(name = "providerName")
    String getProviderName();

    @DDAttribute(name = "use-jaspi", type = DDAttributeType.Enum, defaultValue = "inherit")
    @DDXMIAttribute(name = "useJaspi")
    UseJASPIEnum getUseJASPI();

}
