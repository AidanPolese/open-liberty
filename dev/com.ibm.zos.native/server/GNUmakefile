#-----------------------------------------------------------------------
# IBM Confidential
#
# OCO Source Materials
#
# Copyright IBM Corp. 2011, 2016
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
C_PATH_PREFIX = ../
ASM_PATH_PREFIX = ../
CC_SUBMAKE_FLAGS = -DSERVER_COMPILE
include $(BLD_ROOT_DIR)/bldtools/config/rules.mak

#-----------------------------------------------------------------------
# Target list
#-----------------------------------------------------------------------
# File lists for different DLLs
CSL_OBJECTS= \
bpx_stat.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_iarv64.o \
mvs_pause_release_lock.o \
mvs_psw.o \
mvs_storage.o \
mvs_utils.o \
security_saf_authorization.o \
server_start_proc.o \
server_tracing_functions_auth.o \
server_trgoo_services.o \
stack_services.o

SAFM_OBJECTS= \
angel_bgvt_services.o \
angel_process_data.o \
bpx_ipt.o \
bpx_load.o \
heap_management.o \
mvs_abend.o \
mvs_aio_services.o \
mvs_cell_pool_services.o \
mvs_contents_supervisor.o \
mvs_cpool_services.o \
mvs_enq.o \
mvs_estae.o \
mvs_iarv64.o \
mvs_ifausage.o \
mvs_latch.o \
mvs_resmgr.o \
mvs_pause_release_lock.o \
mvs_plo.o \
mvs_psw.o \
mvs_stimerm.o \
mvs_storage.o \
mvs_svcdump_services.o \
mvs_utils.o \
mvs_user_token_manager.o \
mvs_wto.o \
petvet.o \
security_saf_acee.o \
security_saf_authentication.o \
security_saf_authorization.o \
security_saf_authz.o \
security_saf_sandbox.o \
security_saf_sync_to_thread.o \
server_authorized_function_module.o \
server_kernel_functions.o \
server_lcom_services.o \
server_local_comm_client.o \
server_local_comm_cleanup.o \
server_local_comm_data_store.o \
server_local_comm_ffdc.o \
server_local_comm_footprint.o \
server_local_comm_global_lock.o \
server_local_comm_queue.o \
server_local_comm_shared_memory.o \
server_process_data.o \
server_task_data.o \
server_tracing_functions_auth.o \
server_trgoo_services.o \
server_util_queue_services.o \
server_util_registry.o \
server_util_sortedcache.o \
server_wlm_services.o \
server_wola_registration.o \
server_wola_registration_server.o \
server_wola_services.o \
server_wola_service_queues.o \
server_wola_shared_memory_anchor.o \
server_wola_shared_memory_anchor_server.o \
stack_services.o \
server_wola_unauth_services.o \
mvs_wait.o \
tx_authorized_rrs_services.o

SCFM_OBJECTS= \
angel_process_data.o \
bbgaimss.o \
bpx_ipt.o \
client_dynamic_area_cell_pool.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_enq.o \
mvs_estae.o \
mvs_iarv64.o \
mvs_pause_release_lock.o \
mvs_plo.o \
mvs_psw.o \
mvs_stimerm.o \
mvs_storage.o \
mvs_svcdump_services.o \
mvs_user_token_manager.o \
mvs_utils.o \
mvs_wto.o \
petvet.o \
server_common_function_module.o \
server_local_comm_cleanup.o \
server_local_comm_client.o \
server_local_comm_data_store.o \
server_local_comm_ffdc.o \
server_local_comm_footprint.o \
server_local_comm_global_lock.o \
server_local_comm_shared_memory.o \
server_local_comm_queue.o \
server_process_data.o \
security_saf_authorization.o \
server_tracing_functions_auth.o \
server_trgoo_services.o \
server_wola_client.o \
server_wola_client_connection_get.o \
server_wola_client_connection_release.o \
server_wola_client_get_context.o \
server_wola_client_get_data.o \
server_wola_client_host_service.o \
server_wola_client_invoke.o \
server_wola_client_rebind.o \
server_wola_client_receive_request_any.o \
server_wola_client_receive_request_specific.o \
server_wola_client_receive_response_length.o \
server_wola_client_register.o \
server_wola_client_send_request.o \
server_wola_client_send_response.o \
server_wola_client_send_response_exception.o \
server_wola_client_unregister.o \
server_wola_connection_pool.o \
server_wola_message.o \
server_wola_nametoken_utility.o \
server_wola_registration.o \
server_wola_service_queues.o \
server_wola_shared_memory_anchor.o \
stack_services.o 

