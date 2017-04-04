// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

//  CHANGE HISTORY
//Defect        Date        Modified By     Description
//--------------------------------------------------------------------------------------
//366965        06/22/06    mmolden         Reduce number of lines enclosed in synchronized in    WASCC.web.webcontainer

package com.ibm.ws.webcontainer.filter;

import java.util.ArrayList;


/**
 * Represents the entities that are contained in a filter chain.
 *
 */
@SuppressWarnings("unchecked")
class FilterChainContents
{
    private ArrayList _filterNames = new ArrayList();

    boolean _hasFilters = false;

    FilterChainContents()
    {
    }

    /** public interface **/

    /**
     * Gets the filter name array list for this chain
     *
     * @return the filter names for this chain
     */
    public ArrayList getFilterNames()
    {
        return _filterNames;
    }

    /**
     * Adds a filter name to the name vector for this chain
     *
     * @param the filter name to add
     */
    public void addFilter(String filterName)
    {
        _hasFilters = true;
        _filterNames.add(filterName);
    }

}
