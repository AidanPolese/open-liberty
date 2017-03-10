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
# Build date and time information -- 8 characters each for eye catchers
#-----------------------------------------------------------------------
export BUILD_DATE_STAMP := $(shell /bin/date -u +%Y%m%d)
export BUILD_TIME_STAMP := $(shell /bin/date -u +%T)
export BUILD_IMAGE_STAMP := $(shell /bin/date -u +%Y%m%d-%H%M)

#-----------------------------------------------------------------------
# Build level information as a string in objects and listings
# LIBERTY_BETA_VERSION gets liberty.beta.version in build-antz-zos-liberty.xml
# LIBERTY_SERVICE_VERSION gets liberty.service.version in build-antz-zos-liberty.xml
# or they can be null if this build is kicked off without Ant.
# If either version was not set by the time build-antz-zos-liberty.xml
# runs, it gets a long string for a default value. 
#-----------------------------------------------------------------------
# Accept the versions for whatever they are and change them if needed
	BUILD_LEVEL_BETA := $(LIBERTY_BETA_VERSION)\ $(BUILD_IMAGE_STAMP)
	BUILD_LEVEL_SERVICE := $(LIBERTY_SERVICE_VERSION)\ $(BUILD_IMAGE_STAMP)
	LIBERTY_BUILD_LABEL := $(LIBERTY_SERVICE_VERSION)" "$(BUILD_LABEL)" / "$(LIBERTY_BETA_VERSION)" "$(BUILD_LABEL)
	
# If either version was not set and gets the default value from build-antz-zos-liberty.xml,
# change the value to Sandbox for BUILD_LEVEL_xxx
ifeq ("$(LIBERTY_BETA_VERSION)", "theVersionShouldHaveBeenSetWhenCallingTheBuild")
	BUILD_LEVEL_BETA := Sandbox\ $(BUILD_IMAGE_STAMP)
endif
ifeq ("$(LIBERTY_SERVICE_VERSION)", "theVersionShouldHaveBeenSetWhenCallingTheBuild")
	BUILD_LEVEL_SERVICE := Sandbox\ $(BUILD_IMAGE_STAMP)
endif
# If either version is null because this is not being run by Ant, change value to Sandbox 
ifeq ("$(LIBERTY_BETA_VERSION)", "")
	BUILD_LEVEL_BETA := Sandbox\ $(BUILD_IMAGE_STAMP)
endif
ifeq ("$(LIBERTY_SERVICE_VERSION)", "")
	BUILD_LEVEL_SERVICE := Sandbox\ $(BUILD_IMAGE_STAMP)
endif

export BUILD_LEVEL_BETA
export BUILD_LEVEL_SERVICE
export LIBERTY_BUILD_LABEL

export BLD_ROOT_DIR = $(CURDIR)
include $(BLD_ROOT_DIR)/bldtools/config/rules.mak

#-----------------------------------------------------------------------
# Instruct the code to generate version number/string for the angel
# fixed shim module (bbgzafsm) and dynamic replaceable module
# (bbgzadrm).  This will cause the modules to look different every time
# the code asks for the version, and will thus always reload the code.
#-----------------------------------------------------------------------
ifdef angel_generate_ver
ANGEL_GENERATE_VER_FILE_LIST = angel_functions.mc angel_fixed_shim_pc.mc
ANGEL_GENERATE_VER_STRING := $(shell /bin/date -u +%j%H%M%S | sed 's/0*//')
export ANGEL_GENERATE_VER = -DANGEL_GENERATE_VER=$(ANGEL_GENERATE_VER_STRING)
endif

