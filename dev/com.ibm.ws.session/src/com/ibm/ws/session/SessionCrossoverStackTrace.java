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
 *  @(#) 1.1 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/SessionCrossoverStackTrace.java, WASCC.web.session, WASX.SERV1, o0901.11 10/13/06 15:50:29 [1/9/09 15:00:59]
 *
 * @(#)file   SessionCrossoverStackTrace.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

public class SessionCrossoverStackTrace extends java.lang.Exception {

    private static final long serialVersionUID = -3025753313833590830L;

    public SessionCrossoverStackTrace() {
        super();
    }

    public SessionCrossoverStackTrace(String s) {
        super(s);
    }

}
