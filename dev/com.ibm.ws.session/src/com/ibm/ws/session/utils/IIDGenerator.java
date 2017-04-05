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
 *  @(#) 1.1 WEB/ws/code/web.session.core/src/com/ibm/wsspi/session/IIDGenerator.java, WASCC.web.session.core, CCX.WEB, nn0720.02 10/13/06 15:53:13 [5/31/07 17:29:45]
 *
 * @(#)file   IIDGenerator.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session.utils;

/**
 * This interface allows the session manager creator to plug in his own
 * implementation of the Session ID Generator. The default id generator
 * generates
 * ids that are compatible with the base HTTP Session manager in WebSphere.
 * <p>
 * 
 * @author Aditya Desai
 * 
 */
public interface IIDGenerator {

    /**
     * Returns a new unique session id
     * <p>
     * 
     * @return String
     */
    public String getID();
}