NATIVE_SERVICES= \
mvs_psw.o \
security_saf_authz_jni.o \
security_saf_credentials.o \
security_saf_registry.o \
security_saf_sync_to_thread_jni.o \
server_async_io.o \
server_bytebuffer.o \
server_command_jni.o \
server_diagnostics_functions.o \
server_dump_functions_jni.o \
server_ipt_stubs.o \
server_jni_method_manager.o \
server_kernel_functions_jni.o \
server_lcom_services_jni.o \
server_logging_functions.o \
server_native_service_tracker.o \
server_nls_messages.o \
server_product_registration_jni.o \
server_smf_jni.o \
server_tagging_jni.o \
server_thread_term_manager.o \
server_tracing_functions.o \
server_util_tiot.o \
server_utils_jni.o \
server_wlm_services_jni.o \
server_wola_services_jni.o \
stack_services_c.o \
tx_rrs_services_jni.o \
zos_connect_saf.o

SRV_OBJECTS= \
server_launcher.o

SUFM_OBJECTS= \
angel_process_data.o \
angel_server_pc_stub.o \
bpx_ipt.o \
heap_management.o \
mvs_abend.o \
mvs_cell_pool_services.o \
mvs_enq.o \
mvs_estae.o \
mvs_extract.o \
mvs_iarv64.o \
mvs_ifausage.o \
mvs_pause_release_lock.o \
mvs_psw.o \
mvs_qedit.o \
mvs_resmgr.o \
mvs_storage.o \
mvs_user_token_manager.o \
mvs_utils.o \
mvs_wait.o \
mvs_wto.o \
petvet.o \
server_command_functions.o \
server_function_module_stub.o \
server_process_data.o \
server_task_data.o \
server_tracing_functions_unauth.o \
server_trgoo_services.o \
server_unauth_test_functions.o \
server_utils_unauth.o \
server_wola_unauth_services.o \
stack_services.o 

LOCAL_COMM_SHARED_MEMORY_CLEANUP_UTILITY_OBJECTS= \
mvs_enq.o \
mvs_iarv64.o \
mvs_psw.o \
mvs_user_token_manager.o \
server_local_comm_shared_memory_cleanup.o

BBOASHR_SHARED_MEMORY_CLEANUP_UTILITY_OBJECTS= \
mvs_enq.o \
mvs_psw.o \
mvs_user_token_manager.o \
server_bboashr_shared_memory_cleanup.o

WOLA_CLIENT_REGISTER= \
bboa1reg.o 

WOLA_CLIENT_UNREGISTER= \
bboa1urg.o

WOLA_CLIENT_INVOKE= \
bboa1inv.o 

WOLA_CLIENT_HOST_SERVICE= \
bboa1srv.o 

WOLA_CLIENT_SEND_RESP= \
bboa1srp.o

WOLA_CLIENT_CONN_RELEASE= \
bboa1cnr.o

WOLA_CLIENT_CONN_GET= \
bboa1cng.o

WOLA_CLIENT_GET_DATA= \
bboa1get.o

WOLA_CLIENT_GET_CONTEXT= \
bboa1gtx.o

WOLA_CLIENT_SEND_REQUEST= \
bboa1srq.o

WOLA_CLIENT_RECEIVE_RESP_LEN= \
bboa1rcl.o

WOLA_CLIENT_RECEIVE_REQ_ANY= \
bboa1rca.o

