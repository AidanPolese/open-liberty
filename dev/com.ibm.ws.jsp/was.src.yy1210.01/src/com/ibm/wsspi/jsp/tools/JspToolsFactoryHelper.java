//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * Created on Dec 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.ibm.wsspi.jsp.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kaspar
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JspToolsFactoryHelper {

	static String TOOLS_FACTORY ="com.ibm.ws.jsp.tools.JspToolsFactoryImpl";
	
	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.wsspi.jsp.tools.JspToolsFactoryHelper";
	static {
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}

    /**
     * 
     */
    public JspToolsFactoryHelper() {
        super();
    }

    public static JspToolsFactory getJspToolsFactory() {
        JspToolsFactory factory=null;
        try {
            factory = (JspToolsFactory) Class.forName(TOOLS_FACTORY).newInstance();
        }
        catch (ClassNotFoundException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "getJspToolsFactory", "Failed to find class: "+ TOOLS_FACTORY , e);
        } catch (InstantiationException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "getJspToolsFactory", "Failed to instantiate class: "+ TOOLS_FACTORY , e);
        } catch (IllegalAccessException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "getJspToolsFactory", "Failed to access class: "+ TOOLS_FACTORY , e);
        }
        return factory; 
    }

}
