/*
 * appl_mgmt_util.h
 *
 * Copyright (c) 2010-2013 by Cisco Systems, Inc.
 *
 * THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY
 * BY CISCO SOLELY FOR THE PURPOSE of PROVIDING PROGRAMMING EXAMPLES.
 * CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY
 * APPLICATION.
 *
 * Redistribution and use of the sample code, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * Redistributions of source code must retain the above disclaimer.
 */

#include "onep_core_services.h"
#include "onep_constants.h"

session_handle_t *
 connect_network_element (char* ipaddr, char *username, char* password,
                          char* app_name, char *transport,
                          network_element_t **ne);
char *
 get_element_address (void);

char *
get_user (void);

char *
get_pwd (void);

char *
get_interface (void);

char *
get_protocol (void);

void
process_cmd_line_arguments (int argc, char* argv[]);

char *
 get_transport_type (void);

void
disconnect_network_element (network_element_t **ne,
                            session_handle_t **session_handle);

