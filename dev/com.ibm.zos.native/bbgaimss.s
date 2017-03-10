*/*START OF SPECIFICATIONS ********************************************
*
* DESCRIPTIVE-NAME: WAS z/OS Adapters check for IMS Stop routine
*
* CSECT NAME: BBGAIMSS
*
* COMPONENT:  boss_adapter
*
* EYE-CATCHER: none
*
* PROPRIETARY STATEMENT=
* IBM Confidential
* OCO Source Materials
* 5655-I35 (C) Copyright IBM Corp. 2010
* The source code for this program is not published or otherwise
* divested of its trade secrets, irrespective of what has been
* deposited with the U.S. Copyright Office.
* Status = H28W700
*
*
* FUNCTION:  Checks to see if IMS is stopping
*
* METHOD-OF-ACCESS:
*
* SIZE:
*
* SERIALIZATION: None
*
* DEPENDENCIES: None
*
* EXTERNAL CLASSIFICATION: NONE
* END OF EXTERNAL CLASSIFICATION:
*
* CHANGE-ACTIVITY:
*
*   Flag Reason      Release  YYYYMMDD Origin  Description
*   $F003694-25507   H28W700  20100504 PDFG    OLA II IMS support.
*   $PM36598         H28W700  20110419 PDFG    SSOB is not reliable
*   $PI45852         HBBO850  20140715  PDLS   Use non-IMS path for 
*                                              Batch DLI
*
*END OF SPECIFICATIONS ***********************************************/
*
         TITLE 'BBGAIMSS - WAS Adapters check IMS Stop'
BBGAIMSS CSECT
BBGAIMSS AMODE 64
BBGAIMSS RMODE ANY
         J     Around
         DC    CL8'BBGAIMSS'            Module name
         DC    CL8' *OLA2* '            Stub marker - OLA2
         DC    CL10'&SYSDATE '          Asm date
         DC    CL6'&SYSTIME'            Asm time
*
Around   DS    0H
         STMG  R14,R12,8(R13)           Save all caller's Regs F4SA
         LGR   R12,R15
         USING BBGAIMSS,R12
         XGR   R15,R15                  Clear return code
* Make sure we were invoked in the right key
         IPK
         CLM   R2,B'0001',MYKey    PSW Key 2?
         BNZ   NotIMS              No.... invalid call
*
* Figure out if we're even in IMS
FindIMS  XGR   R4,R4
         USING PSA,R4
         LLGT  R4,PSAAOLD          Get address of current ASCB
         DROP  R4
         USING ASCB,R4
         L     R4,ASCBASXB         Get address of ASXB
         DROP  R4
         USING ASXB,R4
         L     R4,ASXBFTCB         Get address of first TCB
         SGR   R6,R6               Initialize flag register
*
         DROP  R4
         USING TCB,R4
LATTCB   DS    0H                  Look At This TCB
         XGR   R9,R9
         ICM   R9,15,TCBLLS        This TCB have LLEs?
         BZ    LANTCB              No, check next TCB
*
         USING LLE,R9
LATLLE   DS    0H                  Look At This LLE
         XGR   R8,R8
         ICM   R8,15,LLECDPT       CDEs exist?
         BZ    LANLLE              Strange, go look at next LLE
*
         USING CDENTRY,R8
         CLC   CDNAME(8),=C'DFSSRB  ' Check for IMS dependent
         BNE   LANPRPX             Branch if no match
         LLGT  R1,CDENTPT          R1-->DFSSRB
         XGR   R2,R2               Clear for validation
         IVSK  R2,R1               R2=SPK OF DFSSRB
         CLM   R2,B'0001',IMSKey   Key 7 storage?
         BE    IMSDEP              Branch if we found it
LANPRPX  DS    0H
         CLC   CDNAME(8),=C'DFSPRPX0' Check for IMS PARMBLK
         BNE   LANLLE              Branch if no match
         LA    R6,1                Flag the find
