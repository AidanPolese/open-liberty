/* rexx */
/**********************************************************************/
/* 02/11/2009 JTM                                                     */
/* This exec was taken from the LE 'cics' rexx exec that supports     */
/* doing CICS translates on assembler and PLX parts. The was added    */
/* by J Mulvey for the OLA feature -LI4798I7-08. It was originally    */
/* written by Mark Wallen while on the LE team.                       */
/*   syntax:  cics <file>.o                                           */
/* Error messages will be directed to STDERR.                         */
/* -a save asm-plx370 source as name.s - name.plx370                  */
/**********************************************************************/
type='mac'
cc=0
lca='a'
lcc='c'
lcv='v'
lco='o'
lcm='m'
lcw='w'
ucv='V'
opts=''
argx=getopts('acvmV','ow')
if argx=0 then exit 1
if __argv.0<argx | __argv.0>argx then
   do
   call sayit 'syntax: cics [-cmvV] [-o file] file'
   exit 1
   end

pgm=__argv.argx

call sayit 'Processing CICS Translate for program: 'pgm

mytype=SUBSTR(pgm,POS('/',pgm)+1,LENGTH(pgm))
mytype=SUBSTR(mytype,POS('.',mytype)+1,LENGTH(mytype))

if mytype = 'cicsasm' | mytype = 'cics42asm' | mytype = 'cics51asm' | mytype = 'cics52asm'  | mytype = 'cics53asm' then
   do
   call sayit 'Processing .cicsasm part ...'
   cics='DFHEAP1$'
   p1parm='CICS,NOPROLOG,NOEPILOG'
   osrctype='s'
   end
else if mytype = 'cicsc' then
   do
   cics='DFHEDP1$'
   p1parm=''
   osrctype='c'
   end
else
   do
   cics='DFHEPP1$'
   /*p1parm='CICS,PLS,PLSA0,SP'*/
   p1parm='CICS,PLS,SYSEIB,SP'
   osrctype='plx'
   end
say 'osrctype='osrctype
if opt.lco<>'' then
   obj=opt.lco
 else
   do
   i=lastpos('.',pgm)
   if i=0 then i=length(pgm)+1
   obj=substr(pgm,1,i-1)'.o'
   end
if substr(obj,length(obj)-1)='.o' then
   do
   srcstr=lastpos('/',obj)
   srcns=substr(obj,srcstr+1,length(obj)-srcstr)
   asmnm=substr(srcns,1,length(srcns)-2)'.'osrctype
   end
 else
   asmnm=obj'.'osrctype
if opt.lca<>'' then
   plparm='TERM,NOOBJECT'

syslib=''
csyslib=''
address syscall 'getlogin prefix'
do i=1 to __environment.0
   if substr(__environment.i,1,10)='ASMSYSLIB=' then do
      syslib=substr(__environment.i,11)
   end
   else do
     if substr(__environment.i,1,10)='CICSSYSLIB=' then do
       csyslib=substr(__environment.i,11)
     end
     else
       if substr(__environment.i,1,10)='TSOPREFIX=' then do
        prefix=substr(__environment.i,11)
       end
   end
end
if syslib='' & csyslib='' then
   do
   syslib='SYS1.MACLIB'
   csyslib=syslib
   end
else
if syslib='' then
   syslib=csyslib
else
if csyslib='' then
   csyslib=syslib

i=0
i=i+bpxwdyn("alloc fi(sysin)    new unit(sysda) cyl space(4,2) msg(2)")
i=i+bpxwdyn("alloc fi(sysprint) new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(systerm)  new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(sysut1)   new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(sysut2)   new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(syspunch) new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(sysut3)   new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(sysut4)   new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(asmlin)   new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(asmut1)   new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(asmterm)  new unit(sysda) cyl space(2,2) msg(2)")
i=i+bpxwdyn("alloc fi(asmprint) new unit(sysda) cyl space(2,2) msg(2)")
if i>0 then
   do
   call sayit i 'allocations failed for temporary data sets'
   exit 1
   end
lns.0=0
address syscall 'readfile' pgm 'lns.'
if lns.0=0 then
   do
   call sayit 'Unable to read file or file is empty:' pgm
   exit 1
   end
do l=1 to lns.0
 /* call sayit 'line: 'l lns.l */
   if length(lns.l)=0 then
      lns.l=' '
end
address mvs 'execio * diskw sysin (fini stem lns.'
if rc>1 then
   do
   call sayit 'execio error creating PLX source file rc: 'rc
   exit 1
   end

say 'liballoc for' syslib
if opt.lcv<>'' then call sayit 'CICS macro libraries:'
call liballoc csyslib,'syslib'

