/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.wsspi.webcontainer.servlet;

import java.util.Vector;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import com.ibm.websphere.servlet.response.IResponse;

/**
 *  RTC 160610. Adding extra methods to ServletResponse
 *  in order to use this interface instead of IExtendedResponse.
 */
public interface ServletResponseExtended extends ServletResponse {

    public IResponse getIResponse();

    public Vector[] getHeaderTable();

    public void addSessionCookie(Cookie cookie);

    public void setHeader(String name, String s, boolean checkInclude);

    public int getStatusCode();

}