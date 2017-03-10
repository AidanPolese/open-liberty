#-----------------------------------------------------------------------
# Build related locations
#-----------------------------------------------------------------------
ANGEL_TEST1_DIR := $(CURDIR)/test1
BLD_TOOLS_DIR := $(BLD_ROOT_DIR)/bldtools
BLD_CONFIG_DIR := $(BLD_TOOLS_DIR)/config
DEPDIR := $(CURDIR)/.deps
DISTDIR := $(BLD_ROOT_DIR)/dist
DSECT_SRC_DIR = $(BLD_ROOT_DIR)/dsects
DSECT_HDR_DIR = $(BLD_ROOT_DIR)/include/gen
IMAGE_STAGING_DIR := $(BLD_ROOT_DIR)/image-staging-dir
JAVA_HOME = /usr/lpp/java/J6.0.1_64
NLSPROPS_SRC_DIR = $(BLD_ROOT_DIR)/resources
NLSPROPS_HDR_DIR = $(BLD_ROOT_DIR)/include/gen
PAXFILE := wlp.pax
IMPAXFILE := wlp-zos-core.pax
KERNELIMPAXFILE := wlp-zos-kernel.pax

#-----------------------------------------------------------------------
# Redirect of stderr and stdout to the bit bucket
#-----------------------------------------------------------------------
ifndef verbose
#STDERR_QUIET_REDIRECT = 2>/dev/null
#STDOUT_QUIET_REDIRECT = 1>/dev/null
endif

#-----------------------------------------------------------------------
# Dataset locations for other products CICS/IMS
# TODO: Need to have CICS version-specific variables
# Note: 27 February 2014 These are tweaked for Hursley builds in
#       build-antz-zos-liberty.xml
#-----------------------------------------------------------------------
CICS_SDFHMAC_DS = BOSS.ZWAS800.Z12PLUS.CICS41.SDFHMAC
CICS42_SDFHMAC_DS = BOSS.ZWAS800.Z12PLUS.CICS42.SDFHMAC
CICS51_SDFHMAC_DS = BOSS.ZWAS800.Z12PLUS.CICS51.SDFHMAC
CICS52_SDFHMAC_DS = BOSS.ZWAS800.Z12PLUS.CICS52.SDFHMAC
CICS53_SDFHMAC_DS = BOSS.ZWAS800.Z12PLUS.CICS53.SDFHMAC

CICS_SDFHLOAD_DS = BOSS.ZWAS800.Z12PLUS.CICS41.SDFHLOAD

IMS910_SDFSMAC_DS = BOSS.ZWAS800.Z12PLUS.IMS910.SDFSMAC
IMS910_ADFSLOAD_DS = BOSS.ZWAS800.Z12PLUS.IMS910.ADFSLOAD
IMSV12_SDFSMAC_DS = BOSS.LIBERTY.IMSV12.SDFSMAC
IMSV12_ADFSLOAD_DS = BOSS.LIBERTY.IMSV12.ADFSLOAD

#-----------------------------------------------------------------------
# Other compiler-related datasets.
#-----------------------------------------------------------------------
CSSLIB_DS = SYS1.CSSLIB
SCCNCMP_DS = BOSS.WAS.ZOSV1R13.SCCNCMP

#-----------------------------------------------------------------------
# Common C compiler options
# Note: 01 October 2013 These are tweaked for Hursley builds in
#       build-antz-zos-liberty.xml
#-----------------------------------------------------------------------
CC = /u/mvsbuild/zos113/usr/lpp/cbclib/xlc/bin/.orig/xlc
CC_STEPLIB_FLAGS = -qsteplib=$(SCCNCMP_DS)
CC_ARCH_TUNE = -qarch=5 -qtune=9

#-----------------------------------------------------------------------
# z/OS compatibility levels
#-----------------------------------------------------------------------
OS_TARGET = -qtarget=zosv1r11
LD_COMPAT = COMPAT=ZOSV1R11