*
LANLLE   DS    0H                  Look At Next LLE
         ICM   R9,15,LLECHN        Another LLE exist?
         BNZ   LATLLE              Yes, check the CDE
*
LANTCB   DS    0H                  Look At Next TCB
         ICM   R4,15,TCBTCB        Another TCB exist?
         BNZ   LATTCB              Yes, go look at this TCB
         CL    R6,=A(1)            R6 say we found DFSPRPX0?
         BNE   NotIMS              BRANCH, not IMS
*
*   IMS Batch Region
*
IMSBATCH DS    0H
         B     NotIMS              @PI45852C
*
*   IMS Dependent region (BMP, MPP, IFP....)
*
IMSDEP   DS    0H
*
IMSEnv   DS    0H
         LG    R14,8(R13)          Restore R14 F4SA
         LMG   R0,R11,24(R13)      Restore R0-R11 F4SA
         LLGT  R4,16               A(CVT)
         LLGT  R4,0(,R4)           A(NEW/OLD)
         LLGT  R4,4(,R4)           A(CURRENT TCB)
         LLGT  R4,112(,R4)         A(1ST SAVE AREA)=TCBFSA
         LTGR  R4,R4               Is TCBFSA valid?
         BZ    Done                No
         SPKA  Key8
         LLGT  R4,24(,R4)          Reg15 has DFSECP address
         LTGR  R4,R4               Is DFSECP valid?
         BZ    NSTOPNG             No
         USING DFSECP,R4           Map DFSECP
*
         LLGT  R4,ECPDIRCL         Get DIRCA @PM36598C
         LTGR  R4,R4               Is DIRCA valid?
         BZ    NSTOPNG             No
         DROP  R4
         USING DIRCA,R4
         LLGT  R4,DIRPSTA          Get PST
         LTGR  R4,R4               Is PST valid?
         BZ    NSTOPNG             No
         DROP  R4
         USING PST,R4
         SPKA  Key7
         TM    PSTTERM,PSTFPA      Is this a fast path?
         BZ    NOTIFP              No, don't check IFP bits
         LLGT  R6,PSTEPST          Get EPST
         LTGR  R6,R6               Is EPST valid?
         BZ    NOTIFP              No
         USING EPST,R6
         TM    EPSTSTOP,EPSTSTST+EPSTSTAB Is stopping?
         BNZ   STOPNG              Yes, set return code
         DROP  R6
NOTIFP   LGHI  R7,EPFLGT
         SGR   R4,R7               Get EPF
         DROP  R4
         USING EPF,R4
         LLGT  R4,EPFPTR           Get SAP addr
         LTGR  R4,R4               Is SAP valid?
         BZ    NSTOPNG             No
         DROP  R4
         USING SAP,R4
         TM    SAPSFLG0,SAPSCNCL+SAPSSTOP+SAPSABDP Is stopping?
         BNZ   STOPNG              Yes, set return code
         TM    SAPSFLG1,SAPSABPR   Is stopping?
         BZ    NSTOPNG             No, too bad.
STOPNG   LA    R15,1(R15)          Indicate stop has been requested
NSTOPNG  SPKA  Key2
         DS    0H
         B     Done
*
NotIMS   LA    R15,4(R15)          Indicate not in IMS
         DS    0H
Done     LG    R14,8(R13)          Restore R14 F4SA
         LMG   R0,R12,24(R13)      Restore R0-R12 F4SA
         BR    R14
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
Key8     EQU   X'80'
Key7     EQU   X'70'
Key2     EQU   X'20'
IMSKey   DC    X'70'
MYKey    DC    X'20'
*
         IHAASCB
         IHAASXB
         IHALLE
         IHACDE
         CVT   DSECT=YES
         IHAPSA
         DFSECP IMS
         DFSRRT
         IRC   DIRCA=0
         IEPF  DSECT=YES
         ISAP
         IKJTCB
         IPST
         DBFEPST
         END   BBGAIMSS
