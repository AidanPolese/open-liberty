//BBGZANGL PROC PARMS='',COLD=N,NAME=''
//*------------------------------------------------------------------
//  SET ROOT='/u/MSTONE1/wlp'
//*------------------------------------------------------------------
//* Start the Liberty angel process
//*------------------------------------------------------------------
//* This proc may be overwritten by fixpacks or iFixes.
//* You must copy to another location before customizing.
//*------------------------------------------------------------------
//STEP1   EXEC PGM=BPXBATA2,REGION=0M,TIME=NOLIMIT,
//      PARM='PGM &ROOT./lib/native/zos/s390x/bbgzangl COLD=&COLD NAME=X
//             &NAME &PARMS'
//STDOUT    DD SYSOUT=*
//STDERR    DD SYSOUT=*
//* ================================================================ */
//* PROPRIETARY-STATEMENT:                                           */
//* Licensed Material - Property of IBM                              */
//*                                                                  */
//* (C) Copyright IBM Corp. 2011, 2012                               */
//* All Rights Reserved                                              */
//* US Government Users Restricted Rights - Use, duplication or      */
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp.*/
//* ================================================================ */
