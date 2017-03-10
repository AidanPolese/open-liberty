//BBGZSRV PROC PARMS='defaultServer'
//*------------------------------------------------------------------
//* This proc may be overwritten by fixpacks or iFixes.
//* You must copy to another location before customizing.
//*------------------------------------------------------------------
//* INSTDIR - the path to the WebSphere Liberty Profile install.
//*           This path is used to find the product code and is
//*           equivalent to the WLP_INSTALL_DIR environment variable
//*           in the Unix shell.
//* USERDIR - the path to the WebSphere Liberty Profile user area.
//*           This path is used to store shared and server specific
//*           configuration information and is equivalent to the
//*           WLP_USER_DIR environment variable in the Unix shell.
//*------------------------------------------------------------------
//  SET INSTDIR='/u/MSTONE1/wlp'
//  SET USERDIR='/u/MSTONE1/wlp/usr'
//*------------------------------------------------------------------
//* Start the Liberty server
//*
//* WLPUDIR - PATH DD that points to the Liberty Profile's "user"
//*           directory. If the DD is not allocated, the user
//*           directory location defaults to the wlp/usr directory
//*           in the install tree.
//* STDOUT  - Destination for stdout (System.out)
//* STDERR  - Destination for stderr (System.err)
//* MSGLOG  - Destination for messages.log (optional)
//* STDENV  - Initial Unix environment - read by the system.  The
//*           installation default and server specific server
//*           environment files will be merged into this environment
//*           before the JVM is launched.
//*------------------------------------------------------------------
//STEP1   EXEC PGM=BPXBATSL,REGION=0M,TIME=NOLIMIT,
//  PARM='PGM &INSTDIR./lib/native/zos/s390x/bbgzsrv &PARMS'
//WLPUDIR  DD PATH='&USERDIR.'
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//*MSGLOG   DD SYSOUT=*
//*STDENV   DD PATH='/etc/system.env',PATHOPTS=(ORDONLY)
//*STDOUT   DD PATH='&ROOT/std.out',
//*            PATHOPTS=(OWRONLY,OCREAT,OTRUNC),
//*            PATHMODE=SIRWXU
//*STDERR   DD PATH='&ROOT/std.err',
//*            PATHOPTS=(OWRONLY,OCREAT,OTRUNC),
//*            PATHMODE=SIRWXU
//* ================================================================ */
//* PROPRIETARY-STATEMENT:                                           */
//* Licensed Material - Property of IBM                              */
//*                                                                  */
//* (C) Copyright IBM Corp. 2011, 2012                               */
//* All Rights Reserved                                              */
//* US Government Users Restricted Rights - Use, duplication or      */
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp.*/
//* ================================================================ */