//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
/*
* Change history:
* defect 223226 "GVT: DBCS in included page is displayed as ???"  2004/08/11  Scott Johnson
* jsp2.1work
* 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson
*/

package com.ibm.ws.jsp.configuration;


public class StaticIncludeJspConfiguration extends JspConfiguration {
    public StaticIncludeJspConfiguration(JspConfiguration parentConfig) {
        super(parentConfig.getConfigManager(), parentConfig.getServletVersion(), parentConfig.getJspVersion(), parentConfig.isXml(), parentConfig.isXmlSpecified(), parentConfig.elIgnored(), parentConfig.scriptingInvalid(), parentConfig.isTrimDirectiveWhitespaces(), parentConfig.isDeferredSyntaxAllowedAsLiteral(), parentConfig.getTrimDirectiveWhitespaces(), parentConfig.getDeferredSyntaxAllowedAsLiteral(), parentConfig.elIgnoredSetTrueInPropGrp(), parentConfig.elIgnoredSetTrueInPage(), parentConfig.getDefaultContentType(), parentConfig.getBuffer(), parentConfig.isErrorOnUndeclaredNamespace()); 
    }
}
