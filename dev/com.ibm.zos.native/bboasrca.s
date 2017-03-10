*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise
* divested of its trade secrets, irrespective of what has been
* deposited with the U.S. Copyright Office.
*-----------------------------------------------------------
* CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!!
*-----------------------------------------------------------
*  ANY CHANGES TO THIS PART need to have the Client Vector
*  Slot level in bboacall incremented. If this is not done,
*  the code in server_wola_services.mc that loads BBOACALL
*  will NOT reload this module if there is already a BBOACALL
*  at an older level loaded.
*  While testing changes to modules in BBOACALL, you can
*  set this value to 9999 and server_wola_services.mc will
*  always replace an old BBOACALL with this one.
*  We started with level 101 for WAS 9 where this code
*  is introduced.
*-----------------------------------------------------------
* CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!!
*-----------------------------------------------------------
*
         TITLE 'BBOASRCA - WAS Connectors API Receive Req Any-31 bit'
BBOASRCA CSECT
BBOASRCA AMODE ANY
BBOASRCA RMODE ANY
         J     Around
         DC    CL8'BBOASRCA'            Module name
         DC    CL8' *OLA3* '            Stub marker - OLA3
         DC    CL10'&SYSDATE '          Asm date
         DC    CL6'&SYSTIME'            Asm time
*
Around   DS    0H
* 31 bit mode save area starts at offset 12
         STM   R14,R12,12(R13)    Save all caller's Regs low half
         LR    R12,R15
         USING BBOASRCA,R12
*
* check for CICS, call DFHRMCAL if we're in CICS.
*
         COPY  BBOAISCS
*
* check for IMS, call DFSESPRO if we're in IMS.
*
         BBOAIMS SVC=RCA
*
* If we get control here, we're not in CICS or IMS.  We need to restore
* our registers and addressability.
*
         LM    R14,R12,12(R13)          Restore caller Regs
         LR    R12,R15
         USING BBOASRCA,R12
         LR    R3,R1          save R1 addr of parmlist passed from C
         USING BBOA1RCA_PLIST,R3
*
* Obtain dynamic area and set it up for a 64 bit metal C call.
* Save area must be 144 bytes (F4SA) and the last word must point to
* the next available byte (NAB) of storage.
*
         STORAGE OBTAIN,LENGTH=32768,BNDRY=PAGE
         USING DYNAREA,R1
         STMH  R14,R12,HHREGS Save high halves of registers
         XC    CLREGS,CLREGS  Clear storage so we can clear HH regs
         LMH   R14,R13,CLREGS Clear HH regs for switch to 64 bit mode
*
         MVC   F4SA+4(4),F4SAEYE Move F4SA eye catcher into F4SA
         STG   R13,128(R1)    Store backchain ptr into F4SA
         LR    R9,R1          Compute next available byte (NAB)
         AHI   R9,DYNA_LEN    Compute next available byte (NAB)
         STG   R9,136(R1)     Store NAB in F4SA
         LR    R13,R1         Move dynamic area into R13
         DROP  R1
         USING DYNAREA,R13
*
         LA    R1,PLIST
         USING WOLA_CLIENT_RECVANY_PARMS,R1
         LLGT  R9,BBOA1RCA_REGNAME load register name, clear high
         STG   R9,WOLA_CLIENT_RECVANY_REGNAME store as 1st parm
         LLGT  R9,BBOA1RCA_CONNHDL load connhandle address
         STG   R9,WOLA_CLIENT_RECVANY_HANDLE store as 2nd parm
         LLGT  R9,BBOA1RCA_SERVNAME load service name address
         STG   R9,WOLA_CLIENT_RECVANY_SERVNAME store as 3rd parm
         LLGT  R9,BBOA1RCA_SERVNAMEL load service name len address
         STG   R9,WOLA_CLIENT_RECVANY_SERVNAMEL store as 4th parm
         LLGT  R9,BBOA1RCA_REQDATAL load request data len address
         STG   R9,WOLA_CLIENT_RECVANY_DATAL store as 5th parm
         LLGT  R9,BBOA1RCA_WAITTIME load waittime address
         STG   R9,WOLA_CLIENT_RECVANY_WAITTIME store as 6th parm
         XC    WOLA_CLIENT_RECVANY_CICSPRM,WOLA_CLIENT_RECVANY_CICSPRM
         LLGT  R9,BBOA1RCA_RC   load return code address
         STG   R9,WOLA_CLIENT_RECVANY_RC store as 8th parm
         LLGT  R9,BBOA1RCA_RSN  load reason code address
         STG   R9,WOLA_CLIENT_RECVANY_RSN store as 9th parm
         DROP  R1
