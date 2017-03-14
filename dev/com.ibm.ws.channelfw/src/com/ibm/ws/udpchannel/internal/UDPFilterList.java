//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//d306341		mjohn256  Add RAS logging support to UDP Channel.

package com.ibm.ws.udpchannel.internal;

import com.ibm.ws.tcpchannel.internal.FilterList;

/**
 * Contains the only the protected members of the FilterList object that I
 * couldn't call in my AccessLists object.
 */
public class UDPFilterList extends FilterList {
    @Override
    protected void setActive(boolean value) {
        super.setActive(value);
    }

    @Override
    protected boolean getActive() {
        return super.getActive();
    }

    @Override
    protected void buildData(String[] data, boolean validateOnly) {
        super.buildData(data, validateOnly);
    }

}
