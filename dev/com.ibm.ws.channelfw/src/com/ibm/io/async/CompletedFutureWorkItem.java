//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
// 01/17/06 gilgen     336062          created file
// 03/14/06 wigger     351108          remove unused ResultHandler reference

package com.ibm.io.async;

/**
 * An item that represents a completed IO request.
 */
public class CompletedFutureWorkItem {

    // the future that was completed
    protected AsyncFuture future;
    // the number of bytes read/wrote
    protected int numBytes;
    // the return code from the operation
    protected int returnCode;

    // the result handler that should process this item

    /**
     * Constructor.
     * 
     * @param _future
     * @param _numBytes
     * @param _returnCode
     */
    public CompletedFutureWorkItem(AsyncFuture _future, int _numBytes, int _returnCode) {
        this.future = _future;
        this.numBytes = _numBytes;
        this.returnCode = _returnCode;
    }
}