WOLA_CLIENT_RECEIVE_REQ_SPECIFIC= \
bboa1rcs.o

WOLA_CLIENT_SEND_RESP_EXCEPTION= \
bboa1srx.o

WOLA_IMS_ESMT= \
bboaiemt.o

WOLA_IMS_ESMT_INIT= \
bboaidmy.o \
mvs_abend.o \
wola_ims_esmt_init_exit.o \
wola_normal_call_exit.o

WOLA_CLIENT_OBJECTS= \
angel_client_pc_stub.o \
angel_server_pc_stub.o \
bboascng.o \
bboasget.o \
bboasgtx.o \
bboassrq.o \
bboasrcl.o \
bboasrca.o \
bboasrcs.o \
bboassrx.o \
bboassrv.o \
bboassrp.o \
bboascnr.o \
bboasinf.o \
bboasinv.o \
bboasreg.o \
bboasurg.o \
client_wola_stubs.o \
mvs_enq.o \
mvs_estae.o \
mvs_plo.o \
mvs_storage.o \
mvs_user_token_manager.o \
server_bboacall_services.o \
server_common_function_module_stub.o \
server_wola_nametoken_utility.o 

WOLA_CICS_TRUE_OBJECTS= \
bboabs41.o \
bboabs42.o \
bboabs51.o \
bboabs52.o \
bboabs53.o \
bboacont.o \
bboaic42.o \
bboaic51.o \
bboaic52.o \
bboaic53.o \
bboatrue.o \
bboatrup.o.twas

WOLA_CLIENT_CPP_LINK_SERVER_SUPPORT= \
bboa1reg.o \
bboa1cnr.o \
bboa1srx.o \
bboa1get.o \
bboa1gtx.o \
bboa1inf.o \
bboa1srp.o \
bboa1rca.o \
bboa1rcs.o \
bboa1urg.o

WOLA_CLIENT_CPP_LINK_SERVER_CNTL= \
bboacntl.o \
$(WOLA_CLIENT_CPP_LINK_SERVER_SUPPORT)

WOLA_CLIENT_CPP_LINK_SERVER_CLNK= \
bboaclnk.o \
bboac2pc.o.twas \
bboac242.o.twas \
bboac251.o.twas \
bboac252.o.twas \
$(WOLA_CLIENT_CPP_LINK_SERVER_SUPPORT)
#bboaceli.o

WOLA_CLIENT_CPP_LINK_SERVER_CSRV= \
bboacsrv.o \
$(WOLA_CLIENT_CPP_LINK_SERVER_SUPPORT)

WOLA_NORMAL_CALL_OBJECTS= \
wola_normal_call_exit.o

ZFAT_NATIVE_SERVICES= \
zfat_wlm_services_jni.o

JBATCH_NATIVE_UTILITY_OBJECTS_NO_MAIN= \
jbatch_json.o \
jbatch_messages.o \
jbatch_models.o \
jbatch_mq.o \
jbatch_native_utility.o \
jbatch_task_args.o \
jbatch_wola.o \
jbatch_utils.o \
$(WOLA_CLIENT_REGISTER) \
$(WOLA_CLIENT_UNREGISTER) \
$(WOLA_CLIENT_INVOKE) \
$(WOLA_CLIENT_HOST_SERVICE) \
$(WOLA_CLIENT_SEND_RESP) \
$(WOLA_CLIENT_CONN_RELEASE) \
$(WOLA_CLIENT_CONN_GET) \
$(WOLA_CLIENT_GET_DATA) \
$(WOLA_CLIENT_SEND_REQUEST) \
$(WOLA_CLIENT_RECEIVE_RESP_LEN) \
$(WOLA_CLIENT_RECEIVE_REQ_ANY) \
$(WOLA_CLIENT_RECEIVE_REQ_SPECIFIC) \
$(WOLA_CLIENT_SEND_RESP_EXCEPTION) \

JBATCH_NATIVE_UTILITY_OBJECTS= \
jbatch_native_utility_main.o \
$(JBATCH_NATIVE_UTILITY_OBJECTS_NO_MAIN) 


