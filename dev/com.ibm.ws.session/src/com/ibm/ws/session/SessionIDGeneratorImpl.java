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
 *  @(#) 1.3 WEB/ws/code/web.session.core/src/com/ibm/ws/session/SessionIDGeneratorImpl.java, WASCC.web.session.core, CCX.WEB, nn0720.02 3/13/07 12:00:47 [5/31/07 17:29:43]
 *
 * @(#)file   SessionIDGeneratorImpl.java
 * @(#)version   1.3
 * @(#)date      3/13/07
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import com.ibm.ws.session.utils.IDGeneratorImpl;

public class SessionIDGeneratorImpl extends IDGeneratorImpl {

    public SessionIDGeneratorImpl() {
        super();
    }

    public SessionIDGeneratorImpl(int sessionIDLength) {
        super(sessionIDLength);
    }

    public String getSessionID() {
        return getID();
    }

}