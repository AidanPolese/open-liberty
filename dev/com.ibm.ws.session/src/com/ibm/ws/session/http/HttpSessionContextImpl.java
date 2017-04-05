/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/ws/session/http/HttpSessionContextImpl.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:52:35 [1/9/09 15:01:27]
 *
 * @(#)file   HttpSessionContextImpl.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/

package com.ibm.ws.session.http;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class HttpSessionContextImpl implements HttpSessionContext {

    // ----------------------------------------
    // Class Constructor
    // ----------------------------------------
    public HttpSessionContextImpl() {
        super();
    }

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------

    /**
     * Method getSession
     * 
     * @deprecated @see
     *             javax.servlet.http.HttpSessionContext#getSession(java.lang.
     *             String)
     */
    public HttpSession getSession(String arg0) {
        return null;
    }

    /**
     * Method getIds
     * 
     * @deprecated @see javax.servlet.http.HttpSessionContext#getIds()
     */
    public Enumeration getIds() {
        Vector v = new Vector();
        return v.elements();
    }

}