if opt.lcv<>'' then call sayit 'ASM macro libraries:'
call liballoc syslib,'asmlib'

if opt.lcm<>'' then
   plparm=plparm',MS'
if opt.lcw<>'' then
   plparm=opt.lcw','plparm
call sayit 'CICS Compile options:' p1parm
call sayit 'Invoking CICS compiler: 'cics
address linkmvs cics 'p1parm'
rtn=rc
if rtn>0 then
   do
   address mvs 'execio * diskr sysprint (fini stem err.'
   do i=1 to err.0
      call sayit strip(substr(err.i,2),'T')
   end
   address mvs 'execio * diskr systerm (fini stem err.'
   do i=1 to err.0
      call sayit strip(substr(err.i,2),'T')
   end
   address mvs 'execio * diskr sysprint (fini stem err.'
   do i=1 to err.0
      call sayit strip(substr(err.i,2),'T')
   end
   address mvs 'execio * diskr asmterm (fini stem err.'
   do i=1 to err.0
      call sayit strip(substr(err.i,2),'T')
   end
   call sayit 'CICS return code='rtn
   if rtn>4 then
      cc=1
   end
else
   if opt.lcv<>'' then call sayit 'CICS return code='rtn

if opt.lcc='' & opt.lca='' then
   do
   if opt.lcv<>'' then call sayit 'writing object file:' obj
   address mvs 'execio * diskr asmlin (fini stem obj.'
   address syscall 'creat' obj 666
   fd=retval
   if retval=-1 then
      do
      call sayit 'unable to create obj file:' errno errnojr
      cc=1
      end
    else
      do i=1 to obj.0
         address syscall 'write' fd 'obj.i'
         if retval<>length(obj.i) then
            do
            call sayit 'error writing object file'
            cc=1
            leave
            end
      end
   end

if opt.lca<>'' then
   do
   if opt.lcv<>'' then call sayit 'writing asm file:' asmnm
   obj.0=0
   address mvs 'execio * diskr syspunch (fini stem obj.'
   address syscall 'creat' asmnm 666
   fd=retval
   if retval=-1 then
      do
      call sayit 'unable to create asm file:' errno errnojr
      cc=1
      end
    else do
      objall = ''
      do i=1 to obj.0
         objall  = objall || strip(obj.i,'T') || esc_n
      end i
         objalllen = LENGTH(objall)
         address syscall 'write' fd 'objall' objalllen
         if retval<>objalllen then
            do
            call sayit 'error writing asm file'
            cc=1
    /*      leave */
            end
    end
   end

if opt.ucv<>'' then
   do
   if opt.lcv<>'' then call sayit 'writing listing file'
   address mvs 'execio * diskr sysprint (fini stem err.'
   do i=1 to err.0
      say strip(err.i,'T')
   end
   address mvs 'execio * diskr asmprint (fini stem err.'
   do i=1 to err.0
      say strip(err.i,'T')
   end
   end

return cc

sayit:
   parse arg saytext.1
   address mvs 'execio 1 diskw 2 (stem saytext.'
   return

liballoc:
   parse arg lib,dd
   tdd=strip(substr(dd,1,4))
   do i=1 by 1 until lib=''
      parse var lib lib.i ':' lib
      if lib.i='' then i=i-1
   end
   lib.0=i
   if pos('/',lib.1)>0 then
      call pathlib lib.1,dd
    else
      call bpxwdyn 'alloc fi('dd') da('strip(lib.1)') shr msg(2)'
   if opt.lcv<>'' then call sayit lib.1
   ddlist=dd
   if lib.0>1 then
      do
      do i=2 to lib.0
         lib.i=strip(lib.i)
         if opt.lcv<>'' then call sayit lib.i
         if pos('/',lib.i)>0 then
            call pathlib lib.i,tdd||i
          else
            call bpxwdyn 'alloc fi('tdd||i') da('lib.i') shr msg(2)'
         ddlist=ddlist || ','tdd||i
      end
      if bpxwdyn('concat ddlist('ddlist') msg(2)')<>0 then
         do
         call sayit 'SYSLIB concatenation failed'
         exit 1
         end
      end
   return

