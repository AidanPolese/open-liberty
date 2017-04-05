// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
// PK22392      04/25/06    mmolden             FLUSH() IS NOT WORKING AS EXPECTED ON SERVLETOUTPUTSTREAM          
//392654.3      02/20/07    mmolden             FVT4: response not received from web container on oneway call
//433960        04/20/07    mmolden             Using print for an outputstream does not flush                                                                                              
//PK89810       08/04/09    anupag               Send last buffers and close the connection when close()   
//
package com.ibm.wsspi.webcontainer.util;

import java.io.IOException;
import java.io.Writer;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.webcontainer.srt.WriteBeyondContentLengthException;
import com.ibm.websphere.servlet.response.IResponse;

/**
 * This class implements a buffered writer for writing servlet
 * response data. It also keeps track of the number of chars that have
 * been written Additionally, an observer list is maintained which
 * can be used to notify observers the first time the stream is written to.
 *
 */
public class BufferedWriter extends Writer implements ResponseBuffer
{
    /**
     * The actual writer	
     */
    protected Writer out;

    /**
     * The output buffer.
     */
    protected char[] buf = new char[0];

    /**
     * The current number of chars in the buffer.
     */
    protected int count;

    /**
     * The total number of chars written so far.
     */
    protected int total;

    /**
     * The maximum number of chars that can be written. This is initially
     * set to -1 in order to indicate that observers must be notified.
     */
    protected int limit;
    
    protected IResponse response;

    /**
     * The content length for this stream.
     */
    protected int length = -1;

    /**
     * The observer that will be notified when the stream is first written.
     */
    protected IOutputStreamObserver obs;

    /**
     * Flag indicating that the first write has already occurred.
     */
    protected boolean _hasWritten;

    /**
     * Flag indicating that the first write has already occurred.
     */
    protected boolean _hasFlushed;

    /**
     * If set then an I/O exception is pending.
     */
    protected IOException except;

    /**
     * Indicated whether the buffer has been written to the stream
     */
    protected boolean committed;

    private int bufferSize;
    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.wsspi.webcontainer.util");
	private static final String CLASS_NAME="com.ibm.wsspi.webcontainer.util.BufferedWriter";

    /**
     * Should we close the underlying stream on close ?
     * This flag is used for handling servlet chains which uses piped streams
     * to establish comunication from the filtered servlet to its servlet
     * filter.
     * By closing the filtered servlet output stream, we trigger the end 
     * of the filter's input stream.
     */
    private boolean closeOnClose = false;

    private static TraceNLS nls = TraceNLS.getTraceNLS(BufferedWriter.class, "com.ibm.ws.webcontainer.resources.Messages");