*
* Make call to 64 bit metal C function.
*
         SAM64                  switch to 64 bit mode,
         LLGT  R15,=V(CLNTRCA)  call metalC clientRcvReqAny routine
         BASR  R14,R15
         SAM31
*
* See if we need to call the tWAS WOLA stub instead.
*
         L     R9,BBOA1RCA_RC    Load address of BBOA1RCA RC
         LHI   R14,BBOAAPI_RC_ERROR8
         C     R14,0(R9)         Is it RC=8
         JNZ   UNWIND            Not RC=8, continue
         L     R9,BBOA1RCA_RSN   Load address of BBOA1RCA RSN
         LHI   R14,WOLA_RSN_INTERNAL_TRY_TWAS_STUB
         CH    R14,2(R9)         Is this a tWAS registration?
         JNZ   UNWIND            Nope, don't try tWAS version
*
* Go get the tWAS function vector.  It's referenced off the connection
* handle.  Use the master client stub table (MCST).  The MCST must
* exist because we needed it to get here.
*
         L     R7,X'10'                 Load CVT
         USING CVT,R7
         L     R7,CVTECVT               Load ECVT
         DROP  R7
         USING ECVT,R7
         L     R7,ECVTBCBA              Load master/glbl BGVT
         DROP  R7
         USING BGVT,R7
         L     R7,BBODBGVT_BBOAMCST_PTR Load client stub tbl
         DROP  R7
         USING BBOAMCST,R7
         ICM   R7,B'1111',AMCST_SLOTS Load first slot (tWAS)
         JNZ   GETTWAS
         L     R9,BBOA1RCA_RC    Load address of BBOA1RCA RC
         LHI   R14,BBOAAPI_RC_SEVERE12
         ST    R14,0(R9)         Store RC
         L     R9,BBOA1RCA_RSN   Load address of BBOA1RCA RSN
         LHI   R14,BBOAAPI_RSN_NO_AMCSS
         ST    R14,0(R9)         Store RSN
         J     UNWIND
GETTWAS  DS    0H
         USING BBOAMCSS,R7
         L     R7,AMCSS_VECTOR_PTR Load tWAS client vec table
         DROP  R7
         USING BBOAXVEC_TWAS,R7
         L     R15,AVECTWAS_RRQA@  Load get data function
         DROP  R7
         LR    R1,R3               Move parm list into R1
         BASR  R14,R15             Call tWAS get data function
         DROP  R3
*
* Unwind back to caller
*
UNWIND   LG    R9,128(R13)      Get previous save area
         LMH   R14,R12,HHREGS   Restore high halves of registers
         STORAGE RELEASE,LENGTH=32768,ADDR=(R13)
         DROP  R13
         LR    R13,R9
         LM    R14,R12,12(R13)  restore registers
         LHI   R15,0            always RC=0
         BR    R14              return to caller
*
F4SAEYE  DC    CL4'F4SA'
*
R0       EQU   0
R1       EQU   1
R2       EQU   2
R3       EQU   3
R4       EQU   4
R5       EQU   5
R6       EQU   6
R7       EQU   7
R8       EQU   8
R9       EQU   9
R10      EQU   10
R11      EQU   11
R12      EQU   12
R13      EQU   13
R14      EQU   14
R15      EQU   15
         LTORG
         DS    0F
BBOASRCA@ DC   A(BBOASRCA)         Address of this routine for BBOAICAL
IMSLIT   DC    CL4'BBOA'           IMS ESAF LIT for WOLA
IMSKey   DC    X'70'               IMS KEY 7
*
CLNTRCA  ALIAS C'ClientReceiveRequestAny'
           EXTRN CLNTRCA

DYNAREA  DSECT
F4SA     DS CL144   Save area for called routine (64 bit)
HHREGS   DS CL64    Area to store HH of our registers
CLREGS   DS CL64    Area to clear HH of our registers
PLIST    DS CL128   Area to build parameter list for called function

* Compute dynamic area length
DYNA_LEN EQU *-DYNAREA

*
* CVT/ECVT/BGVT etc
         CVT   DSECT=YES
         IHAECVT DSECT=YES
         BBODBGVT DSECT=YES
         BBOAMCST
         BBOAXPRM
		 IHAPSA
		 IHAASCB
         IHAASXB
         IHALLE
         IHACDE
         DFSECP IMS
         DFSRRT

         COPY BBOAXVEC

         END