#-----------------------------------------------------------------------
# Enable compiler listings
#-----------------------------------------------------------------------
ifdef listout
CC_LISTING = -qlist=$(*).clst
LINK_LISTING = -qlist=$(@).llst -qreport -qinlrpt -qmap
endif

#-----------------------------------------------------------------------
# Handle linking metal C runtime library statically.
#-----------------------------------------------------------------------
ifdef metal_static
METAL_STATIC_FLAGS = -D__METAL_STATIC
METAL_STATIC_OBJS = //\'BOSS.KACZYNS.MCRTL.STATIC.D120314.OBJ\(CCR6CINT\)\' //\'BOSS.KACZYNS.MCRTL.STATIC.D120314.OBJ\(CCR6STGS\)\'
METAL_STATIC_LIBS = -S //\'SYS1.SCCR6BND\' 
endif

#-----------------------------------------------------------------------
# verbose=1 adds a lot of extra compiler info and warning messages
# -qinfo:als gives ansi aliasing info
#-----------------------------------------------------------------------
#CC_INFO_FLAGS = -qinfo=pro -qseverity=E=CCN3304
ifdef verbose
CC_INFO_FLAGS += -qinfo=all \
	-qsuppress=CCN3469 \
	-qsuppress=CCN3457 \
	-qphaseid \
	-qwarn64
endif

COMMON_CFLAGS = -qlongname -qroconst -qro -qlanglvl=extended \
	$(CC_INFO_FLAGS) \
	$(CC_ARCH_TUNE) \
	$(OS_TARGET) \
	$(CC_STEPLIB_FLAGS) \
	$(CC_LISTING) \
	$(CC_SUBMAKE_FLAGS) \
	-DBUILD_DATE_STAMP=\"$(BUILD_DATE_STAMP)\" \
	-DBUILD_TIME_STAMP=\"$(BUILD_TIME_STAMP)\" \
	-DBUILD_LEVEL=\"$(BUILD_LABEL)\" \
	-DLIBERTY_BUILD_LABEL=\"$(LIBERTY_BUILD_LABEL)\" \
	-qservice='$(BUILD_LEVEL)' \
	-F $(BLD_CONFIG_DIR)/xlc$(suffix $<).cfg \
	-qmakedep -MF $(df)

#-----------------------------------------------------------------------
# Metal C Compiler
# -I$(BLD_ROOT_DIR)/include/metal is temporary for metal C APAR 
#-----------------------------------------------------------------------
METAL_OPT_FLAGS = -O3
METAL_CFLAGS = -S -qreserved_reg=r12 -qmetal \
	-qnosearch -I$(BLD_ROOT_DIR)/include/metal \
	-I/u/mvsbuild/zos113/usr/include/metal -I//"'SYS1.SIEAHDR.+'" \
	-D__METAL_CSYSENV_VERSION=2 \
	-qinfo=pro -qseverity=E=CCN3304 \
	$(METAL_STATIC_FLAGS) \
	$(METAL_OPT_FLAGS) \
	$(ANGEL_GENERATE_VER) \
	$(COMMON_CFLAGS)

#-----------------------------------------------------------------------
# LE Enabled C Compiler
#-----------------------------------------------------------------------
ifdef ipa
C_IPA_LINK_FLAGS = -qipa=level=2:map:list -O3
C_OPT_FLAGS = -qipa=noobj:list -O3
else
C_OPT_FLAGS = -O3
endif
CFLAGS = -c -qfloat=ieee -qxplink -qdll \
	$(C_OPT_FLAGS) \
  -qnosearch -I/u/mvsbuild/zos113/usr/include \
	-I$(JAVA_HOME)/include -I//"'SYS1.SIEAHDR.+'" \
	-D_XOPEN_SOURCE_EXTENDED=1 \
	-D_XOPEN_SOURCE=500 \
	-D_OPEN_SYS \
	-D_OPEN_SYS_FILE_EXT=1 \
	-D_OPEN_THREADS \
	-D_POSIX_SOURCE \
	-D_ISOC99_SOURCE \
	-D_UNIX03_SOURCE \
	-D_VARARG_EXT_ \
	$(COMMON_CFLAGS)

#-----------------------------------------------------------------------
# Common C++ Compilation / Linkage options for WOLA
#-----------------------------------------------------------------------

#C++ Compiler
CPPC = /u/mvsbuild/zos113/usr/lpp/cbclib/xlc/bin/.orig/xlC

#STEPLIB Concatenation borrowed from tWAS, /u/boss/WAS855.ZNATV.daily/gm1318.01/CB390make.env.override
CPPC_STEPLIB_FLAGS = -qsteplib=$(SCCNCMP_DS):BOSS.ZWAS800.Z12PLUS.CCNV18.SCEERUN2:$(CICS_SDFHLOAD_DS):$(IMSV12_ADFSLOAD_DS)


#Switches for compilation debugging
ifdef verbose
CPPC_INFO_FLAGS += -qinfo=all \
	-qphaseid \
	-qwarn64
endif

COMMON_CPPFLAGS = -qlongname \
	-qcics=SP \
	-qroconst \
	-qro \
	-qlanglvl=extended \
	$(CPPC_INFO_FLAGS) \
	$(CC_ARCH_TUNE) \
	$(OS_TARGET) \
	$(CPPC_STEPLIB_FLAGS) \
	$(CC_LISTING) \
	$(CC_SUBMAKE_FLAGS) \
	-DBUILD_DATE_STAMP=\"$(BUILD_DATE_STAMP)\" \
	-DBUILD_TIME_STAMP=\"$(BUILD_TIME_STAMP)\" \
	-DBUILD_LEVEL=\"$(BUILD_LABEL)\" \
    -DLIBERTY_BUILD_LABEL=\"$(LIBERTY_BUILD_LABEL)\" \
    -qservice='$(BUILD_LEVEL)' \
	-F $(BLD_CONFIG_DIR)/xlc$(suffix $<).cfg \
	-qmakedep \
	-MF $(df)
CPPFLAGS = -c -I$(JAVA_HOME)/include \
	-I//"'SYS1.SIEAHDR.+'" \
	-I//"'SYS1.SAMPLIB'" \
	-I$(BLD_ROOT_DIR)/include \
	$(COMMON_CPPFLAGS) \
	$(C_OPT_FLAGS) \
	-qfloat=ieee \
	-qdll \
	-D_XOPEN_SOURCE_EXTENDED=1 \
	-D_XOPEN_SOURCE=500 \
	-D_OPEN_SYS \
	-D_OPEN_SYS_FILE_EXT=1 \
	-D_OPEN_THREADS \
	-D_ISOC99_SOURCE \
	-D_UNIX03_SOURCE \
	-D_VARARG_EXT_
CPPFLAGS_STRICT = -c -I$(JAVA_HOME)/include \
	-I//"'SYS1.SIEAHDR.+'" \
	-I//"'SYS1.SAMPLIB'" \
	-I$(BLD_ROOT_DIR)/include \
	$(COMMON_CPPFLAGS) \
	-qfloat=ieee \
	-qdll \
	-D_XOPEN_SOURCE_EXTENDED=1 \
	-D_XOPEN_SOURCE=500 \
	-D_OPEN_SYS \
	-D_OPEN_SYS_FILE_EXT=1 \
	-D_OPEN_THREADS \
	-D_ISOC99_SOURCE \
	-D_UNIX03_SOURCE \
	-D_VARARG_EXT_

LDCPPOPTS = -bcase=mixed \
	-brent \
	-S //\'BOSS.ZWAS800.Z12PLUS.LEV18.SCEELKEX\' \
	-S //\'BOSS.ZWAS800.Z12PLUS.LEV18.SCEELKED\' \
	-S //\'BOSS.ZWAS800.Z12PLUS.LEV18.SCEECPP\' \
	-S //\'$(CSSLIB_DS)\' \
	-S //\'BOSS.ZWAS800.Z12PLUS.LEV18.SCEEOBJ\' \
	-S //\'$(CICS_SDFHLOAD_DS)\'
