/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This is the base class of all the Local Interface related exceptions.
 * This is the counterpart to ContainerException of the remote interface.
 **/
public class ContainerLocalException extends ContainerEJBException //p118356
{
    private static final long serialVersionUID = -6645328035635864281L;

    /**
     * Create a new <code>ContainerLocalException</code> instance.
     */
    public ContainerLocalException()
    {
        super();
    }

    /**
     * Create a new <code>ContainerLocalException</code> instance.
     * 
     * @param s Exception message string.
     */
    public ContainerLocalException(String s)
    {
        super(s);
    }

    /**
     * Create a new <code>ContainerLocalException</code> instance.
     * 
     * @param ex Chained exception.
     */
    public ContainerLocalException(java.lang.Exception ex)
    {
        super(ex.toString(), ex);
    }

    /**
     * Create a new <code>ContainerLocalException</code> instance.
     * 
     * @param s Exception message string.
     * @param ex Chained exception.
     */
    public ContainerLocalException(String s, java.lang.Exception ex)
    {
        super(s, ex);
    }

    /**
     * Create a new <code>ContainerLocalException</code> instance.
     * 
     * @param ex Chained exception.
     */
    public ContainerLocalException(java.lang.Throwable ex) //p118356
    {
        super(ex); //150727
    }

    /**
     * Create a new <code>ContainerLocalException</code> instance.
     * 
     * @param s Exception message string.
     * @param ex Chained exception.
     */
    public ContainerLocalException(String s, java.lang.Throwable ex) //p118356
    {
        super(s, ex);
    }

} // ContainerLocalException
