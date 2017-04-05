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
package com.ibm.ws.security.wim.xpath.mapping.datatype;

import java.util.HashMap;
import java.util.Iterator;

import com.ibm.websphere.ras.annotation.Trivial;

@Trivial
public class PropertyNode implements XPathPropertyNode
{
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_LONG_2012;

    private String operator = null;
    private String name = null;
    private Object value = null;
    private boolean inRepos = true;

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setValue(Object value)
    {

        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public short getNodeType()
    {
        return XPathNode.NODE_PROPERTY;
    }

    @SuppressWarnings("unchecked")
    public Iterator getPropertyNodes(HashMap attNodeMap)
    {
        HashMap nodeMap = attNodeMap;

        if (nodeMap == null) {
            nodeMap = new HashMap();
        }
        nodeMap.put(Integer.valueOf(this.hashCode()), this);
        return nodeMap.values().iterator();
    }

    public void setPropertyLocation(boolean inRepos)
    {
        this.inRepos = inRepos;
    }

    public boolean isPropertyInRepository()
    {
        return inRepos;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s = s.append(name + " " + operator + " " + value.toString());
        return s.toString();
    }
}