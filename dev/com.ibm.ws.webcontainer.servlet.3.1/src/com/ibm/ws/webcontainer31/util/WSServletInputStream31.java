// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2014
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.ws.webcontainer31.util;

import javax.servlet.ReadListener;

import com.ibm.wsspi.webcontainer.util.WSServletInputStream;


public abstract class WSServletInputStream31 extends WSServletInputStream {

    public abstract boolean isFinished();
    public abstract boolean isReady();
    public abstract void setReadListener(ReadListener readListener);

}
