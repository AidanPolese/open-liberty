package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 * Reason           Date     Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 * PM54465(724046) 20/12/11  slaterpa   Log becomes corrupt because checkpoint not triggered
 * ============================================================================
 */

/**
 * This is never thrown. It is simply used to record the fact that the padding
 * space available went negative. We do not expect that it should.
 * 
 */
public final class NegativePaddingSpaceException extends ObjectManagerException
{

    private static final long serialVersionUID = -4617412147654063010L;

    /**
     * 
     * @param Object creating the exception.
     * @param paddingSpaceAvailable
     */
    protected NegativePaddingSpaceException(Object source,
                                            long paddingSpaceAvailable)
    {
        super(source,
              NegativePaddingSpaceException.class,
              new Object[] { Long.valueOf(paddingSpaceAvailable) });
    }
}
