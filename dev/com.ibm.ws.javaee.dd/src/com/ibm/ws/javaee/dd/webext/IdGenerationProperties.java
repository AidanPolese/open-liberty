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
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;id-generation-properties>.
 */
@LibertyNotInUse
public interface IdGenerationProperties {

    boolean isSetUseURI();

    @DDAttribute(name = "use-uri", type = DDAttributeType.Boolean)
    boolean isUseURI();

    boolean isSetAlternateName();

    @DDAttribute(name = "alternate-name", type = DDAttributeType.String)
    String getAlternateName();

    boolean isSetUsePathInfos();

    @DDAttribute(name = "use-path-infos", type = DDAttributeType.Boolean)
    boolean isUsePathInfos();

    @DDElement(name = "cache-variable")
    List<CacheVariable> getCacheVariables();
}
