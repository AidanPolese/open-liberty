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
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/IPerformanceMetrics.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:53:17 [1/9/09 15:01:32]
 *
 * @(#)file   IPerformanceMetrics.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

/**
 * This interface provides a handle to query statistics about the sessions that
 * are
 * being managed by a Session Manager. These will be available as methods off
 * of a management interface (like an MBean) for external querying.
 * 
 */
public interface IPerformanceMetrics {

    /**
     * Returns the total number of sessions that are created (CountStatistic).
     */
    public long getSessionsCreated();

    /**
     * Returns the total number of sessions that are invalidated (CountStatistic).
     */
    public long getInvalidatedSessions();

    /**
     * Returns the total number of sessions that are currently accessed by
     * requests (RangeStatistic).
     */
    public long getActiveSessions();

    /**
     * Returns the total number of sessions that currently live in memory
     * (RangeStatistic).
     */
    public long getMemoryCount();

    /**
     * Returns the total number of session objects that are forced out of the
     * cache (CountStatistic).
     */
    public long getCacheDiscards();

    /**
     * Returns the total number of HTTP session affinities that are broken, not
     * counting WebSphere Application Server intentional breaks of session
     * affinity (CountStatistic).
     */
    public long getAffinityBreaks();

    /**
     * Returns the total number of sessions that are invalidated with timeout
     * (CountStatistic).
     */
    public long getInvalidatedByTimeout();

    /**
     * Returns the total number of requests for a session that no longer exists,
     * presumably because the session timed out (CountStatistic).
     */
    public long getAccessToNonExistentSession();

    /**
     * Returns the total total number of requests for valid sessions (session that
     * exists)
     * 
     */
    public long getSessionAccessCount();

}
