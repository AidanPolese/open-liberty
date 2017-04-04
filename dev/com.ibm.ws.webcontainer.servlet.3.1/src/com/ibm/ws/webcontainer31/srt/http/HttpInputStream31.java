// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2014
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//382943        08/09/06    todkap             remove SUN dependencies from core webcontainer
//421712.1      03/22/07    mmolden            FVT4: BBOS1S restart repeatedly with wrong mtom SOAPMessage
//PM18453       08/24/10    mmulholl           Allow for input data have being read early 
//

/*
 * @(#)HttpInputStream31.java	1.13 97/10/13
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

package com.ibm.ws.webcontainer31.srt.http;

import javax.servlet.ReadListener;

/**
 * This class implements a buffered input stream for reading servlet request
 * data. It also keeps track of the number of bytes that have been read, and
 * allows the specification of an optional byte limit to ensure that the
 * content length has not been exceeded.
 *
 * @version	1.13, 10/13/97
 */
public class HttpInputStream31 extends com.ibm.ws.webcontainer.srt.http.HttpInputStream
{

    //private static TraceNLS nls = TraceNLS.getTraceNLS(HttpInputStream31.class, "com.ibm.ws.webcontainer.resources.Messages");

    /* (non-Javadoc)
     * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
     */
    @Override
    public void setReadListener(ReadListener arg0) {
        return;      
    }
}
