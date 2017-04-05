// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//382943        08/09/06    todkap             remove SUN dependencies from core webcontainer
//LIDB3518-1.1  06-23-07    mmolden             ARD
//

package com.ibm.ws.webcontainer31.util;

import javax.servlet.WriteListener;

import com.ibm.wsspi.webcontainer.util.WSServletOutputStream;

public abstract class WSServletOutputStream31 extends WSServletOutputStream {

    public abstract boolean isReady();
    public abstract void setWriteListener(WriteListener writeListener);
}
