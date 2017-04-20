/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.tx.jta.cdi.interceptors.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@Stereotype
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Transactional(value = TxType.REQUIRED, dontRollbackOn = NoSuchMethodException.class)
@RequestScoped
public @interface TransactionalStereotypeSimple3 {

}
