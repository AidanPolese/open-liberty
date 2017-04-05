// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.runtime;

public class UnsynchronizedStack extends java.util.ArrayList {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257291318249140273L;

	public UnsynchronizedStack() {}
        
    public UnsynchronizedStack(int initialSize) {
    	super(initialSize);
    }
    
    public Object push(Object item) {
        add(item);
        return item;
    }

    public Object pop() {
        Object obj = null;
        int len = size();
        if (len > 0)
            obj = remove(len - 1);
        return obj;
    }
}

