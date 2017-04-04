package com.ibm.ws.sib.msgstore.persistence.objectManager;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *   251161        14/04/05   gareth    Add ObjectManager code to CMVC
 *   292187        21/07/05   gareth    Improve toString() for trace
 * ============================================================================
 */

import com.ibm.ws.objectManager.ManagedObject;

import com.ibm.ws.sib.msgstore.persistence.UniqueKeyGenerator;


/**
 * This object is the Java representation of a unique key generators
 * persistent data.
 */
public class UniqueKeyGeneratorManagedObject extends ManagedObject
{
    private static final long serialVersionUID = 6408654889610894998L;

    private String _generatorName;
    private long   _generatorKeyLimit;


    public UniqueKeyGeneratorManagedObject(UniqueKeyGenerator generator)
    {
        _generatorName     = generator.getName();
        _generatorKeyLimit = generator.getRange();
    }

    public String getGeneratorName()
    {
        return _generatorName;
    }


    public void setGeneratorName(String generatorName)
    {
        _generatorName = generatorName;
    }


    public long getGeneratorKeyLimit()
    {
        return _generatorKeyLimit;
    }


    public void setGeneratorKeyLimit(long generatorKeyLimit)
    {
        _generatorKeyLimit = generatorKeyLimit;
    }


    public void becomeCloneOf(ManagedObject other)
    {
        _generatorName     = ((UniqueKeyGeneratorManagedObject)other).getGeneratorName();
        _generatorKeyLimit = ((UniqueKeyGeneratorManagedObject)other).getGeneratorKeyLimit();
    }

    public String toString()
    {
        // Defect 292187
        // include the super implementation to ensure 
        // inclusion of the object id.
        StringBuffer buffer = new StringBuffer(super.toString());

        buffer.append("(UniqueKeyGenerator[ generatorName: ");
        buffer.append(_generatorName);
        buffer.append(", generatorKeyLimit: ");
        buffer.append(_generatorKeyLimit);
        buffer.append(" ])");

        return buffer.toString();
    }
}
