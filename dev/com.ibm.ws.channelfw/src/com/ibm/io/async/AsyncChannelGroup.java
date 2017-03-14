// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 08/31/05 gilgen      LIDB3618-2      M2/M3 drops 
// 09/05/05 gilgen      302453          M3 code cleanup/perf improvements
// 09/22/05 gilgen      307313          Code cleanup/improvements
// 10/02/05 gilgen      308856.1        Code review comments
// 01/19/06 gilgen      336062          new threading model 
// 02/10/06 gilgen      345836          make method public for use in traces

package com.ibm.io.async;

/**
 * Encapsulation of a single specific AIO channel group.
 */
public class AsyncChannelGroup {

    /** IO handle for this group */
    private ResultHandler resultHandler = null;
    /** completion port for this object */
    private long completionPort = 0;
    /** Name for this group */
    private String myGroupName = null;

    private static IAsyncProvider provider = AsyncLibrary.getInstance();

    // private static final TraceComponent tc = Tr.register(
    // AsyncChannelGroup.class,
    // TCPChannelMessageConstants.TCP_TRACE_NAME,
    // TCPChannelMessageConstants.TCP_BUNDLE);

    /**
     * Constructor.
     * 
     * @param name
     * @throws AsyncException
     */
    public AsyncChannelGroup(String name) throws AsyncException {

        this.completionPort = provider.getNewCompletionPort();
        this.myGroupName = name;
        this.resultHandler = new ResultHandler(name, this.completionPort);
    }

    /**
     * @return ResultHandler for this channel group
     */
    protected ResultHandler getResultHandler() {
        return this.resultHandler;
    }

    /**
     * Activate the AIO channel group.
     */
    public void activate() {
        this.resultHandler.activate();
    }

    /**
     * @return completionPort for this channel group
     */
    public long getCompletionPort() {
        return this.completionPort;
    }

    /**
     * Query the name of this work group.
     * 
     * @return String
     */
    protected String getGroupName() {
        return this.myGroupName;
    }

    /**
     * Trigger the debug printing of the current statistics.
     */
    public void dumpStatistics() {
        this.resultHandler.dumpStatistics();
    }

}