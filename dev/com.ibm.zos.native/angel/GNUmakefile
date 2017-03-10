#-----------------------------------------------------------------------
# IBM Confidential
#
# OCO Source Materials
#
# Copyright IBM Corp. 2011
#
# The source code for this program is not published or otherwise
# divested of its trade secrets, irrespective of what has been
# deposited with the U.S. Copyright Office.
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Default target
#-----------------------------------------------------------------------
all: link

#-----------------------------------------------------------------------
# Include the rules for the make.  BLD_ROOT_DIR is defined in the root
# make file and is exported for these sub-make files.
#-----------------------------------------------------------------------
MC_PATH_PREFIX = ../
CC_SUBMAKE_FLAGS = -DANGEL_COMPILE
include $(BLD_ROOT_DIR)/bldtools/config/rules.mak

#-----------------------------------------------------------------------
# Target list
#-----------------------------------------------------------------------
# File lists for different DLLs
ANGEL_OBJECTS= \
angel_bgvt_services.o \
angel_main.o \
angel_pc_initialization.o \
angel_process_data.o \
angel_sgoo_services.o \
angel_task_data.o \
angel_tracing_functions.o \
bpx_load.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_contents_supervisor.o \
mvs_cpool_services.o \
mvs_enq.o \
mvs_estae.o \
mvs_iarv64.o \
mvs_pause_release_lock.o \
mvs_psw.o \
mvs_resmgr.o \
mvs_storage.o \
mvs_user_token_manager.o \
mvs_utils.o \
mvs_wto.o \
stack_services.o

DRM_OBJECTS= \
angel_bgvt_services.o \
angel_client_pc.o \
angel_client_process_data.o \
angel_dynamic_replaceable_module.o \
angel_functions.o \
angel_process_data.o \
angel_server_pc.o \
angel_sgoo_services.o \
angel_task_data.o \
angel_tracing_functions.o \
bpx_ipt.o \
bpx_load.o \
bpx_stat.o \
client_dynamic_area_cell_pool.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_contents_supervisor.o \
mvs_cpool_services.o \
mvs_enq.o \
mvs_estae.o \
mvs_extract.o \
mvs_iarv64.o \
mvs_pause_release_lock.o \
mvs_psw.o \
mvs_qedit.o \
mvs_resmgr.o \
mvs_setlock.o \
mvs_storage.o \
mvs_svcdump_services.o \
mvs_tcb_iterator.o \
mvs_user_token_manager.o \
mvs_utils.o \
mvs_wait.o \
mvs_wto.o \
security_saf_authorization.o \
stack_services.o

FSM_OBJECTS= \
angel_armv_services.o \
angel_bgvt_services.o \
angel_client_process_data.o \
angel_fixed_shim_module.o \
angel_fixed_shim_pc.o \
angel_fixed_shim_resmgr.o \
angel_process_data.o \
angel_sgoo_services.o \
angel_task_data.o \
angel_tracing_functions.o \
bpx_ipt.o \
bpx_load.o \
client_dynamic_area_cell_pool.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_contents_supervisor.o \
mvs_cpool_services.o \
mvs_enq.o \
mvs_estae.o \
mvs_iarv64.o \
mvs_pause_release_lock.o \
mvs_psw.o \
mvs_resmgr.o \
mvs_storage.o \
mvs_user_token_manager.o \
mvs_utils.o \
mvs_wto.o \
stack_services.o

BBGZACHK_OBJECTS= \
angel_check_main.o \
angel_check_module.o

ANGEL_CLIENT_BIND_REPORT_UTILITY_OBJECTS= \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_iarv64.o \
mvs_psw.o \
mvs_user_token_manager.o \
angel_client_bind_report.o

OBJECT_TARGETS = \
$(ANGEL_OBJECTS) \
$(DRM_OBJECTS) \
$(FSM_OBJECTS) \
$(BBGZACHK_OBJECTS)

PRODUCT_LOAD_MODULES = \
bbgzachk \
bbgzadrm \
bbgzafsm \
bbgzangl

