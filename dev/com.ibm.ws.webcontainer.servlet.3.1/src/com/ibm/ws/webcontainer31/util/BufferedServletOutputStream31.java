//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61. (C) COPYRIGHT International Business Machines Corp. 2014
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
/*
 * @(#)HttpOutputStream.java	1.35 98/03/06
 * 
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.0
 */
package com.ibm.ws.webcontainer31.util;


import javax.servlet.WriteListener;

import com.ibm.wsspi.webcontainer.util.BufferedServletOutputStream;
/**
 * This class adds Servlet 3.1 methods to its 3.0-specific counterpart that
 * implements a buffered output stream for writing servlet response data.
 */
public class BufferedServletOutputStream31 extends BufferedServletOutputStream
{

//    protected static final Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer31.util");
//    private static TraceNLS nls = TraceNLS.getTraceNLS(BufferedServletOutputStream31.class, "com.ibm.ws.webcontainer.resources.Messages");

    /* (non-Javadoc)
     * @see javax.servlet.ServletOutputStream#isReady()
     */
    @Override
    public boolean isReady() {
        return false;
    }
    /* (non-Javadoc)
     * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.WriteListener)
     */
    @Override
    public void setWriteListener(WriteListener arg0) {
        return;
    }


}