pathlib:
   parse arg path,pathdd
   dsn=prefix'.PLX'time('S')'.'pathdd
   j=0
   j=j+bpxwdyn('alloc fi('pathdd') msg(2)',
        'recfm(f,b) lrecl(80) blksize(3280)',
        'mod catalog da('dsn') space(5,5) tracks dir(30)')
   call bpxwdyn('free fi('pathdd') msg(2)')
   j=j+bpxwdyn('alloc fi('pathdd') da('dsn') shr delete msg(2)')
   address syscall
   'readdir (path) dir.'
   if rc<0 | retval=-1 then
      do
      call sayit 'Unable to read directory' path
      call sayit 'Errno='errno 'Reason='errnojr
      exit 1
      end
   do p=1 to dir.0
      if length(dir.p)<length(type)+1 then iterate
      if substr(dir.p,length(dir.p)-length(type))<>'.'type then iterate
      parse upper var dir.p mem '.'
      if opt.lcv<>'' then call sayit 'extracting' mem
      fl.0=0
      fn=path'/'dir.p
      'readfile (fn) fl.'
      if fl.0=0 then
         do
         call sayit 'Error reading file or file empty:' fn
         exit 1
         end
      j=j+bpxwdyn('alloc fi(memdd) da('dsn'('mem')) shr msg(2)')
      do l=1 to fl.0
         if length(fl.l)=0 then
            fl.l=' '
      end
      address mvs 'execio' fl.0 'diskw memdd (stem fl. fini'
      if rc<>0 then
         do
         call sayit 'Error getting macro' fn
         exit 1
         end
      call bpxwdyn 'free fi(memdd) msg(2)'
   end
   if j>0 then
      do
      call sayit j 'allocation errors'
      exit 1
      end
   return

/**********************************************************************/
/*  Function: GETOPTS                                                 */
/*     This function parses __ARGV. stem for options in the format    */
/*     used by most POSIX commands.  This supports simple option      */
/*     letters and option letters followed by a single parameter.     */
/*     The stem OPT. is setup with the parsed information.  The       */
/*     options letter in appropriate case is used to access the       */
/*     variable:  op='a'; if opt.op=1 then say 'option a found'       */
/*     or, if it has a parameter:                                     */
/*        op='a'; if opt.op<>'' then say 'option a has value' opt.op  */
/*                                                                    */
/*  Parameters: option letters taking no parms                        */
/*              option letters taking 1 parm                          */
/*                                                                    */
/*  Returns: index to the first element of __ARGV. that is not an     */
/*           option.  This is usually the first of a list of files.   */
/*           A value of 0 means there was an error in the options and */
/*           a message was issued.                                    */
/*                                                                    */
/*  Usage:  This function must be included in the source for the exec */
/*                                                                    */
/*  Example:  the following code segment will call GETOPTS to parse   */
/*            the arguments for options a, b, c, and d.  Options a    */
/*            and b are simple letter options and c and d each take   */
/*            one argument.  It will then display what options were   */
/*            specified and their values.  If a list of files is      */
/*            specified after the options, they will be listed.       */
/*                                                                    */
/*    parse value 'a   b   c   d' with,                               */
/*                 lca lcb lcc lcd .                                  */
/*    argx=getopts('ab','cd')                                         */
/*    if argx=0 then exit 1                                           */
/*    if opt.0=0 then                                                 */
/*       say 'No options were specified'                              */
/*     else                                                           */
/*       do                                                           */
/*       if opt.lca<>'' then say 'Option a was specified'             */
/*       if opt.lcb<>'' then say 'Option b was specified'             */
/*       if opt.lcc<>'' then say 'Option c was specified as' opt.lcc  */
/*       if opt.lcd<>'' then say 'Option d was specified as' opt.lcd  */
/*       end                                                          */
/*    if __argv.0>=argx then                                          */
/*       say 'Files were specified:'                                  */
/*     else                                                           */
/*       say 'Files were not specified'                               */
/*    do i=argx to __argv.0                                           */
/*       say __argv.i                                                 */
/*    end                                                             */
/*                                                                    */
/**********************************************************************/
getopts: procedure expose opt. __argv.
   parse arg arg0,arg1
   argc=__argv.0
   opt.=''
   opt.0=0
   optn=0
   do i=2 to argc
      if substr(__argv.i,1,1)<>'-' then leave
      if __argv.i='-' then leave
         opt=substr(__argv.i,2)
      do j=1 to length(opt)
         op=substr(opt,j,1)
         if pos(op,arg0)>0 then
            do
            opt.op=1
            optn=optn+1
            end
         else
         if pos(op,arg1)>0 then
            do
            if substr(opt,j+1)<>'' then
               do
               opt.op=substr(opt,j+1)
               j=length(opt)
               end
             else
               do
               i=i+1
               if i>argc then
                  do
                  call sayit 'Option' op 'requires an argument'
                  return 0
                  end
               opt.op=__argv.i
               end
            optn=optn+1
            end
         else
            do
            call sayit 'Invalid option =' op
            call sayit 'Valid options are:' arg0 arg1
            return 0
            end
      end
   end
   opt.0=optn
   return i

