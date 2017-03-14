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
// 09/01/05 gilgen      302453          M3 code updates

package com.ibm.io.async;

/**
 * Defines the method to be implemented by future listeners.
 * <p>
 * This interface is used by a future object to notify registered listeners that the operation represented by the future
 * has completed. These <em>callback</em> methods can perform significant work in the application code.
 * </p>
 */
public interface ICompletionListener {

    /**
     * This method is called by the future object when the operation has completed.
     * <p>
     * If the listener is registered with a future when the operation has already completed, the listener is called immediately during the
     * register operation.
     * </p>
     * <p>
     * The threads available for running completion callbacks are provided by the <code>ThreadPool</code>, under a policy encoded in
     * the <code>IResultThreadHandler</code>.
     * </p>
     * 
     * @param result
     *            the future that has completed.
     * @param userState
     *            the object that was passed in as an argument when the listener was registered, or <code>null</code> if the argument was
     *            <code>null</code>.
     * @see IAbstractAsyncFuture#addCompletionListener(ICompletionListener, Object)
     */
    public void futureCompleted(IAbstractAsyncFuture result, Object userState);

}