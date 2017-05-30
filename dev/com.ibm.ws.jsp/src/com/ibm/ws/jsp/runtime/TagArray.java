//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//Changes:
//Feature 4147-24 "JSP 2.1 upgrade: Incorporate resource injection engine"  2007/03/30 Curtiss Howard

package com.ibm.ws.jsp.runtime;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.Tag;

import com.ibm.ws.jsp.taglib.annotation.AnnotationHandler;

public class TagArray {
    private Tag[] tags;

    private int next = -1;
    
    private AnnotationHandler tagAnnotationHandler;
    
    public TagArray(int size, ServletContext context) {
        tags = new Tag[size];
        
        // LIDB4147-24
        
        this.tagAnnotationHandler = AnnotationHandler.getInstance
             (context);
    }

    public Tag get() {
        Tag tag = null;
        if (next >= 0) {
            tag = tags[next--];
        }
        return tag;
    }

    public void put(Tag tag) {
        if (next < (tags.length - 1)) {
            tags[++next] = tag;
            return;
        }
        
        this.tagAnnotationHandler.doPreDestroyAction (tag);   // LIDB4147-24
        
        tag.release();
    }

    public void releaseTags() {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] != null) {
                this.tagAnnotationHandler.doPreDestroyAction (tags[i]);   // LIDB4147-24
                 
                tags[i].release();
                tags[i] = null;
            }
        }
    }
    
    public int numberInUse() {
        return next+1;
    }
}
