#------------------------------------------------------------------
# Makefile - Make rules for ONE-P Sample applications
#
# Copyright (c) 2011-2012 by cisco Systems, Inc.
# All rights reserved.
#------------------------------------------------------------------

ONEP_SDK ?=/opt/cisco/onep/c64/sdk-c64-1.0.0.84/c/
JAVA_HOME ?=/usr/lib/jvm/java-1.7.0-openjdk-amd64
DEFAULT_LIB_INSTALL_PATH ?=/opt/cisco/onep/c64/sdk-c64-1.0.0.84/c/


# --------------------------------------
# Build 32 bit as a default
# To build 64 bit, update LBITS to 64
# for setting some FLAGS
# --------------------------------------
LBITS ?=64
ifeq ($(LBITS),64)
    # compile 64 bit
    ONEP_LBITS=64
else
    # compile 32 bit
    ONEP_LBITS=32
endif

# ----------------
# Common GCC Variables
# ----------------
CC ?= gcc

CC_BLDFLAGS := \
    -Wall -c -fPIE -O1 -D_FORTIFY_SOURCE=2 -m${ONEP_LBITS}

CC_INCDIRS := \
    -I${ONEP_SDK}/c/include \
    -I${JAVA_HOME}/include

LD_LIBDIRS := \
    -L${ONEP_SDK}/c/lib

LD_LIBS := \
    -lonep${ONEP_LBITS}_core \
    -lonep${ONEP_LBITS}_datapath 

LD_FLAGS := \
    -m${ONEP_LBITS} -pie -shared -fPIC -Wl,-soname,libProgramNode.so

# Objects file to build
OBJSNETAPP += \
	obj/ProgramNode.o \
	      
# All Target
# To build DatapathTutorial: "make bin/DatapathTutorial"
all: makedirs bin/libProgramNode.so 

# Add inputs and outputs from these tool invocations to the build variables 

makedirs:
	@if [ ! -d obj ] ; then mkdir obj ; fi
	@if [ ! -d bin ] ; then mkdir bin ; fi

# Tool invocations
bin/libProgramNode2.so: makedirs $(OBJSNETAPP)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	$(CC) -o $@ $(OBJSNETAPP) $(LD_FLAGS) \
			$(LD_LIBDIRS) $(LD_LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

bin/libProgramNode.so: makedirs $(OBJSNETAPP)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	$(CC) -shared  -fPIC -o $@ $(OBJSNETAPP)\
			$(ONEP_SDK)/c/lib/libonep64_core.so \
			$(ONEP_SDK)/c/lib/libonep64_datapath.so
	@echo 'Finished building target: $@'
	@echo ' '
	
# Other Targets
clean:
	-$(RM) -rf obj bin 
	-@echo ' '

obj/%.o: %.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	$(CC) $(CC_INCDIRS) $(CC_BLDFLAGS) -MMD -MP \
		-MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

.PHONY: all clean dependents
.SECONDARY:

