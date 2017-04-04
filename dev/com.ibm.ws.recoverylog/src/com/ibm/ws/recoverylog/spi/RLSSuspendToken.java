/* **************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                             */
/*                                                                              */
/* IBM Confidential OCO Source Material                                         */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2005            */
/* The source code for this program is not published or otherwise divested      */
/* of its trade secrets, irrespective of what has been deposited with the       */
/* U.S. Copyright Office.                                                       */
/*                                                                              */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                */
/*                                                                              */
/*  Change History:                                                             */
/*                                                                              */
/*  Date      Programmer Defect       Description                               */
/*  --------  ---------- ------       -----------                               */
/*  19-01-05  mdobbie    LI3603       Creation                                  */
/* **************************************************************************** */
package com.ibm.ws.recoverylog.spi;


/**
 * An instance of a RLSSuspendToken represents a unique token returned when the Recovery Log Service
 * is called to suspend.   The token must be passed in during the corresponding call to resume the Recovery Log Service
 */
public interface RLSSuspendToken
{
   /**
    * Returns a byte array representation of the RLSSuspendToken. 
    */
    public byte[] toBytes();

    /**
     * Returns a printable String that identifies the RLSSuspendToken. This is
     * used for debug and servicability purposes.
     * 
     * @return A printable String that identifies the RLSSuspendToken.
     *
     */
    public String print();
}
