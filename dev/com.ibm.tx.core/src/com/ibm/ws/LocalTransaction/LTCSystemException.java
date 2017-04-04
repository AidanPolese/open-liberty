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
/*  Date      Programmer    Defect      Description                           */
/*  --------  ----------    ------      -----------                           */
/*  28/01/02   gareth       118164       Initial Creation                     */
/*  19/02/02   gareth       118164       Change name to LTCSystemException    */
/*  10-02-05   hursdlg    LIDB3706-5  Serialization                           */
/* ************************************************************************** */

/**
 * 
 * <p> This class is private to WAS.
 * Any use of this class outside the WAS Express/ND codebase 
 * is not supported.
 *
 */
public class LTCSystemException extends java.lang.Exception
{

   private static final long serialVersionUID = 3096415451898873118L;

   private Exception exception = null;

   /**
    * Default constructor.
    * 
    */
   public LTCSystemException() 
   {
   }

   public LTCSystemException(String msg) 
   {
      super(msg);
   }

   public LTCSystemException(Exception ex) 
   {
      exception = ex;
   }

   public LTCSystemException(String msg, Exception ex) 
   {
      super(msg);
      exception = ex;
   }

   public Exception getNestedException()
   {
      return exception;
   }

}