SERVICE_LOAD_MODULES = \
bbgzacbr

ANGEL_TEST_OBJECTS = \
angel_bgvt_services.o \
angel_check_main.o \
angel_check_module.o \
angel_sgoo_services.o \
angel_tracing_functions.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_cpool_services.o \
mvs_iarv64.o \
mvs_pause_release_lock.o \
mvs_psw.o \
mvs_storage.o \
mvs_user_token_manager.o \
mvs_utils.o \
mvs_wto.o \
test1/angel_check_test.o

ANGEL_TEST_MODULES = \
bbgzchkt

#-----------------------------------------------------------------------
# Targets
#-----------------------------------------------------------------------
$(OBJECT_TARGETS): | prep

link: $(PRODUCT_LOAD_MODULES)

bbgzadrm.extattr:=ap--
bbgzadrm: $(DRM_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -eBBGZADRM -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
	../bbgzscan BBGZADRM < $@.llst > $@.scanc
	

bbgzafsm.extattr=ap--
bbgzafsm: $(FSM_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -eBBGZAFSM -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
	../bbgzscan BBGZAFSM < $@.llst > $@.scanc

bbgzachk.extattr:=ap--
bbgzachk: $(BBGZACHK_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -eBBGZACHK -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
	../bbgzscan BBGZACHK < $@.llst > $@.scanc

bbgzangl.extattr:=aps-
bbgzangl: $(ANGEL_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@

# These are not built by default, must be specified explicitly.
#  gmake BLD_ROOT_DIR=/u/yourid/rtcbuildir -C angel bbgzacbr
bbgzacbr: $(ANGEL_CLIENT_BIND_REPORT_UTILITY_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@

# These are not built by default, must be specified explicitly.
#  gmake BLD_ROOT_DIR=/u/yourid/rtcbuildir -C angel prep bbgzchkt
bbgzchkt.extattr:=aps-
bbgzchkt: $(ANGEL_TEST_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
# End modules not built by default.

clean:
	rm -Rf $(DEPDIR) $(PRODUCT_LOAD_MODULES) $(ANGEL_TEST_MODULES) $(ANGEL_TEST1_DIR)
	rm -f *.o *.alst *.clst *.llst *.i *.x *.so
	ls -1 *.s 2>/dev/null | sed 's/\(.*\).s/\1/' | while read LINE; do if [ -e $(MC_PATH_PREFIX)$$LINE.mc -a -e $$LINE.s ]; then rm $$LINE.s; fi done

prep:
	mkdir -p $(DEPDIR)
	mkdir -p $(ANGEL_TEST1_DIR)

pax: link
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzadrm.extattr) ./bbgzadrm
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzafsm.extattr) ./bbgzafsm
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzachk.extattr) ./bbgzachk
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzangl.extattr) ./bbgzangl
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\../macros/@wlp/dev/api/ibm/zos/@p ../macros/BBGZAACK
# add to properties file to pax to recover extended attributes later
	echo "bbgzadrm $(bbgzadrm.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzafsm $(bbgzafsm.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzachk $(bbgzachk.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzangl $(bbgzangl.extattr)" >> $(DISTDIR)/extattr.properties

.PHONY: all link clean prep pax

#-----------------------------------------------------------------------
# Special targets
#-----------------------------------------------------------------------
.SILENT: clean prep

# Keep generated artifacts
ifdef keep_generated
.SECONDARY:
endif

# Delete files if make encounters an error (ie linker fails, we get a zero length file)
.DELETE_ON_ERROR:

#-----------------------------------------------------------------------
# Dependency generation
#
# Dependency files are created during initial compilation.  By Using
# -include instead of include, missing dependency files won't cause
# the make to fail.
#-----------------------------------------------------------------------
df = $(DEPDIR)/$*.o
-include $(addprefix $(DEPDIR)/, $(addsuffix .d, $(shell echo $(sort $(OBJECT_TARGETS)))))
