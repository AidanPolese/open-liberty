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

import com.ibm.ws.javaee.dd.commonext.GlobalTransaction;
import com.ibm.ws.javaee.dd.commonext.LocalTransaction;
import com.ibm.ws.javaee.dd.web.common.Servlet;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;servlet>.
 */
@LibertyNotInUse
@DDIdAttribute
public interface ServletExtension {

    @DDAttribute(name = "name", type = DDAttributeType.String)
    @DDXMIRefElement(name = "extendedServlet", referentType = Servlet.class, getter = "getServletName")
    String getName();

    @DDElement(name = "local-transaction")
    @DDXMIElement(name = "localTransaction")
    LocalTransaction getLocalTransaction();

    @DDElement(name = "global-transaction")
    @DDXMIElement(name = "globalTransaction")
    GlobalTransaction getGlobalTransaction();

    @DDElement(name = "web-global-transaction")
    @DDXMIElement(name = "webGlobalTransaction")
    WebGlobalTransaction getWebGlobalTransaction();

    @DDElement(name = "markup-language")
    @DDXMIElement(name = "markupLanguages")
    List<MarkupLanguage> getMarkupLanguages();
}