    /**
     * Creates a new servlet output stream using the specified buffer size.
     * @param size the output buffer size
     */
    public BufferedWriter(int size)
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"BufferedWriter", "Constructor --> "+size);
        } 
        buf = new char[size];
        bufferSize = size;
        _hasWritten = false;
        _hasFlushed = false;
    }

    /**
     * Creates a new, uninitialized servlet output stream with a default
     * buffer size.
     */
    public BufferedWriter()
    {
        this(1 * 1024);
    }

    /**
     * Initializes the iwriter with the specified raw writer.
     * @param out the raw writer
     */
    public void init(Writer out, int bufSize)
    {
        // make sure that we don't have anything hanging around between
        // init()s -- this is the fix for the broken pipe error being
        // returned to the browser
        initNewBuffer(out, bufSize);
    }

    /**
     * Initializes the output stream with the specified raw output stream.
     * @param out the raw output stream
     */
    void initNewBuffer (Writer out, int bufSize)
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){   //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"init", "initNewBuffer size --> "+bufSize);
        }
        this.out = out;
        this.except = null;
        if (buf.length != bufSize)
        {
            bufferSize = bufSize;
            buf = new char[bufferSize];
        }
    }

    /**
     * Finishes the current response.
     */
    public void finish() throws IOException 
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){ //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"finish", "finish");
        }
        if ( length == -1 && total != 0 )
            length = total;
		//PK89810 Start
		if (WCCustomProperties.FINISH_RESPONSE_ON_CLOSE) {			
			WebContainerRequestState reqState = WebContainerRequestState.getInstance(false);		
			if (reqState==null || reqState.getAttribute("com.ibm.ws.webcontainer.appIsArdEnabled")==null){
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        			logger.logp(Level.FINE, CLASS_NAME,"finish", "finishresponseonclose and appIsNotArdEnabled, setLastBuffer to true");
				response.setLastBuffer(true);	
			}       	
		}//PK89810 End
        flush();
    }

    /**
     * Resets the output stream for a new connection.
     */
    public void reset()
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"reset", "reset");
        }
        out = null;
        //     obs = null;
        count = 0;
        total = 0;
        limit = -1;
        length = -1;
        committed = false;
        _hasWritten = false;
        _hasFlushed = false;
        response = null;
    }

    /**
     * Returns the total number of chars written so far.
     */
    public int getTotal()
    {
        return total;
    }

    /**
     * Sets an observer for this output stream. The observer will be
     * notified when the stream is first written to.
     */
    public void setObserver(IOutputStreamObserver obs)
    {
        this.obs = obs;
        limit = -1;
    }

    /**
     * Returns whether the output has been committed or not.
     */
    public boolean isCommitted()
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"isCommitted", "isCommitted: " + committed);
        }
        return committed;
    }


    /**
     * Checks the output stream for a pending IOException that needs to be
     * thrown, or a content length that has been exceeded.
     * @param len the number of chars about to be written
     */
    protected void check() throws IOException 
    {
        // check for pending IOException
        if (except != null)
        {
            flush();
            throw except;
        }
    }

    /**
     * Writes a char. This method will block until the char is actually
     * written.
     * @param b the char
     * @exception IOException if an I/O error has occurred
     */
    public void write(int c) throws IOException 
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"write", "write --> " + c);
        } 

        if (!_hasWritten && obs != null)
        {
            _hasWritten = true;
            obs.alertFirstWrite();
        }

        if ( limit > -1 )
        {
            if (total >= limit)
            {
                throw new WriteBeyondContentLengthException();
            }
        }
        if (count == buf.length)
        {
            response.setFlushMode(false);
            flushChars();
            response.setFlushMode(true);
        }
        buf[count++] = (char)c;
        total++;
    }

    /**
     * Writes an array of chars. This method will block until all the chars
     * are actually written.
     * @param b the data to be written
     * @param off the start offset of the data
     * @param len the number of chars to write
     * @exception IOException if an I/O error has occurred
     */
    public void write(char[] b, int off, int len) throws IOException 
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"write", "write total: "+total+" len: "+len+" limit: "+limit+" buf.length: "+buf.length+" count: "+count);
        } 
        if (len < 0)
        {
            logger.logp(Level.SEVERE, CLASS_NAME,"write", "Illegal.Argument.Trying.to.write.chars");
            throw new IllegalArgumentException();
        }

        if (!_hasWritten && obs != null)
        {
            _hasWritten = true;
            obs.alertFirstWrite();
        }

        if ( limit > -1 )
        {
            if (total + len > limit)
            {
                len = limit - total;
                except = new WriteBeyondContentLengthException();
            }
        }

        if (len >= buf.length)
        {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"write", "len >= buf.length");
            } 
            response.setFlushMode(false);
            flushChars();
            total += len;
            writeOut(b, off, len);
            //	out.flush(); moved to writeOut	277717    SVT:Mixed information shown on the Admin Console    WAS.webcontainer    
            response.setFlushMode(true);
            check();
            return;
        }
        int avail = buf.length - count;
        if (len > avail)
        {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"write", "len >= avail");
            } 
            response.setFlushMode(false);
            flushChars();
            response.setFlushMode(true);
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
        total += len;
        check();
    }

    /**
     * Flushes the output stream.
     */
    public void flush() throws IOException
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"flush", "flush");
        } 
        flushChars();
    }

    /**
     * Flushes the writer chars.
     */
    protected void flushChars() throws IOException 
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"flushChars", "flushChars");
        } 
        if (!committed)
        {
            if (!_hasFlushed && obs != null)
            {
                _hasFlushed = true;
                obs.alertFirstFlush();
            }
        }
        committed = true;
        if (count > 0)
        {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
				logger.logp(Level.FINE, CLASS_NAME,"flushChars"," Count ="+count);
			}
            writeOut(buf, 0, count);
            //	out.flush(); moved to writeOut	277717    SVT:Mixed information shown on the Admin Console    WAS.webcontainer    
            count = 0;
        }else {//PK22392 start
    		if(response.getFlushMode()){
    			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
    				logger.logp(Level.FINE, CLASS_NAME,"flushChars"," Count 0 still flush mode is true , forceful flush");
    			}
    			response.flushBufferedContent();
    		}//PK22392 END
    		else{
    			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
    				logger.logp(Level.FINE, CLASS_NAME,"flushChars"," flush mode is false");
    			}
    		}
        }
    }

    /**
     * Prints a string.
     * @exception IOException if an I/O error has occurred
     */
    public void print(String s) throws IOException 
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"print", "print --> ", s);
        } 
        if (!_hasWritten && obs != null)
		{
			_hasWritten = true;
			obs.alertFirstWrite();
		}
        int len = s.length();
        if ( limit > -1 )
        {
            if (total + len > limit)
            {
                len = limit - total;
                except = new WriteBeyondContentLengthException();
            }
        }

        int off = 0;
        while (len > 0)
        {
            int n = buf.length - count;
            if (n == 0)
            {
            	response.setFlushMode(false);
                flushChars();
                response.setFlushMode(true);
                n = buf.length - count;
            }
            if (n > len)
            {
                n = len;
            }

            s.getChars(off, off + n, buf, count);

            count += n;
            total += n;
            off += n;
            len -= n;
        }
        check();
    }

    /**
     * Closes the servlet output stream.
     */
    public void close() throws IOException 
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"close", "close");
        } 
        // Were we requested to close the underlying stream ?
        finish();
        try
        {
            // 104771 - alert the observer that the underlying stream is being closed
            obs.alertClose();

            // don't close the underlying stream...we want to reuse it
            // out.close();
        }
        catch (Exception ex)
        {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ex, "com.ibm.ws.webcontainer.srt.BufferedWriter.close", "397", this);
        }
    }

    public void setLimit(int lim)
    {
        limit = lim;
    }
    
    public void setResponse(IResponse resp) {
    	response = resp;
    }

    /*
     * Writes to the underlying stream
     */
    protected void writeOut(char[] buf, int offset, int len)
    throws IOException
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"writeOut", "writeOut --> "+len);
        } 
        try
        {
            out.write(buf, offset, len);
            out.flush();	//277717    SVT:Mixed information shown on the Admin Console    WAS.webcontainer
        }
        catch (IOException ioe)
        {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ioe, "com.ibm.ws.webcontainer.srt.BufferedWriter.writeOut", "416", this);
            count = 0;

            // begin pq54943
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"writeOut", "IOException occurred in writeOut method, observer alerting close.");
            } 
            // IOException occurred possibly due to SocketError from early browser closure...alert observer to close writer
            obs.alertClose();
            // end pq54943

            // let the observer know that an exception has occurred...
            obs.alertException();

            throw ioe;
        }
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public void setBufferSize(int size)
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setBufferSize", "setBufferSize --> "+size);
        } 
        if (total > 0)
        {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"setBufferSize", "setBufferSize(): illegal state--> already wrote " + total + " bytes");
            } 
            throw new IllegalStateException(nls.getString("Cannot.set.buffer.size.after.data","Can't set buffer size after data has been written to stream"));
        }
        initNewBuffer(out, size);
    }             

    public void clearBuffer()
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"clearBuffer", "clearBuffer");
        } 
        if (isCommitted())
        {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"clearBuffer", "clearBuffer(): illegal state--> stream is committed ");
            } 
            throw new IllegalStateException();
        }
        total = 0;
        count = 0;
        _hasWritten = false;
    }

    public void flushBuffer() throws IOException{
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"flushBuffer", "flushBuffer");
        } 
        flush();
    }
}