UNIT_TEST_OBJECTS= \
$(SAFM_OBJECTS) \
$(SCFM_OBJECTS) \
test1/CuTest.o \
test1/CuTest_test.o \
test1/mvs_cell_pool_services_test.o \
test1/mvs_plo_test.o \
test1/mvs_stimerm_test.o \
test1/security_saf_authentication_test.o \
test1/security_saf_authorization_test.o \
test1/server_local_comm_queue_test.o \
test1/server_wola_connection_pool_test.o \
test1/server_wola_message_test.o \
test1/server_wola_registration_test.o \
test1/server_wola_services_test.o \
test1/server_wola_service_queues_test.o \
test1/server_wola_shared_memory_anchor_test.o \
test1/server_wola_client_test.o \
test1/server_wola_client_get_context_test.o \
test1/unit_test_main.o

# Note: the dir is named "test1" to avoid colliding with the make target "test",
# 		which confuses make for some reason
UNIT_TEST_LEC_OBJECTS= \
$(NATIVE_SERVICES) \
test1/CuTest_lec.o \
test1/CuTest_test_lec.o \
test1/server_util_tiot_test.o \
test1/unit_test_main_lec.o 

UNIT_TEST_LECPP_OBJECTS= \
test1/unit_test_main_lecpp.o \
test1/CuTest_lecpp.o \
test1/CuTest_test_lecpp.o \
test1/jbatch_json_test.o \
test1/jbatch_models_test.o \
test1/jbatch_native_utility_test.o \
test1/jbatch_utils_test.o \
$(JBATCH_NATIVE_UTILITY_OBJECTS_NO_MAIN) 

OBJECT_TARGETS = \
$(WOLA_CLIENT_CPP_LINK_SERVER_CSRV) \
$(NATIVE_SERVICES) \
$(SAFM_OBJECTS) \
$(SCFM_OBJECTS) \
$(SRV_OBJECTS) \
$(SUFM_OBJECTS) \
$(WOLA_CLIENT_CPP_LINK_SERVER_CLNK) \
$(WOLA_CLIENT_CPP_LINK_SERVER_CNTL) \
$(WOLA_CLIENT_REGISTER) \
$(WOLA_CLIENT_UNREGISTER) \
$(WOLA_CLIENT_INVOKE) \
$(WOLA_CLIENT_HOST_SERVICE) \
$(WOLA_CLIENT_SEND_RESP) \
$(WOLA_CLIENT_CONN_RELEASE) \
$(WOLA_CLIENT_CONN_GET) \
$(WOLA_CLIENT_GET_DATA) \
$(WOLA_CLIENT_SEND_REQUEST) \
$(WOLA_CLIENT_RECEIVE_RESP_LEN) \
$(WOLA_CLIENT_RECEIVE_REQ_ANY) \
$(WOLA_CLIENT_RECEIVE_REQ_SPECIFIC) \
$(WOLA_CLIENT_SEND_RESP_EXCEPTION) \
$(WOLA_CLIENT_OBJECTS) \
$(WOLA_CICS_TRUE_OBJECTS) \
$(JBATCH_NATIVE_UTILITY_OBJECTS) \
$(CSL_OBJECTS)

PRODUCT_LOAD_MODULES = \
batchManagerZos \
bbgzcsl \
bbgzsafm \
bbgzscfm \
bbgzsrv \
bbgzsufm \
bboa1rcl \
bboa1rcs \
bboa1rca \
bboa1srq \
bboa1get \
bboa1gtx \
bboa1cng \
bboa1srx \
bboa1inv \
bboa1reg \
bboa1urg \
bboa1srv \
bboa1srp \
bboa1cnr \
bboacall \
bboaclnk \
bboacntl \
bboacsrv \
bboaiemi \
bboaiemt \
bboatrue \
libzNativeServices.so

SERVICE_LOAD_MODULES = \
bbgzlcmc \
bbgzshrc

