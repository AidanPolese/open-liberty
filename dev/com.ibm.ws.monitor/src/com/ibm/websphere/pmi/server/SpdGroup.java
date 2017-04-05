// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/**
 * History:
 * CMVC 86523: create the file - wenjian
 * 
 * The interface is used for Spd<Type>Aggregate data.
 */
package com.ibm.websphere.pmi.server;

import java.util.*;

public interface SpdGroup extends SpdData {
    public boolean addSorted(SpdData data); /* in a sorted order */

    public boolean add(SpdData data); /* add - not sorted */

    public boolean remove(SpdData data);

    public Iterator members();

    public void updateAggregate();
}
