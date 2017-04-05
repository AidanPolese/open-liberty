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

package com.ibm.ws.javaee.dd.commonext;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIEnumConstant;

/**
 * Represents &lt;method>.
 */
@DDIdAttribute
public interface Method {

    enum MethodTypeEnum {
        @DDXMIEnumConstant(name = "Unspecified")
        UNSPECIFIED,
        @DDXMIEnumConstant(name = "Remote")
        REMOTE,
        @DDXMIEnumConstant(name = "Home")
        HOME,
        @DDXMIEnumConstant(name = "Local")
        LOCAL,
        @DDXMIEnumConstant(name = "LocalHome")
        LOCAL_HOME,
        @DDXMIEnumConstant(name = "ServiceEndpoint")
        SERVICE_ENDPOINT
    }

    @DDAttribute(name = "name", type = DDAttributeType.String)
    @DDXMIAttribute(name = "name")
    String getName();

    @DDAttribute(name = "params", type = DDAttributeType.String)
    @DDXMIAttribute(name = "parms")
    String getParams();

    @DDAttribute(name = "type", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "type")
    MethodTypeEnum getType();

}