UNIT_TEST_LOAD_MODULES = \
bbgznut \
bbgznut_lec

WLM_ZFAT_LOAD_MODULES = \
bbgzzfat

#-----------------------------------------------------------------------
# Targets
#-----------------------------------------------------------------------
$(OBJECT_TARGETS): | prep

link: $(PRODUCT_LOAD_MODULES)

bbgzsafm.extattr=ap--
bbgzsafm: $(SAFM_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -eBBGZSAFM -o $@ $^ $(METAL_STATIC_OBJS) -S //\'$(IMSV12_ADFSLOAD_DS)\' -u DFSYCRET -V > $@.llst
	chmod 755 $@

bbgzscfm.extattr=ap--
bbgzscfm: $(SCFM_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -eBBGZSCFM -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
	../bbgzscan BBGZSCFM < $@.llst > $@.scanc

bbgzsufm.extattr=-p--
bbgzsufm: $(SUFM_OBJECTS)
	$(LD) $(LDOPTS) -eBBGZSUFM -o $@ $^ $(METAL_STATIC_OBJS) -S //\'$(IMSV12_ADFSLOAD_DS)\' -u DFSYCRET -V > $@.llst
	chmod 755 $@
	
bbgzcsl.extattr=aps-
bbgzcsl: $(CSL_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@

bbgzsrv.extattr=-ps-
bbgzsrv: $(SRV_OBJECTS)
	$(CC) -F $(BLD_CONFIG_DIR)/xlc.c.cfg -Wl,MAP=YES,LIST,$(LD_COMPAT) $(LINK_LISTING) $(CC_STEPLIB_FLAGS) $(C_IPA_LINK_FLAGS) -q64 -qxplink -o $@ $^ > $@.maplst
	chmod 755 $@

libzNativeServices.so.extattr=-ps-
libzNativeServices.so: $(NATIVE_SERVICES)
	$(CC) -F $(BLD_CONFIG_DIR)/xlc.c.cfg -Wl,dll,MAP=YES,LIST,$(LD_COMPAT) $(LINK_LISTING) $(CC_STEPLIB_FLAGS) $(C_IPA_LINK_FLAGS) -q64 -qxplink -o $@ $^ > $@.maplst
	chmod 755 $@

batchManagerZos.extattr=-ps-
batchManagerZos: $(JBATCH_NATIVE_UTILITY_OBJECTS)
	$(LD) $(LDCPPOPTS) -bdynam=dll -u DFHELII -o $@ $(BLD_ROOT_DIR)/import/CSQBMQ1 $^ -V > $@.llst

# These are not built by default, must be specified explicitly.
#  gmake BLD_ROOT_DIR=/u/yourid/rtcbuildir -C server bbgzlcmc
#
# !! NOTE: bbgznut, bbgzlcmc, and bbgzshrc are all built by the "test build"
# target and can all be installed via "ezwas install".
#
bbgzlcmc: $(LOCAL_COMM_SHARED_MEMORY_CLEANUP_UTILITY_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
	
# These are not built by default, must be specified explicitly.
#  gmake BLD_ROOT_DIR=/u/yourid/rtcbuildir -C server bbgzshrc
#
# !! NOTE: bbgznut, bbgzlcmc, and bbgzshrc are all built by the "test build"
# target and can all be installed via "ezwas install".
# 
# usage:  s BBGZSHRC,parms=<wola-group-name>
# 	 eg:  s BBGZSHRC,parms=LIBERTY 
#
bbgzshrc: $(BBOASHR_SHARED_MEMORY_CLEANUP_UTILITY_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@

# These are not built by default, must be specified explicitly.
#  e.g: /u/sykesm/bin/gmake BLD_ROOT_DIR=/u/ralder/rtcsb.liberty.x -C /u/ralder/rtcsb.liberty.x/server bbgznut
#
# !! NOTE: bbgznut, bbgzlcmc, and bbgzshrc are all built by the "test build"
# target and can all be installed via "ezwas install".
#
bbgznut.extattr:=aps-
bbgznut: $(UNIT_TEST_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -emain -o $@ $(BLD_ROOT_DIR)/import/CSQBMQ1 $^ $(METAL_STATIC_OBJS) -V > $@.llst
	chmod 755 $@
	
# These are not built by default, must be specified explicitly.
#  e.g: /u/sykesm/bin/gmake BLD_ROOT_DIR=/u/ralder/rtcsb.liberty.x -C /u/ralder/rtcsb.liberty.x/server bbgznut_lec
# OR... run the "test build" target from RTC. 
# Note: "ezwas install" will install the modules on your ezwas machine.
bbgznut_lec.extattr=-ps-
bbgznut_lec: $(UNIT_TEST_LEC_OBJECTS)
	$(CC) -F $(BLD_CONFIG_DIR)/xlc.c.cfg -Wl,dll,MAP=YES,LIST,$(LD_COMPAT) $(LINK_LISTING) $(CC_STEPLIB_FLAGS) $(C_IPA_LINK_FLAGS) -q64 -qxplink -o $@ $^ > $@.maplst
	chmod 755 $@

# These are not built by default, must be specified explicitly.
#  e.g: /u/sykesm/bin/gmake BLD_ROOT_DIR=/u/ralder/rtcsb.liberty.x -C /u/ralder/rtcsb.liberty.x/server bbgznut_lecpp
# OR... run the "test build" target from RTC. 
# Note: "ezwas install" will install the modules on your ezwas machine.
bbgznut_lecpp.extattr=-ps-
bbgznut_lecpp: $(UNIT_TEST_LECPP_OBJECTS)
	$(LD) $(LDCPPOPTS) -bdynam=dll -u DFHELII -o $@ $(BLD_ROOT_DIR)/import/CSQBMQ1 $^ -V > $@.llst

# These are not built by default, must be specified explicitly.
#  gmake BLD_ROOT_DIR=/u/yourid/rtcbuildir -C server bbgzzfat
#
# !! NOTE: bbgznut, bbgzlcmc, and bbgzshrc are all built by the "test build"
# target and can all be installed via "ezwas install".
bbgzzfat: $(ZFAT_NATIVE_SERVICES)
	$(CC) -F $(BLD_CONFIG_DIR)/xlc.c.cfg -Wl,dll,MAP=YES,LIST,$(LD_COMPAT) $(LINK_LISTING) $(CC_STEPLIB_FLAGS) $(C_IPA_LINK_FLAGS) -q64 -qxplink -o $@ $^ > $@.maplst
	chmod 755 $@

# These modules need to be moved explicitly moved to target system and loaded into a dataset

bboaiemt: $(WOLA_IMS_ESMT)
	$(LD) $(LDOPTS) -bAMODE=24 -bRMODE=24 -bAC=1 -eMAINEP -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	
bboaiemi: $(WOLA_IMS_ESMT_INIT);
	$(LD) $(LDOPTS) -bAMODE=31 -bRMODE=ANY -bAC=1 -einitESMTExit -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1reg: $(WOLA_CLIENT_REGISTER)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1REG -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1urg: $(WOLA_CLIENT_UNREGISTER)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1URG -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1inv: $(WOLA_CLIENT_INVOKE)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1INV -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1srv: $(WOLA_CLIENT_HOST_SERVICE)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1SRV -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1srp: $(WOLA_CLIENT_SEND_RESP)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1SRP -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1cnr: $(WOLA_CLIENT_CONN_RELEASE)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1CNR -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1rcl: $(WOLA_CLIENT_RECEIVE_RESP_LEN)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1RCL -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1get: $(WOLA_CLIENT_GET_DATA)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1GET -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1gtx: $(WOLA_CLIENT_GET_CONTEXT)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1GTX -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1rcs: $(WOLA_CLIENT_RECEIVE_REQ_SPECIFIC)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1RCS -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1rca: $(WOLA_CLIENT_RECEIVE_REQ_ANY)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1RCA -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1srq: $(WOLA_CLIENT_SEND_REQUEST)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1SRQ -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1cng: $(WOLA_CLIENT_CONN_GET)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1CNG -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboa1srx: $(WOLA_CLIENT_SEND_RESP_EXCEPTION)
	$(LD) $(LDOPTS) -bAC=1 -eBBOA1SRX -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

bboacall.extattr=ap--
bboacall: $(WOLA_CLIENT_OBJECTS)
	$(LD) $(LDOPTS) -bAC=1 -eBBGAXVEC -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	../bbgzscan BBOACALL < $@.llst > $@.scanc

bboaclnk: $(WOLA_CLIENT_CPP_LINK_SERVER_CLNK)
	$(LD) $(LDCPPOPTS) -u DFHELII -o $@ $^ -V > $@.llst
	
bboacntl: $(WOLA_CLIENT_CPP_LINK_SERVER_CNTL)
	$(LD) $(LDCPPOPTS) -u DFHELII -o $@ $^ -V > $@.llst
	
bboacsrv: $(WOLA_CLIENT_CPP_LINK_SERVER_CSRV)
	$(LD) $(LDCPPOPTS) -u DFHELII -o $@ $^ -V > $@.llst

bboatrue: $(WOLA_CICS_TRUE_OBJECTS)
	$(LD) $(LDOPTS) -eBBOATRUE -S //\'$(CICS_SDFHLOAD_DS)\' -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst

#bboaical: $(WOLA_NORMAL_CALL_OBJECTS)
#	$(LD) $(LDOPTS) -bAMODE=31 -bRMODE=ANY -bAC=1 -enormalCallExit -o $@ $^ $(METAL_STATIC_OBJS) -V > $@.llst
	
# End modules not built by default.

prep:
	mkdir -p $(DEPDIR)

clean:
	rm -Rf $(DEPDIR) $(PRODUCT_LOAD_MODULES) $(SERVICE_LOAD_MODULES) $(UNIT_TEST_LOAD_MODULES) $(WLM_ZFAT_LOAD_MODULES)
	rm -f *.o *.alst *.clst *.llst *.i *.x *.so
	rm -f test1/*.o test1/*.alst test1/*.clst test1/*.llst test1/*.i test1/*.x test1/*.so
	ls -1 *.s 2>/dev/null | sed 's/\(.*\).s/\1/' | while read LINE; do if [ -e $(MC_PATH_PREFIX)$$LINE.mc -a -e $$LINE.s ]; then rm $$LINE.s; fi done

pax: link
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzsafm.extattr) ./bbgzsafm
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzscfm.extattr) ./bbgzscfm
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzsrv.extattr)  ./bbgzsrv
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzsufm.extattr) ./bbgzsufm
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(libzNativeServices.so.extattr) ./libzNativeServices.so
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bboacall.extattr) ./bboacall
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(batchManagerZos.extattr) ./batchManagerZos
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/lib/native/zos/s390x/@p -oZOS.extattr=$(bbgzcsl.extattr) ./bbgzcsl
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1reg
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1urg
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1inv
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1srv
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1srp
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1cnr
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1cng
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1get
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1rcl
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1srq
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1rca
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1rcs
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboa1srx
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboaclnk
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboacntl
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboacsrv
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboatrue
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboaiemt
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./@wlp/clients/zos/@p ./bboaiemi
	
	
# and add to properties file for extended attributes
	echo "bbgzsafm $(bbgzsafm.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzscfm $(bbgzscfm.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzsrv $(bbgzsrv.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzsufm $(bbgzsufm.extattr)" >> $(DISTDIR)/extattr.properties
	echo "libzNativeServices.so $(libzNativeServices.so.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bboacall $(bboacall.extattr)" >> $(DISTDIR)/extattr.properties
	echo "batchManagerZos $(batchManagerZos.extattr)" >> $(DISTDIR)/extattr.properties
	echo "bbgzcsl $(bbgzcsl.extattr)" >> $(DISTDIR)/extattr.properties

.PHONY: all link prep clean pax

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


