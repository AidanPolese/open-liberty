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
package com.ibm.ws.security.wim.xpath;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.security.wim.xpath.mapping.datatype.LogicalNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.XPathNode;

public class FederationLogicalNode extends LogicalNode {
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_LONG_2012;

    @Trivial
    public short getNodeType()
    {
        return XPathNode.NODE_FED_LOGICAL;
    }
}