CPP_OBJS = //\'BOSS.ZWAS800.Z12PLUS.LEV18.SCEELIB\(CELHS003\)\' 

#-----------------------------------------------------------------------
# Assembler
#  (for listing: -aegmrsx=test.alst )
#  -mnousing specified to stop ASMA303W from coming out
#  -mgoff is required for 64bit and -mgoff=adata required for debug
#  -mmachine specified to generate/support instructions specific to 
#   z/Architecture systems
#-----------------------------------------------------------------------
AS = as
AS_OPTS = -aegmrsx=$*.alst -mmachine=ZS -mnousing -mgoff -I$(BLD_ROOT_DIR)/macros -I$(BLD_ROOT_DIR)/macros/zos2.1

#-----------------------------------------------------------------------
# Linker
# -brent is specified to let BPX1LDX load the load mod into key 0 storage
#-----------------------------------------------------------------------
LD = ld
LDOPTS = -bcase=mixed -brent -S //\'$(CSSLIB_DS)\' -S //\'SYS1.CBC.SCCNOBJ\' $(METAL_STATIC_LIBS)

#-----------------------------------------------------------------------
# Rule for building a pre-processed metal C source file from a metal C
# source file.  This is intended as a debugging aid when used to build
# a specific .i part.
#-----------------------------------------------------------------------
%.i: $(MC_PATH_PREFIX)%.mc | dsects nlsprops
	$(CC) $(METAL_CFLAGS) -q64 -P $<

#-----------------------------------------------------------------------
# Rule for building an assembler file from a metal c file.
#-----------------------------------------------------------------------
%.o: $(MC_PATH_PREFIX)%.mc 
	$(CC) $(METAL_CFLAGS) -q64 $<
	$(AS) $(AS_OPTS) $(*).s $(STDERR_QUIET_REDIRECT)
	sed -e 's/^$(*).s\:/$*.o\:/' < $(df) > $(df).d && rm -f $(df)

#-----------------------------------------------------------------------
# Rule for building an object file from a metal c file in the test1/ dir
#-----------------------------------------------------------------------
test1/%.o: $(MC_PATH_PREFIX)test1/%.mc 
	$(CC) $(METAL_CFLAGS) -q64 $< -o test1/$(*).s
	$(AS) $(AS_OPTS) -o test1/$(*).o test1/$(*).s $(STDERR_QUIET_REDIRECT) 
	sed -e 's/^test1\/$(*).s\:/test1\/$*.o\:/' < $(df) > $(df).d && rm -f $(df)

#-----------------------------------------------------------------------
# Rule for building an object file from assembler source.
#-----------------------------------------------------------------------
%.o: $(ASM_PATH_PREFIX)%.s
	$(AS) $(AS_OPTS) -I $(CICS_SDFHMAC_DS) -I $(IMSV12_SDFSMAC_DS) $< $(STDERR_QUIET_REDIRECT)

#-----------------------------------------------------------------------
# Rule for building an object file from CICS-enabled assembler source.
#-----------------------------------------------------------------------
%.o: $(ASM_PATH_PREFIX)%.cicsasm
	STEPLIB=$(CICS_SDFHLOAD_DS) $(BLD_TOOLS_DIR)/bin/invoke-cics-translator.rexx -a -v $<
	$(AS) $(AS_OPTS) -I $(CICS_SDFHMAC_DS) $(*).s $(STDERR_QUIET_REDIRECT)

#-----------------------------------------------------------------------
# Rule for building an object file from CICS-enabled assembler source with cics42 macros
#-----------------------------------------------------------------------
%.o: $(ASM_PATH_PREFIX)%.cics42asm
	STEPLIB=$(CICS_SDFHLOAD_DS) $(BLD_TOOLS_DIR)/bin/invoke-cics-translator.rexx -a -v $<
	$(AS) $(AS_OPTS) -I $(CICS42_SDFHMAC_DS) $(*).s $(STDERR_QUIET_REDIRECT)

