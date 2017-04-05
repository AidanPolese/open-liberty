/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

public interface ELFactoryWrapperForCDI {

    public void setExpressionFactory(ExpressionFactory arg0);

    public Object coerceToType(Object arg0, Class<?> arg1) throws ELException;

    public MethodExpression createMethodExpression(ELContext arg0, String arg1, Class<?> arg2, Class<?>[] arg3) throws ELException, NullPointerException;

    public ValueExpression createValueExpression(Object arg0, Class<?> arg1);

    public ValueExpression createValueExpression(ELContext arg0, String arg1, Class<?> arg2) throws NullPointerException, ELException;
}