#-----------------------------------------------------------------------
# Build list of header files to generate from dsects.
#-----------------------------------------------------------------------
DSECT_TARGETS = $(addprefix $(DSECT_HDR_DIR)/, $(addsuffix .h, $(basename $(notdir $(wildcard $(DSECT_SRC_DIR)/*.s)))))

#-----------------------------------------------------------------------
# Rule for generating a header from an assembler include
#-----------------------------------------------------------------------
.PRECIOUS: $(DSECT_HDR_DIR)/%.h
$(DSECT_HDR_DIR)/%.h: $(DSECT_SRC_DIR)/%.s
	$(BLD_TOOLS_DIR)/bin/generate-dsect-mapping.pl $< > $(DSECT_HDR_DIR)/$*.h $(STDERR_QUIET_REDIRECT)

#-----------------------------------------------------------------------
# Target list
#-----------------------------------------------------------------------
# File lists for different DLLs
SUBDIRS = angel server

# Set Java bin for native2ascii script
JAVA_BIN = /java/J6.0/bin/
#-----------------------------------------------------------------------
# Targets
#-----------------------------------------------------------------------

link: subdirs

# pax -w -x pax -a -f test.pax -oZOS.extattr=aps- -s@\.\.\/@wlp/lib/native/zos/s390x/@p ../bbgzangl
#   ../bbgzangl >> wlp/lib/native/zos/s390x/bbgzangl
# Brute force because I'm not smart enough to figure this out right now.
# Regardless, if you get this pax over to a test system, the following
# command will restore the modules with the correct attributes:
#     pax -r -ppx -f build-${BUILD_IMAGE_STAMP}
pax: link
	rm -rf $(DISTDIR)
	mkdir -p $(DISTDIR)
	touch $(DISTDIR)/extattr.properties
	for dir in $(SUBDIRS); do $(MAKE) -C $$dir pax; done
	chmod 755 procs
	chmod 644 procs/*
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./resources/com/ibm/ws/zos/@wlp/lib/native/zos/s390x/nls/@p ./resources/com/ibm/ws/zos/*.cat
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./procs/@wlp/templates/zos/procs/@p ./procs/
# Add the bboaapi.h and bboaapip.h header files to be used for applications	
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./include/client/@wlp/dev/api/ibm/zos/@p ./include/client/bboaapi*.h
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@\./include/client/@wlp/dev/api/ibm/zos/@p ./include/client/bboaapi*.include

	# convert both version files to ebcdic, they will be added according to the package name below
	for version in ./versions/$(LIBERTY_BETA_VERSION)/*.properties; do \
		mv $$version $$version.ebcdic && cat $$version.ebcdic | iconv -f ibm1047 -t iso8859-1 > $$version && rm $$version.ebcdic ; \
		chtag -t -c iso8859-1 $$version ; \
		chmod 755 $$version ; \
	done ; 
	for version in ./versions/$(LIBERTY_SERVICE_VERSION)/*.properties; do \
		mv $$version $$version.ebcdic && cat $$version.ebcdic | iconv -f ibm1047 -t iso8859-1 > $$version && rm $$version.ebcdic ; \
		chtag -t -c iso8859-1 $$version ; \
		chmod 755 $$version ; \
	done ;
# pax up the versions directory, excluding properties files, which will be added later from the correct version directory
	pax -w -x pax -a -f $(DISTDIR)/$(PAXFILE) -s@./versions/.*properties@@ -s@./versions/$(LIBERTY_BETA_VERSION).*@@ -s@./versions/$(LIBERTY_SERVICE_VERSION).*@@ -s@\./versions@wlp/lib/versions@p ./versions/
	
# At this point an IMAGE_STAGING_DIR should exist with subdirectories for each package.  For each subdirectory, add the version 
# specific properties files, make a copy of the
# z/OS pax file, unzip the distributed zip file, update permissions and set translate on for the readable files and then pax the 
# distributed pax file with the z/OS pax file and create a install file that contains the whole pax file

	if [ -d $(IMAGE_STAGING_DIR) ]; then \
		for subdir in `ls -lDx $(IMAGE_STAGING_DIR)`; do \
			cp $(DISTDIR)/$(PAXFILE) $(IMAGE_STAGING_DIR)/$(PAXFILE); \
			case $$subdir in \
			*beta*|*bluemix*) \
				pax -w -x pax -a -f $(IMAGE_STAGING_DIR)/$(PAXFILE) -s@\./versions/$(LIBERTY_BETA_VERSION)@wlp/lib/versions@p ./versions/$(LIBERTY_BETA_VERSION)/*properties; \
				;; \
			*) \
				pax -w -x pax -a -f $(IMAGE_STAGING_DIR)/$(PAXFILE) -s@\./versions/$(LIBERTY_SERVICE_VERSION)@wlp/lib/versions@p ./versions/$(LIBERTY_SERVICE_VERSION)/*properties; \
				;; \
			esac; \
			for zip in $(IMAGE_STAGING_DIR)/$$subdir/*.zip; do \
				if [ -f $$zip ]; then \
					unzip $$zip -d `dirname $$zip` && rm -f $$zip; \
					find $(IMAGE_STAGING_DIR)/$$subdir/ -name '*.xml' -o -name '*.TXT' -o -name '*.txt' -o -name '*.properties' -o -name '*.blst' -o -name '*.mf' -o -name '*.bat' -o -name '*.py' -o -name 'README' | xargs chtag -t -c iso8859-1 ; \
					find $(IMAGE_STAGING_DIR)/$$subdir/ -type d | xargs chmod ugo+rx ; \
					find $(IMAGE_STAGING_DIR)/$$subdir/wlp/bin -type f | while read script; do \
					    case `basename $$script` in \
						*.bat) \
							chtag -t -c iso8859-1 $$script; \
							;; \
						*.*) \
							;; \
						*) \
							mv $$script $$script.ascii && cat $$script.ascii | iconv -f iso8859-1 -t ibm1047 > $$script && rm $$script.ascii; \
							chtag -t -c IBM-1047 $$script; \
							chmod 755 $$script; \
							;; \
						esac; \
					done; \
				fi; \
			done ; \
		    cat install/install-logic.sh > $(DISTDIR)/install.sh; \
			pax -w -x pax -a -f $(IMAGE_STAGING_DIR)/$(PAXFILE) -s@$(IMAGE_STAGING_DIR)/$$subdir/@@p $(IMAGE_STAGING_DIR)/$$subdir/* ; \
			case $$subdir in \
			liberty) \
				cat $(IMAGE_STAGING_DIR)/$(PAXFILE) | uuencode -m - >> $(DISTDIR)/install.sh; \
				mv  $(DISTDIR)/install.sh $(DISTDIR)/wlp-install.sh; \
				mv $(IMAGE_STAGING_DIR)/$(PAXFILE) $(DISTDIR)/wlp-zosimage.pax; \
		        ;; \
		    zos-core) \
    	        if [ -z "$(LIBERTY_BETA_VERSION)" -o -z "$(LIBERTY_SERVICE_VERSION)"]; then; \
	                echo "skipping pax in sandbox build"; \
	            else; \
	                pax -r -ppx -f ${IMAGE_STAGING_DIR}/${PAXFILE} -s@wlp/@im_install_temporary_extract_directory/@p; \
	                cd im_install_temporary_extract_directory; pax -w -x pax -a -f ${DISTDIR}/${IMPAXFILE} *; \
	                rm -rf im_install_temporary_extract_directory; \
    	        fi;	\
    	        ;; \
		    zos-kernel) \
    	        if [ -z "$(LIBERTY_BETA_VERSION)" -o -z "$(LIBERTY_SERVICE_VERSION)"]; then; \
	                echo "skipping pax in sandbox build"; \
	            else; \
	                pax -r -ppx -f ${IMAGE_STAGING_DIR}/${PAXFILE} -s@wlp/@im_install_temporary_extract_directory/@p; \
	                cd im_install_temporary_extract_directory; pax -w -x pax -a -f ${DISTDIR}/${KERNELIMPAXFILE} *; \
	                rm -rf im_install_temporary_extract_directory; \
    	        fi;	\
    	        ;; \
			*) \
				cat $(IMAGE_STAGING_DIR)/$(PAXFILE) | uuencode -m - >> $(DISTDIR)/install.sh; \
				mv $(DISTDIR)/install.sh $(DISTDIR)/wlp-$$subdir-install.sh; \
				mv $(IMAGE_STAGING_DIR)/$(PAXFILE) $(DISTDIR)/wlp-$$subdir.pax; \
				echo "Created pax: wlp-$$subdir.pax"; \
				;; \
			esac; \
			if [ $(test_system) ]; then \
				(echo user mstone1 m00ntest; echo bin; echo lcd $(DISTDIR); echo put wlp-$$subdir-$(BUILD_LEVEL).pax) | ftp -n $(test_system); \
			fi; \
		done; \
	else \
		cat install/install-logic.sh > $(DISTDIR)/install.sh; \
		cat $(DISTDIR)/$(PAXFILE) | uuencode -m - >> $(DISTDIR)/install.sh; \
	fi
# and make the zip from the pax (if we have all the files - we won't if we're testing on aq)
	if [ -f $(DISTDIR)/wlp-zosimage.pax ]; then \
		mkdir -p $(DISTDIR)/zip ; \
		cd $(DISTDIR)/zip/; \
		pax -r -o noext -ppx -f $(DISTDIR)/wlp-zosimage.pax ; \
		mv $(DISTDIR)/extattr.properties $(DISTDIR)/zip/wlp/lib/native/zos/s390x/ ; \
		zip -r $(DISTDIR)/$(PAXFILE).zip * ; rm -r $(DISTDIR)/zip/ ; \
	fi
	
	touch $(DEPDIR)/changes

#-----------------------------------------------------------------------
# Builds a unit test (this does not pretend to be an official unit
# test target or the official way to make a unit test, just a part
# that you could use to unit test your code in a sandbox if you
# wanted to).
#-----------------------------------------------------------------------
unit: server_unittest.o server_tracing_functions.o mvs_utils.o
	xlc -F $(BLD_CONFIG_DIR)/xlc.c.cfg $(LINK_LISTING) -q64 -qxplink -o bbgzunit $^

#-----------------------------------------------------------------------
# Builds unit test modules for metal-C code, bbgznut, LE c code, bbgznut_lec, and LE C++, bbgznut_lecpp.
# This is a little closer to an official unit test target than unit: above, but it 
# still does a bit of pretending. See include/CuTest.h for instructions how to create,
# build, and run native unit tests.
#-----------------------------------------------------------------------
test: 
	$(MAKE) -C server bbgzlcmc bbgzshrc bbgzzfat bbgznut_lec bbgznut_lecpp bbgznut
	mkdir -p $(DISTDIR) 
	rm -f $(DISTDIR)/bbgznut.pax 
	pax -w -x pax -a -f $(DISTDIR)/bbgznut.pax server/bbgznut server/bbgzlcmc server/bbgzshrc server/bbgzzfat server/bbgznut_lec server/bbgznut_lecpp
	cat install/install-unit-test-logic.sh > $(DISTDIR)/install-unit-test.sh; 
	echo "BBGZNUT_PAYLOAD:" | cat >> $(DISTDIR)/install-unit-test.sh; 
	cat $(DISTDIR)/bbgznut.pax | uuencode -m - >> $(DISTDIR)/install-unit-test.sh; 
	echo 
	echo " !!! NOTE !!! See include/CuTest.h for instructions on how to create, build, and run native unit tests"
	echo

#-----------------------------------------------------------------------
# Prep and cleanup targets
#-----------------------------------------------------------------------
prep:
	mkdir -p $(DSECT_HDR_DIR)
	mkdir -p $(NLSPROPS_HDR_DIR)
	mkdir -p $(DEPDIR)
	if [ $(angel_generate_ver) ]; then touch $(ANGEL_GENERATE_VER_FILE_LIST); fi
	chmod ug+rx bldtools/bin/*

$(DSECT_TARGETS): | prep

dsects: $(DSECT_TARGETS)

#-----------------------------------------------------------------------
# Processing for translated NLSPROPS files doesn't work right now, and
# does odd things to the build output for terminals that don't support
# these character sets.  Try to limit the NLSPROPS processing for now by
# excluding files that end in _XX.nlsprops.  This will have the side
# effect of ignoring any new NLSPROPS file which happens to end with an
# underscore plus to characters.
#
# NLSPROPS_FILES = $(shell find ${NLSPROPS_SRC_DIR} -name '*.nlsprops')
#-----------------------------------------------------------------------
NLSPROPS_FILES = $(shell find ${NLSPROPS_SRC_DIR} -name '*.nlsprops' -a ! -name '*_??.nlsprops')
STEMS = $(shell echo ${NLSPROPS_FILES} | sed 's/\.nlsprops//g')

nlsprops: prep
	for stem in ${STEMS}; do \
		if [ $$stem.cat -ot $$stem.nlsprops ]; then \
			update=$$update" "$$stem.nlsprops; \
		fi; \
	done; \
	${BLD_TOOLS_DIR}/bin/build_headers.sh `pwd` ${NLSPROPS_HDR_DIR} `echo ${NLSPROPS_FILES} | sed 's/\.h/\.nlsprops/'`; \
	${BLD_TOOLS_DIR}/bin/build_msgcat.sh `pwd` $$update;

scanc: metalc_parse_listing.o
	xlc -F $(BLD_CONFIG_DIR)/xlc.c.cfg $(LINK_LISTING) -q64 -qxplink -o bbgzscan $^
	chmod 755 bbgzscan

clean:    
	for dir in $(SUBDIRS); do $(MAKE) -C $$dir clean; done
	rm -Rf $(DEPDIR) $(DISTDIR) $(DSECT_HDR_DIR) $(NLSPROPS_HDR_DIR) $(IMAGE_STAGING_DIR) $(PRODUCT_LOAD_MODULES)
	find ${NLSPROPS_SRC_DIR} -name '*.cat' -type f -print | xargs rm -f; 
	rm -f *.o *.alst *.clst *.llst *.i *.x *.so
	ls -1 *.s 2>/dev/null | sed 's/\(.*\).s/\1/' | while read LINE; do if [ -e $$LINE.mc -a -e $$LINE.s ]; then rm $$LINE.s; fi done

subdirs: $(SUBDIRS)

$(SUBDIRS): dsects nlsprops scanc
	$(MAKE) -C $@

.PHONY: all link prep clean dsects nlsprops pax subdirs $(SUBDIRS)

#-----------------------------------------------------------------------
# Special targets
#-----------------------------------------------------------------------
.SILENT: prep clean

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

