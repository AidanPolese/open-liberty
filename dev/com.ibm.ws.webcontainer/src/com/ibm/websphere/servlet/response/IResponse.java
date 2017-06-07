//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//PK63328        04/30/08     pmdinh              Response is chunked by PK22392
//        557339         10/16/08     mmolden             FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent
//

package com.ibm.websphere.servlet.response;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import com.ibm.websphere.servlet.request.IRequest;

/**
 * 
 * Interface that the webcontainer expects the response objects to implement. The methods
 * on this interface will be called by the webcontainer in the process of writing back
 * the response.
 * 
 * @ibm-api
 */

public interface IResponse {
  /**
   * Sets the HTTP status code
     * @param code the HTTP status code
   */
  public void setStatusCode(int code);

  /**
   * Get the OutputStream
   * @return OutputStream the output stream
   **/
  public ServletOutputStream getOutputStream() throws IOException;

  /**
   * Check if the response is committed yet
   * @return boolean whether or not the response is committed
   **/
  public boolean isCommitted();

  /**
   * Add a header
     * @param name the name of the header 
     * @param name the value of the header 
   **/
  public void addHeader(String name, String value);

  /**
   * Add a header
     * @param name the name of the header
     * @param value the value of the header
   */
  public void addHeader(byte[] name, byte[] value);

  /**
   * Add a header as a long value
     * @param name the header name
     * @param t the header date value
   */
  public void addDateHeader(String name, long t);

  /**
   * Add a header as an int value
     * @param name the header name
     * @param i the header int value
   */
  public void addIntHeader(String name, int i);

  /**
   * Set date header as a long value
     * @param name the header name
     * @param t the header date value
   */
  public void setDateHeader(String name, long t);

  /**
   * Set a header as an int
     * @param name the header name
     * @param i the header int value
   */
  public void setIntHeader(String name, int i);

  /**
	 * Gets all the header names (keys)
	 * @return Collection of header names
	 */
	// I had to change this to an Enumeration because SIP implements
	// both IRequest and IResponse in the same class and the method
	// signatures have to match for them to compile
	public Enumeration getHeaderNames();

	/**
	 * Gets all the header values for a particular header name
	 * @return Collection of header v
	 */
	public Enumeration getHeaders(String name);

	/**
   * Get a header
     * @param name the header name
   * @return String the header value
   */
  public String getHeader(String name);

  /**
   * Get the header table
   * @return Vector the header names
   */
  public Vector[] getHeaderTable();

  /**
   * Get a header
     * @param name the header name
   * @return String the header value
   */
  public String getHeader(byte[] name);

  /**
   * Returns true is the header with the supplied name is already present
     * @param name the header name
   * @return boolean whether the header is present
   */
  public boolean containsHeader(String name);

  /**
   * Returns true if the header with the supplied name is already present
     * @param name the header name
   * @return boolean whether the header is present
   */
  public boolean containsHeader(byte[] name);

  /**
   * Removes the header with the given name
     * @param name the header name
   */
  public void removeHeader(String name);

  /**
   * Removes the header with the given name
     * @param name the header name
   */
  public void removeHeader(byte[] name);

  /**
   * Clears the headers datastructure
   */
  public void clearHeaders();

  /**
   * Get the webcontainer channel request object for this response
   * @return IWCCRequest the associated request for this response
   **/
  public IRequest getWCCRequest();

  /**
   * Sets the flush mode. When set to true, the subsequent flush calls *must*
   * write the contents to the wire. Otherwise, the contents written can be
   * buffered by the underlying layer.
   * 
   * @param flushToWire
   */
  public void setFlushMode(boolean flushToWire);
  
  public void setIsClosing(boolean isClosing);

  /**
   * Gets the flush mode.
   * 
   * @return flushToWire
   */

  public boolean getFlushMode();

  /**
   * Flushes the contents to wire
   * 
   */
  public void flushBufferedContent();

  /**
   * Sets the reason in the response.
   * 
   * @param reason
   */
  public void setReason(String reason);

  /**
   * Sets the reason in the response.
   * @param reason
   */
  public void setReason(byte[] reason);

  /**
   * Add a cookie to the response.
   * @param cookie
   */
  public void addCookie(Cookie cookie);

  /**
   * Get all the cookies for the response
   * @return Cookie[] containing all the Cookies.
   */
  public Cookie[] getCookies();

  /**
   * Prepare to write the headers
   */
  public void prepareHeadersForWrite();

  /**
   * Write the headers
   */
  public void writeHeaders();

  /**
   * @param name
   * @param s
   */
  public void setHeader(String name, String s);

  /**
   * @param name
   * @param bs
   */
  public void setHeader(byte[] name, byte[] bs);

  /**
   * Set the content type for the response
   * @param value
   */
  public void setContentType(String value);

  /**
   * Set the content type for the response
   * @param value
   */
  public void setContentType(byte[] value);

  /**
   * Set the content language for the response
   * @param value
   */
  public void setContentLanguage(String value);

  /**
   * Set the content length for the response
   * @param length
   */
  public void setContentLength(int length);

  /**
   * Set the content language for the response
   * @param value
   */
  public void setContentLanguage(byte[] value);

  // Added for ARD, for performance in parsing
  /**
   * Set whether to allocate direct or indirect byte buffers
   * @param allocateDirect
   */
  public void setAllocateDirect(boolean allocateDirect);

  /**
   * Get whether to allocate direct or indirect byte buffers
   */
  public boolean isAllocateDirect();

  // Added for ARD, for performance in parsing

  /**
   * Set the last buffer mode
   * @param writeLastBuffer
   */
  public void setLastBuffer(boolean writeLastBuffer); // PK63328

  public void releaseChannel();

  public void removeCookie(String cookieName);
  
  // LIBERTY
  // Added for Liberty support.
  public void resetBuffer();

  // LIBERTY
  // Added for Liberty support.
  public int getBufferSize();

  // LIBERTY
  // Added for Liberty support.
  public void setBufferSize(int bufferSize);

  // LIBERTY
  // Added for Liberty support.
  public void flushBuffer() throws IOException;

}
