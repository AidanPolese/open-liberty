/************** Begin Copyright - Do not add comments here **************
 *
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
package com.ibm.ws.security.wim;

import com.ibm.wsspi.security.wim.model.Root;

public class PageCacheEntry
{
    /**
     * IBM Copyright string.
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_LONG_2012;

    private int totalSize;
    private Root root = null;

    /**
     * Constructor for PageCacheEntry.
     */
    public PageCacheEntry()
    {
        super();
    }

    /**
     * Constructs the PageCacheEntry with the provided input parameters
     * @param totalSize the total size of a paging search results
     * @param dg a datagraph contains the to be cached entity

     */
    public PageCacheEntry(int totalSize, Root rootDO)
    {
        super();
        this.totalSize = totalSize;
        root = rootDO;
    }

    /**
     * Returns the total size of a paging search
     * @return the total size of a paging search
     */
    public int getTotalSize()
    {
        return this.totalSize;
    }

    /**
     * Returns the paged DataGraph object
     * @return  the paged DataGraph object
     */
    public Root getDataObject()
    {
        return root;
    }

    /**
     * Sets the total size of a paging search
     * @param totalSize  the total size of a paging search
     */
    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }

    /**
     * Sets the list of entities which will be stored in the paging cache
     * @param entities  a list of entities
     */
    public void setDataObject(Root rootDO)
    {
        root = rootDO;
    }
}
