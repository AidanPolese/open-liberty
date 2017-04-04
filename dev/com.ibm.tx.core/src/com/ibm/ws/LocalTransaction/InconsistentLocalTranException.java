package com.ibm.ws.LocalTransaction;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60                                     */
/* (C) COPYRIGHT International Business Machines Corp. 2002, 2005             */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  21/02/03  gareth     LIDB1673.19  Make any unextended code final          */
/*  10-02-05  hursdlg    LIDB3706-5   Serialization                           */
/* ************************************************************************** */

/**
 * 
 * <p> This class is private to WAS.
 * Any use of this class outside the WAS Express/ND codebase 
 * is not supported.
 *
 */

/**
 * Thrown when a LocalTransactionCoordinator detects a failure completing
 * one of its resources. The exception indicates that the outcomes of the
 * RMLTs in the LTC scope are mixed. The exception contains a vector
 * of names of the failed resources.
 *
 */
public final class InconsistentLocalTranException extends Exception
{

    private static final long serialVersionUID = -8482185434516436562L;

    private String[] ivResources;


    /**
     * Constructor.
     * 
     * @param message  The exception description
     * @param resource The failing resources
     */
    public InconsistentLocalTranException(String message, String[] resources)
    {

        super(message);

        ivResources = resources;
    }

    /**
     * Returns the identifier of the failing resource which caused 
     * the exception.
     * 
     * @return The failing resources identifier
     */
    public String[] getFailingResources()
    {

        return ivResources;
    }
}
