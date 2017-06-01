//IBM Confidential OCO Source Material
//  5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.

package com.ibm.ws.jsp.runtime;

import javax.servlet.jsp.PageContext;

public class PageContextPool {
    private PageContext pool[];
    private int index;

    public PageContextPool(int size) {
        pool = new PageContext[size];
        index = 0;
    }

    public boolean add(PageContext pageContext) {
        if (index < pool.length) {
            pool[index++] = pageContext;
            return true;
        }
        return false;
    }

    public PageContext remove() {
        if (index > 0) {
            PageContext pageContext = pool[--index];
            pool[index] = null;
            return pageContext;
        }
        return createPageContext();
    }

    protected PageContext createPageContext() {
        return null;
    }
}
