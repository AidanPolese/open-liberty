/* REXX */
/**************************************************************
 *  REXX shell script to invoke the z/OS XLC EDCSECT tool
 *  to generate a C header file from an Assembler DSECT
 *  This is only used by the make process.
 **************************************************************/
/* Arg 1: the file to assemble (required)
       2: assembler options (opt)
       3: EDCDSECT options (opt)              */
parse arg asmsrc aparm dparm
if asmsrc == '' then
  do
  say 'Invalid syntax; argument asmsrc is required'
  exit 1
  end
aparm="NOOBJECT,NODECK,TERM,LIST(133),ADATA,GOFF,"aparm
dparm="EQUATE(DEF),LP64,PPCOND,INDENT(4)"dparm
call syscalls 'ON'
address syscall 'realpath . cwd'
address syscall 'realpath 'asmsrc' asmsrc'

call BPXWDYN("ALLOC DD(SYSIN) PATH('"asmsrc"') FILEDATA(TEXT) PATHOPTS(ORDONLY) MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSLIB) DA(SYS1.MACLIB) SHR MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSLIB2) DA(SYS1.MODGEN) SHR MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSLIB3) DA(SYS1.CEE.SCEEMAC) SHR MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSLIB4) PATH('"cwd"/macros') PATHOPTS(ORDONLY) MSG(2) REUSE")
call BPXWDYN("CONCAT DDLIST(SYSLIB,SYSLIB2,SYSLIB3,SYSLIB4) MSG(2)")
/* call BPXWDYN("ALLOC DD(SYSPRINT) PATH('"asmsrc".list') PATHOPTS(OWRONLY,OCREAT,OTRUNC) PATHMODE(SIRWXU,SIRGRP) FILEDATA(TEXT) MSG(2) REUSE") */
call BPXWDYN("ALLOC DD(SYSPRINT) DUMMY")
call BPXWDYN("ALLOC DD(SYSTERM)  PATH('/dev/fd2') PATHOPTS(OWRONLY) FILEDATA(TEXT) MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSADATA) DA(&&ADATA) NEW SPACE(2,1) CYL RECFM(V,B) LRECL(8144) BLKSIZE(0) MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSLIN) DUMMY")
call BPXWDYN("ALLOC DD(SYSPUNCH) DUMMY")
call BPXWDYN("ALLOC DD(SYSUT1) DA(&&SYSUT1) NEW SPACE(1,1) CYL MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSUT2) DA(&&SYSUT2) NEW SPACE(1,1) CYL MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSUT3) DA(&&SYSUT3) NEW SPACE(1,1) CYL MSG(2) REUSE")

address ATTCHMVS "ASMA90 aparm"
asmrc=RC

call BPXWDYN("free DD(SYSIN)    MSG(2)")
call BPXWDYN("free DD(SYSLIB)   MSG(2)")
call BPXWDYN("free DD(SYSPRINT) MSG(2)")
call BPXWDYN("free DD(SYSTERM)  MSG(2)")
call BPXWDYN("free DD(SYSLIN)   MSG(2)")
call BPXWDYN("free DD(SYSPUNCH) MSG(2)")
call BPXWDYN("free DD(SYSUT1) DELETE MSG(2)")
call BPXWDYN("free DD(SYSUT2) DELETE MSG(2)")
call BPXWDYN("free DD(SYSUT3) DELETE MSG(2)")

if asmrc > 4 then
  do
  call BPXWDYN("free DD(SYSADATA) DELETE MSG(2)")
  exit asmrc
  end

call BPXWDYN("ALLOC DD(SYSPRINT) PATH('/dev/fd2') PATHOPTS(OWRONLY) FILEDATA(TEXT) MSG(2) REUSE")
call BPXWDYN("ALLOC DD(SYSOUT) PATH('/dev/fd2') PATHOPTS(OWRONLY) FILEDATA(TEXT) MSG(2) REUSE")
call BPXWDYN("ALLOC DD(EDCDSECT) DA(&&EDCDSECT) NEW MSG(2) REUSE")
address ATTCHMVS "CCNEDSCT dparm"
edcrc=RC
/* Copy the EDCDSECT output to stdout */
address MVS "EXECIO * DISKR EDCDSECT (STEM line. FINIS"
address MVS "EXECIO" line.0 "DISKW STDOUT (STEM line."
call BPXWDYN("free dd(SYSPRINT) MSG(2)")
call BPXWDYN("free dd(SYSOUT MSG(2)")
call BPXWDYN("free dd(EDCDSECT) delete MSG(2)")
call BPXWDYN("free dd(SYSADATA) delete MSG(2)")

exit edcrc
