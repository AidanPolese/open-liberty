//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * Created on Nov 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.ibm.wsspi.jsp.tools;

import java.util.HashMap;
import java.util.Properties;

/**
 * @author Scott Johnson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JspToolsOptionsMap extends HashMap {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3760846757378209584L;

	/**
     * @param props
     */
    public JspToolsOptionsMap(Properties props) {
        
        super(props);
    }

    /**
     * @param arg0
     * @param arg1
     */
    /**
     * 
     */
    public JspToolsOptionsMap() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void addOption(JspToolsOptionKey key, Object value){
        put(key, value);
    }
}
