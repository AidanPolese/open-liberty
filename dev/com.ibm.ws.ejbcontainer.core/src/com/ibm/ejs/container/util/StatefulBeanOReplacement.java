/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.util;

import java.io.Serializable;

/**
 * This class is used to StatefulBeanO object when serializing a SFSB
 * to the passivation file. There is no need to hold any of the StatefulBeanO
 * data in the replacement object since a new StatefulBeanO is created when
 * the SFSB is activated.
 */
public class StatefulBeanOReplacement implements Serializable
{
    private static final long serialVersionUID = -7308408948738957218L;

    /**
     * Default CTOR.
     */
    public StatefulBeanOReplacement()
    {
        // Intentionally left empty since no data in this object.
    }

}
