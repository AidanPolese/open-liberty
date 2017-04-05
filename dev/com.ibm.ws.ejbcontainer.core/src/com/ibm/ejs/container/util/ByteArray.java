/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.util;

import com.ibm.ejs.container.BeanId;

/**
 * Provides hashcode and equals function for byte arrays. <p>
 * 
 * This EJB Container specific subclass of the util version of ByteArray
 * is provided to cache the beanId associated with the wrappered byte array
 * to improve performance. <p>
 **/
public final class ByteArray
                extends com.ibm.ejs.util.ByteArray // d181754
{
    /**
     * Create new ByteArray instance, containing the given array of bytes.
     */
    public ByteArray(byte[] d)
    {
        super(d); // d181754
    }

    /**
     * Default constructor
     */
    protected ByteArray()
    {
        super(); // d181754
    }

    /**
     * Copy constructor
     */
    protected ByteArray(ByteArray b)
    {
        super(b); // d181754
        beanId = b.beanId; // d181754
    }

    /**
     * Enables the ByteArray to hold on to a reference to an associated BeanId
     * if required. <p>
     * 
     * Performance improvment. <p>
     * 
     * @param id BeanId associated with the wrappered byte array.
     **/
    public final void setBeanId(BeanId id)
    {
        this.beanId = id;
    }

    /**
     * Returns the cached BeanID associated with the wrappered byte array. <p>
     * 
     * Performance improvment. <p>
     * 
     * @param id BeanId associated with the wrappered byte array.
     **/
    public final BeanId getBeanId()
    {
        return beanId;
    }

    // --------------------------------------------------------------------------
    // Data ---------------------------------------------------------------------
    // --------------------------------------------------------------------------

    /**
     * Reference to a BeanId instance associated with this ByteArray.
     * This field is very useful for performance enhancements.
     **/
    private BeanId beanId = null;

    private static final String CLASS_NAME = "com.ibm.ejs.container.util.ByteArray";
}
