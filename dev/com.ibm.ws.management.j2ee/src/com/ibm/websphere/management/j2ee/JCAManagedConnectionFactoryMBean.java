/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.management.j2ee;

/**
 * Identifies a JCA managed connection factory. For each JCA managed connection
 * factory available to a JCAResource, there must be one managed object that
 * implements the JCAManagedConnectionFactory model.
 */
public interface JCAManagedConnectionFactoryMBean extends J2EEManagedObjectMBean {

}
