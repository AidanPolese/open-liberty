package com.ibm.ws.LocalTransaction;
/* ************************************************************************** */
/* COMPONENT_NAME: WEBSJAVA.EJS.TX                                            */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002,2003     */
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
/*  26/04/02  gareth        126930    Creation                                */
/*  08/09/03  sykesm        WS18044   Extend Synchronization                  */
/*  12/11/03  hursdlg       LIDB2775  Merge zOS and distributed code          */
/*  23-03-04  mdobbie     LIDB3133-23 Added SPI classification                */
/* ************************************************************************** */

/**
 * This interface is provided for the EJB container and
 * only the EJB container to use. Synchronizations enlisted
 * with the LTC and ActivitySesion by the EJB container
 * should implement this interface.
 *
 * <p> This interface is private to WAS.
 * Any use of this interface outside the WAS Express/ND codebase 
 * is not supported.
 *
 */
public interface ContainerSynchronization
    extends javax.transaction.Synchronization
{
    /**
     * Set whether the LTC is being driven mid-ActivitySession
     * or at ActivitySession completion.
     * 
     * @param isCompleting
     *               <UL>
     *               <LI>true - ActivitySession is completing.</LI>
     *               <LI>false - ActivitySession is executing a mid session checkpoint/reset.</LI>
     *               </UL>
     */
    public void setCompleting(boolean isCompleting);    
}
