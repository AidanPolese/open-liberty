// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.util;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("unchecked")
public class ArrayEnumeration implements java.util.Enumeration
{
    private Object[] _array;
    private int _index = 0;

    /**
     * ArrayEnumeration constructor comment.
     */
    public ArrayEnumeration(Object[] array) 
    {
        _array = array;
    }

    /**
     * hasMoreElements method comment.
     */
    public boolean hasMoreElements() 
    {
        if (_array == null)
        {
            return false;
        }
        else
        {
            return _index < _array.length;
        }
    }
    
    /**
     * nextElement method comment.
     */
    public Object nextElement() 
    {
        if (_array == null)
        {
            return null;
        }
        else
        {
            synchronized(this){
                if (_index < _array.length)
                {
                    Object obj = _array[_index];
                    _index++;
                    return obj;
                }
                else
                {
                    return null;
                }
            }
        }
    }
}
