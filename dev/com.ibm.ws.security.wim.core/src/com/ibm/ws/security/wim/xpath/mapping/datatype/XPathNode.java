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
package com.ibm.ws.security.wim.xpath.mapping.datatype;

import java.util.HashMap;
import java.util.Iterator;

public interface XPathNode {

    /**
     * The node is a property node.
     */
    final static short NODE_PROPERTY = 0;
    /**
     * The node is a logical node.
     */
    final static short NODE_LOGICAL = 1;
    /**
     * The node is a parenthesis node.
     */
    final static short NODE_PARENTHESIS = 2;
    /**
     * The node is federation logical node.
     */
    final static short NODE_FED_LOGICAL = 4;
    /**
     * The node is federation parenthesis node.
     */
    final static short NODE_FED_PARENTHESIS = 8;

    //  void genSearchString(Object hint, StringBuffer searchBuffer);
    Iterator getPropertyNodes(HashMap propNodeMap);

    short getNodeType();
}
