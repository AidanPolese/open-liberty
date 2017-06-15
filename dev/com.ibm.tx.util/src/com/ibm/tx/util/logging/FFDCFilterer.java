/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.tx.util.logging;

public interface FFDCFilterer
{
    void processException(Throwable e, String s1, String s2, Object o);

    void processException(Throwable e, String s1, String s2);
    
    void processException(Throwable th, String sourceId, String probeId, Object[] objectArray);
    
    void processException(Throwable th, String sourceId, String probeId, Object callerThis, Object[] objectArray);
}
