/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.javaee.ddmetadata.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Target({ METHOD })
public @interface LibertyDurationType {
    TimeUnit timeUnit();
}
