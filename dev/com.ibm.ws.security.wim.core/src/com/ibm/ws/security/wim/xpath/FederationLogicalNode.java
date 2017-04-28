/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.xpath;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.security.wim.xpath.mapping.datatype.LogicalNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.XPathNode;

public class FederationLogicalNode extends LogicalNode {

    @Override
    @Trivial
    public short getNodeType() {
        return XPathNode.NODE_FED_LOGICAL;
    }
}
