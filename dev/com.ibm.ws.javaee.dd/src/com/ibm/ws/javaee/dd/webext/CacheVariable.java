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

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;cache-variable>.
 */
@LibertyNotInUse
public interface CacheVariable {
    enum TypeEnum {
        REQUEST_PARAMETER,
        REQUEST_ATTRIBUTE,
        SESSION_PARAMETER,
        COOKIE
    }

    boolean isSetType();

    @DDAttribute(name = "type", type = DDAttributeType.Enum)
    TypeEnum getType();

    @DDAttribute(name = "identifier", type = DDAttributeType.String)
    String getIdentifier();

    @DDAttribute(name = "method", type = DDAttributeType.String)
    String getMethod();

    boolean isSetRequired();

    @DDAttribute(name = "required", type = DDAttributeType.Boolean)
    boolean isRequired();

    @DDAttribute(name = "data-id", type = DDAttributeType.String)
    String getDataId();

    @DDAttribute(name = "invalidate", type = DDAttributeType.String)
    String getInvalidate();
}
