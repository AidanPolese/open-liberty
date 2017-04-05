package com.ibm.tx.jta.impl;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36. (C) COPYRIGHT International Business Machines Corp. 2002, 2009   */
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
/*  Date      Programmer    Defect   Description                              */
/*  --------  ----------    ------   -----------                              */
/*  27/11/03  johawkes      178502    Start an RA during XA recovery          */
/*  05/12/03  johawkes      184903    Refactor PartnerLogTable                */
/*  13/04/04  beavenj       LIDB1578.1 Initial supprort for ha-recovery       */     
/*  06/06/07  johawkes      443467    Moved                                   */   
/*  02/06/09  mallam        596067    package move                            */  
/* ************************************************************************** */

import java.io.Serializable;

public interface RecoveryWrapper extends Serializable
{
    public boolean isSameAs(RecoveryWrapper rw);
    
    public byte[] serialize();
    
    public PartnerLogData container(FailureScopeController failureScopeController);
}
