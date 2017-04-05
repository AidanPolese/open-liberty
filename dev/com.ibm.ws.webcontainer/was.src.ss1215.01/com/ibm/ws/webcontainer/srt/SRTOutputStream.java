// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.srt;

import java.io.IOException;
import java.io.OutputStream;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.ibm.wsspi.buffermgmt.WsByteBuffer;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.util.ByteBufferWriter;
import com.ibm.wsspi.webcontainer.util.IOutputStreamObserver;

/**
 * Implements a ServletOutputStream for a ISRPConnection.
 */
public class SRTOutputStream extends javax.servlet.ServletOutputStream implements ByteBufferWriter
{
	private OutputStream _conn;
	private IOutputStreamObserver _observer;
protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.srt");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.srt.SRTOutputStream";

	/**
	 * This method was created in VisualAge.
	 */
	public SRTOutputStream()
	{
	}

	public void init(OutputStream str)
	{
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"init", "Initializing");
        }
		_conn = str;
	}

	public void flush() throws IOException
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"flush", "Flushing");
        }
		_conn.flush();
	}

	public void reset()
	{
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"reset", "Reseting");
        }
		_conn = null;
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void close() throws java.io.IOException
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"close", "Closing");
        }

		if (_observer != null) {
			_observer.alertClose();
        }
		super.close();
	}
	/**
	 * This method was created in VisualAge.
	 * @param obs com.ibm.servlet.engine.srp.IOutputStreamObserver
	 */
	protected void setObserver(IOutputStreamObserver obs)
	{
		_observer = obs;
	}
	/**
	 * This method was created in VisualAge.
	 * @param b byte[]
	 * @param off int
	 * @param len int
	 */
	public void write(byte[] b, int off, int len) throws IOException
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"write", "Writing");
        }

		if (_observer != null)
			_observer.alertFirstWrite();
		_conn.write(b, off, len);
	}
	/**
	 * This method was created in VisualAge.
	 * @param b int
	 */
	public void write(int b) throws java.io.IOException
	{
		byte[] buf = new byte[1];
		buf[0] = (byte) b;
		write(buf, 0, 1);
	}
	
//	BEIGN ZHJ	
	public void writeByteBuffer(WsByteBuffer[] buf) {
		
		((ByteBufferWriter)_conn).writeByteBuffer(buf);
	}
//END ZHJ	
}
