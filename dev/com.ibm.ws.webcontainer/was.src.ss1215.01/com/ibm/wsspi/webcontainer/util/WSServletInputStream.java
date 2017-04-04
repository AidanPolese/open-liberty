// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//382943        08/09/06    todkap             remove SUN dependencies from core webcontainer
//

package com.ibm.wsspi.webcontainer.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;


public abstract class WSServletInputStream extends ServletInputStream {
   
    /**
     * Sets the content length for this input stream. This should be called
     * once the headers have been read from the input stream.
     * @param len the content length
     */
    public abstract void setContentLength( int contentLength);
    
    /**
     * Initializes the servlet input stream with the specified raw input stream.
     * @param in the raw input stream
     */
    public abstract void init (InputStream in) throws IOException;
    
    /**
     * Finishes reading the request without closing the underlying stream.
     * @exception IOException if an I/O error has occurred
     */
    public abstract void finish () throws IOException;
    
    /**
     * Sets an observer for this output stream. The observer will be
     * notified when the stream is first written to.
     * @param obs the IOutpuStreamObserver associated with this response
     */
    public void setObserver(IInputStreamObserver obs) {};
    
    /**
     * called to enable the data to be re-read from the beginning
     */
    public void restart() {}; 

}
