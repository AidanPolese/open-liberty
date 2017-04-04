// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class implements SpdGroup interface and holds an arraylist member.
 *  
 */

package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.*;
import com.ibm.websphere.pmi.server.*;
import java.util.*;

public abstract class SpdGroupBase extends SpdDataImpl
                                   implements SpdGroup {
    ArrayList members = new ArrayList();

    // Constructor
    public SpdGroupBase(PmiModuleConfig moduleConfig, String name) {
        super(moduleConfig, name);
    }

    public SpdGroupBase(int dataId) {
        super(dataId);
    }

    /**
     * Insert the element into the members vector in sorted
     * lexicographical order
     */
    protected int insert(SpdData elt) {
        if (members.isEmpty()) {
            members.add(elt);
            return 0;
        }

        int first = 0;
        SpdData firstElt = (SpdData) members.get(first);
        int last = members.size() - 1;
        SpdData lastElt = (SpdData) members.get(last);

        if (elt.compareTo(firstElt) < 0) {
            members.add(first, elt);
            return first;
        } else if (elt.compareTo(lastElt) > 0) {
            members.add(last + 1, elt);
            return last + 1;
        }
        while (last > (first + 1)) {
            int middle = (first + last) / 2;
            SpdData midElt = (SpdData) members.get(middle);

            if (midElt.compareTo(elt) > 0) {
                last = middle;
                lastElt = midElt;
            } else {
                first = middle;
                firstElt = midElt;
            }
        }
        if ((elt.compareTo(firstElt) == 0) ||
            (elt.compareTo(lastElt) == 0)) {
            return -1;
        } else {
            members.add(last, elt);
            return last;
        }
    }

    /**
     * add a data in a sorted order
     */
    public synchronized boolean addSorted(SpdData data) {
        if (data == null) {
            return false;
        } else if (members.contains(data)) {
            return false;
        } else {
            return (insert(data) != -1);
        }
    }

    /**
     * append a data to the members list - not sorted
     */
    public synchronized boolean add(SpdData data) {
        if (data == null) {
            return false;
        } else if (members.contains(data)) {
            return false;
        } else {
            return members.add(data);
        }
    }

    /**
     * remove a data
     */
    public synchronized boolean remove(SpdData data) {
        if (data == null)
            return false;
        return members.remove(data);
    }

    /**
     * return all the members
     */
    public Iterator members() {
        return members.iterator();
    }

    public boolean isEnabled() {
        // fine grained
        /*
         * if (members.size() <= 0)
         * return false;
         * else
         */
        return super.isEnabled();
    }

    public void updateAggregate() {}

    public boolean isAggregate() {
        return true;
    }
}