#-----------------------------------------------------------------------
# Rule for building an object file from CICS-enabled assembler source with cics51 macros
#-----------------------------------------------------------------------
%.o: $(ASM_PATH_PREFIX)%.cics51asm
	STEPLIB=$(CICS_SDFHLOAD_DS) $(BLD_TOOLS_DIR)/bin/invoke-cics-translator.rexx -a -v $<
	$(AS) $(AS_OPTS) -I $(CICS51_SDFHMAC_DS) $(*).s $(STDERR_QUIET_REDIRECT)

#-----------------------------------------------------------------------
# Rule for building an object file from CICS-enabled assembler source with cics52 macros
#-----------------------------------------------------------------------
%.o: $(ASM_PATH_PREFIX)%.cics52asm
	STEPLIB=$(CICS_SDFHLOAD_DS) $(BLD_TOOLS_DIR)/bin/invoke-cics-translator.rexx -a -v $<
	$(AS) $(AS_OPTS) -I $(CICS52_SDFHMAC_DS) $(*).s $(STDERR_QUIET_REDIRECT)

#-----------------------------------------------------------------------
# Rule for building an object file from CICS-enabled assembler source with cics53 macros
#-----------------------------------------------------------------------
%.o: $(ASM_PATH_PREFIX)%.cics53asm
	STEPLIB=$(CICS_SDFHLOAD_DS) $(BLD_TOOLS_DIR)/bin/invoke-cics-translator.rexx -a -v $<
	$(AS) $(AS_OPTS) -I $(CICS53_SDFHMAC_DS) $(*).s $(STDERR_QUIET_REDIRECT)
	
#-----------------------------------------------------------------------
# Rule for building a pre-processed C source file.  This is intended as
# a debugging aid when used to build a specific .i part.
#-----------------------------------------------------------------------
%.i: $(C_PATH_PREFIX)%.c | dsects nlsprops
	$(CC) $(CFLAGS) -q64 -P $<

#-----------------------------------------------------------------------
# Rule for compiling LE enabled C
#-----------------------------------------------------------------------
%.o: $(C_PATH_PREFIX)%.c
	$(CC) $(CFLAGS) -q64 $<
	mv $(df) $(df).d

#-----------------------------------------------------------------------
# Rule for compiling LE enabled C in the test1/ dir
#-----------------------------------------------------------------------
test1/%.o: $(C_PATH_PREFIX)test1/%.c
	$(CC) $(CFLAGS) -q64 $< -o test1/$(*).o
	mv $(df) $(df).d

#-----------------------------------------------------------------------
# Rule for compiling .cpp files with STRICT (no compiler optimizations)
#-----------------------------------------------------------------------
%.o: $(C_PATH_PREFIX)%.strict.cpp
	$(CPPC) $(CPPFLAGS_STRICT) $< -o $(*).o

#-----------------------------------------------------------------------
# Rule for compiling .cpp files with STRICT (no compiler optimizations) in test1/ dir
#-----------------------------------------------------------------------
test1/%.o: $(C_PATH_PREFIX)test1/%.strict.cpp
	$(CPPC) $(CPPFLAGS_STRICT) $< -o test1/$(*).o


#-----------------------------------------------------------------------
# Rule for compiling .cpp files
#-----------------------------------------------------------------------
%.o: $(C_PATH_PREFIX)%.cpp
	$(CPPC) $(CPPFLAGS) $<

#-----------------------------------------------------------------------
# Rule for compiling .cpp files in test1/ dir
#-----------------------------------------------------------------------
test1/%.o: $(C_PATH_PREFIX)test1/%.cpp
	$(CPPC) $(CPPFLAGS) $< -o test1/$(*).o

