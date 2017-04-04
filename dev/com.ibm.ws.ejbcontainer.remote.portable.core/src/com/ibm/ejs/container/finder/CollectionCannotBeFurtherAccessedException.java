/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.finder;

public class CollectionCannotBeFurtherAccessedException
                extends RuntimeException
{
    private static final long serialVersionUID = -2238613038201337470L;

    /**
     * Constructs a <code>CollectionCannotBeFurtherAccessedException</code> with <tt>null</tt>
     * as its error message string.
     */
    public CollectionCannotBeFurtherAccessedException() {
        super();
    }

    /**
     * Constructs a <code>CollectionCannotBeFurtherAccessedException</code>, saving a reference
     * to the error message string <tt>s</tt> for later retrieval by the
     * <tt>getMessage</tt> method.
     * 
     * @param s the detail message.
     */
    public CollectionCannotBeFurtherAccessedException(String s) {
        super(s);
    }
}
