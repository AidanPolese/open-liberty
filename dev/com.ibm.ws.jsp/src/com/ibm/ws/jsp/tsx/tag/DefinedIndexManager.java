// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.tsx.tag;

/*
 * @(#)DefinedIndexManager.java 1.0 04/25/98
 *
 * Copyright (c) 1995-1998 International Business Machines. All Rights Reserved.
 *
 * 4/24/98   Created                                DPJ
 *
 */

import java.util.Vector;

/**
 * Manage the indexes for Template Syntax
 */

public class DefinedIndexManager {

    protected Vector indexNames;
    protected int lastIndexIndex, i;

    public DefinedIndexManager() {
        indexNames = new Vector();
        lastIndexIndex = 0;
    }
    // add a user defined index to the vector
    public void addIndex(String newIndex) {
        indexNames.addElement(newIndex);
    }

    // begin 150288
    // remove a user defined index from the vector
    public void removeIndex(String oldIndex) {
        indexNames.removeElement(oldIndex);
    }
    // end 150288

    // see if the passed in index exists in the vector
    public boolean exists(String index) {
        boolean exists = false;

        if (indexNames.isEmpty() == false) {
            for (i = 0; i < indexNames.size(); i++) {
                if (index.equals((String) indexNames.elementAt(i))) {
                    exists = true;
                    break;
                }
            }
        }

        return exists;
    }
    // return an index that does not already exist in the vector
    // we default to tsx# as an index if the user doesn't specify one
    public String getNextIndex() {
        String newIndex;

        do {
            newIndex = "tsx" + String.valueOf(lastIndexIndex);
            lastIndexIndex++;
        }
        while (exists(newIndex) == true);

        addIndex(newIndex);

        return newIndex;
    }
}
