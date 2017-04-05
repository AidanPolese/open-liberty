// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

final class Element extends QueueElement
{

    Element(Object key, Object object)
    {
	this.key = key;
	this.object = object;
    }

    public String toString()
    {
	return "Element: " + key.toString() + " " + object.toString();
    }

    final Object key;
    final Object object;
}
