/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.srt;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.wsspi.http.HttpInputStream;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.util.WSServletInputStream;

//ALPINE - Switched from HttpInputStream to SRTInputStream to avoid IP issues

public class SRTInputStream extends WSServletInputStream
{
  protected InputStream in;
  
  protected long contentLength;
  private static TraceNLS nls = TraceNLS.getTraceNLS(SRTInputStream.class, "com.ibm.ws.webcontainer.resources.Messages");

  protected static final Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer");
  private static final String CLASS_NAME="com.ibm.ws.webcontainer.srt.SRTInputStream";

  @Override
  public void finish() throws IOException
  {
    this.in.close();
  }

  @Override
  public void init(InputStream in) throws IOException
  {
    this.in = in;
  }

  @Override
  public void setContentLength(long contentLength)
  {
    this.contentLength = contentLength;
  }

  @Override
  public int read() throws IOException
  {
    return this.in.read();
  }

  @Override
  public int read(byte[] output) throws IOException {
      
      return this.in.read(output, 0, output.length);
  }

  @Override
  public int read(byte[] output, int offset, int length) throws IOException {
  
      return this.in.read(output, offset, length);
  }
}
