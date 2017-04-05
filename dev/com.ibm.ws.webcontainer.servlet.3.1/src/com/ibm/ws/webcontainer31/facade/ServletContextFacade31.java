//IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//


//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          436940          05/11/07     mmolden            70FVT: Using RRD from a JSP causes ClassCastException
//          519410          05/14/08     mmolden            SVT:unexpected InvalidPortletWindowIdentifierException
//Code added as part of LIDB 2283-4
//	        PM21451	        09/12/10     mmulholl           Add new getRealPath method
//			PM47487			09/28/11	 pmdinh				getContextPath should return empty string for default context-root
//

package com.ibm.ws.webcontainer31.facade;

import com.ibm.wsspi.webcontainer.facade.ServletContextFacade;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * @author asisin
 *
 * Facade wrapping the WebApp when returning a context to the user. This will 
 * prevent users from exploiting public methods in WebApp which were intended
 * for internal use only.
 */
public class ServletContextFacade31 extends ServletContextFacade {

    //private static TraceNLS nls = TraceNLS.getTraceNLS(ServletContextFacade31.class, "com.ibm.ws.webcontainer.resources.Messages");

    public ServletContextFacade31(IServletContext context) {
        super(context);
    }



    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getVirtualServerName()
     */
    //@Override
    public String getVirtualServerName() {
        return context.getVirtualServerName();
    }

}
