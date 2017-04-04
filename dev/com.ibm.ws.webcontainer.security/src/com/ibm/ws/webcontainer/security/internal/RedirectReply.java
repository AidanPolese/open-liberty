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
package com.ibm.ws.webcontainer.security.internal;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.security.CookieHelper;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;

/**
 * In case where authentication failed, and the challenge type is Custom,
 * the user is redirected to the (re)login page to provide authentication data.
 * This sends a 302 and specified the URL to be redirected to. This URL is
 * obtained
 * from the security configuration.
 */
public class RedirectReply extends WebReply {

    private final List<Cookie> cookieList;

    public RedirectReply(String url,
                         List<Cookie> list) {
        super(HttpServletResponse.SC_MOVED_TEMPORARILY, url);
        cookieList = list;
    }

    @Override
    public void writeResponse(HttpServletResponse resp) throws IOException {
        if (resp.isCommitted())
            return;

        if (cookieList != null && cookieList.size() > 0) {
            CookieHelper.addCookiesToResponse(cookieList, resp);
        }

        if (getStatusCode() != HttpServletResponse.SC_SEE_OTHER) {
            resp.sendRedirect(resp.encodeURL(message));
        } else {
            if (resp instanceof IExtendedResponse) {
                ((IExtendedResponse) resp).sendRedirect303(resp.encodeURL(message));
            } else {
                resp.sendRedirect(resp.encodeURL(message));
            }
        }
    }

}