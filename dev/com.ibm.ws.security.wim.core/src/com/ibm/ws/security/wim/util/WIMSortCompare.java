/************** Begin Copyright - Do not add comments here **************
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.util;

import java.util.Comparator;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.security.wim.model.Entity;
import com.ibm.wsspi.security.wim.model.SortControl;

/**
 * @author Ankit Jain
 * Sorting comparable class.
 */
@Trivial
public class WIMSortCompare<T> implements Comparator<T>
{
    SortControl sortControl = null;

    /**
     * Construct an WIMSortCompare
     */
    public WIMSortCompare(SortControl sortControl)
    {
        this.sortControl = sortControl;
    }

    /**
     * Compares its two objects for order. Returns a negative integer, zero, or 
     * a positive integer as the first argument is less than, equal to, or greater than the second.
     * @param obj1 the first object
     * @param obj2 the second object
     * @return a negative integer, zero, or a positive integer as the first argument is less than, 
     *         equal to, or greater than the second. 

     */
    public int compare(T entity1, T entity2)
    {
        SortHandler shandler = new SortHandler(sortControl);
        return shandler.compareEntitysWithRespectToProperties((Entity) entity1, (Entity) entity2);

    }
}
