/*
 * datapath_util.c
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

#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include <arpa/inet.h>
#include <sys/socket.h>

#include "onep_core_services.h"
#include "onep_constants.h"
#include "onep_common_types.h"

#define ONEP_IPADDR_SIZE 50

/*
 * This is a utility file with helfer funtions for DatapathTutorial.c
 */

static char *ne_address     = NULL;
static char *username ;
static char *password ;
static char *interface ;
static char *protocol   =    "256";
char ip[ONEP_IPADDR_SIZE];
onep_status_t          rc = ONEP_OK;
static char *transport_type = "tls";
// END SNIPPET: c_global
static char *client_cert_path   = NULL;
static char *client_key_path    = NULL;
static char *key_passphrase     = NULL;
static char *root_cert_path     = NULL;

void
 process_cmd_line_arguments (int argc, char **argv)
 {
	 static const struct option options[] = {
	                               {"address", required_argument, 0, 'a'},
	                               {"username", required_argument, 0, 'u'},
	                               {"password", required_argument, 0, 'p'},
	                               {"interface", required_argument, 0, 'i'},
	                               {"protocol", required_argument, 0, 'r'},
								   {"transport", required_argument,  0,  't'},
								   {"clientcert", required_argument,  0,  'C'},
								   {"clientkey", required_argument,  0,  'K'},
								   {"passphrase", required_argument,  0,  'P'},
        						   {"rootcert", required_argument,  0,  'R'},
	                               {0, 0, 0, 0}
	  };

	  int c, option_index;
	  int usage = 0;
	  int filec = 0;
	  char *filev[41];  /* Support up to 20 input params. */
	  FILE *fp;
	  char farg[40];
	  char fval[40];

	  if (argc <= 1) {  /* No parameters were entered on command line */
		memset(filev, 0, sizeof(char *) * 40);
		fp = fopen("./tutorial.properties", "r");
		if (fp == NULL) {
			fprintf(stderr,
					"The file \"tutorial.properties\" could not be read.\n");
			exit(EXIT_FAILURE);
		}

		if (argv[0] != NULL) {
			filev[0] = strdup(argv[0]);
			filec = 1;
		}

		/* Read options from properties file. */
		while (fscanf(fp, "%40s %40s", farg, fval) == 2) {
			filev[filec] = malloc(sizeof(farg));
			if (filev[filec] != NULL) {
				strncpy(filev[filec], farg, sizeof(farg));
				if (sizeof(farg) > 0) {
					filev[filec][sizeof(farg) - 1] = '\0';
				}
				filec++;
			}
			filev[filec] = malloc(sizeof(fval));
			if (filev[filec] != NULL) {
				strncpy(filev[filec], fval, sizeof(fval));
				if (sizeof(fval) > 0) {
					filev[filec][sizeof(fval) - 1] = '\0';
				}
				filec++;
			}
		}

		if (fclose(fp) == EOF) {
			fprintf(stderr,
					"Error in closing the file \"tutorial.properties\": %d",
					errno);
        }
	 }

	/*
	 * options:
	 *       -a, --address <x.x.x.x>
	 *       -u, --username <username>
	 *       -p, --password <password>
	 *		 -i, --interface <Ethernet1/2>
	 *		 -r, --protocol	 <acl protocol>
  	 *       -t, --transport <transport type>
     *       -C, --clientcert <client certificate file>
     *       -K, --clientkey <client private key file>
     *       -P, --passphrase <client private key passphrase>
     *       -R, --rootcert <root certificates file>
	 */
	while (1) {
		if (filec > 0) {
			c = getopt_long(filec, filev, "a:u:p:i:r:t:C:K:P:R:", options, &option_index);
		} else {
			c = getopt_long(argc, argv, "a:u:p:i:r:t:C:K:P:R:", options, &option_index);
		}
		if (c == -1) break;

		switch (c) {
			case 'a': ne_address = optarg;
					  break;
			case 'u': username = optarg;
					  break;
			case 'p': password = optarg;
					  break;
			case 'i': interface = optarg;
					  break;
			case 'r': protocol = optarg;
					  break;
		    case 't': transport_type = optarg;
					  break;
			case 'C': client_cert_path = optarg;
					  break;
			case 'K': client_key_path = optarg;
					  break;
			case 'P': key_passphrase = optarg;
					  break;
			case 'R': root_cert_path = optarg;
		              break;
			default:  usage = 1;
					  break;
		}
	}

	if (!ne_address || !username || !password || !interface) {
		usage = 1;
	}

	if (usage) {
		fprintf(stderr,
		"Usage: %s -a <element address> -u <username> -p <password> -i <interface> \n"
		"[-r <protocol>]"
		"[-t <transport_type>] [-C <client cert file>] "
        "[-K <client private key file>] [-P <client private key passphrase>] "
        "[-R <root certificates file>]\n", argv[0]);
		exit(1);
	}
 }

 /**
  * Disconnects the application from the network element.
  *
  * @param [in,out] ne  Address to the network_element_t pointer to be destroyed.
  * @param [in,out] session_handle  Address to the session_handle_t pointer
  *                                 to be destroyed as returned from
  *                                 onep_element_connect().
  */
 void
 disconnect_network_element (network_element_t **ne,
                             session_handle_t **session_handle)
 {
     network_application_t* myapp = NULL;
     onep_status_t rc;

     if ((ne) && (*ne)) {
         /* Done with Network Element, disconnect it. */
         rc = onep_element_disconnect(*ne);
         if (rc != ONEP_OK) {
             fprintf(stderr, "\nFail to disconnect network element:"
                     " errocode = %d, errormsg = %s",
                      rc, onep_strerror(rc));
         }
         /* Free the network element resource on presentation. */
         rc = onep_element_destroy(ne);
         if (rc != ONEP_OK) {
             fprintf(stderr, "\nFail to destroy network element:"
                     " errocode = %d, errormsg = %s",
                      rc, onep_strerror(rc));
         }
     }
     /* Free the onePK resource on presentation. */
     if (session_handle) {
         rc = onep_session_handle_destroy(session_handle);
         if (rc != ONEP_OK) {
             fprintf(stderr, "\nFail to destroy session handle:"
                     " errocode = %d, errormsg = %s",
                      rc, onep_strerror(rc));
         }
     }
     /* Gets the singleton instance of network_application_t. */
     rc = onep_application_get_instance(&myapp);
     if (rc != ONEP_OK) {
         fprintf(stderr, "\nFail to get the instance of the application:"
                 " errocode = %d, errormsg = %s",
                  rc, onep_strerror(rc));
     }
     if (myapp) {
         /* Destroys the network_application_t and frees its memory resource. */
         rc = onep_application_destroy(&myapp);
         if (rc != ONEP_OK) {
             fprintf(stderr, "\nFail to destroy application:"
                     " errocode = %d, errormsg = %s",
                      rc, onep_strerror(rc));
         }
     }
 }

 /**
  * Creates an instance of session_config_t with the given transport mode.
  *
  * @param [in]  mode    Transport type to use for the session.
  * @param [out] config  Address of the pointer to the session_config_t
  *                      to be created.
  *
  * @retval ONEP_OK  In the case of success. Otherwise, a onep_status_t error
  *                  value is returned. Calling onep_strerror() on the return
  *                  value will convert the error number into an error message.
  */
 onep_status_t
 create_session_config (onep_transport_mode_e mode, session_config_t **config)
 {
     onep_status_t rc;
     onep_status_t destroy_rc;
     session_config_t *local_config = NULL;

     /* Create a new session_config_t with the given transport mode. */
     rc = onep_session_config_new(mode, &local_config);
     if (rc != ONEP_OK) {
         fprintf(stderr, "\nFailed to construct session local_config: "
                 "errorcode = %d, errormsg = %s",
                 rc, onep_strerror(rc));
         return rc;
     }

     /* Set the port to connect to on the network element.
      * The default ports are: ONEP_SESSION_SOCKET   15001
      *                        ONEP_SESSION_TLS      15002
      *
      * Note: for ONEP_SESSION_LOCAL, this setting is ignored.
      */
     switch (mode) {
         case ONEP_SESSION_SOCKET:
             rc = onep_session_config_set_port(local_config, 15001);
             if (rc != ONEP_OK) {
                 fprintf(stderr, "\nFailed to set port: "
                         "errorcode = %d, errormsg = %s",
                         rc, onep_strerror(rc));
                 goto error_cleanup;
             }
             break;
         case ONEP_SESSION_TLS:
             rc = onep_session_config_set_port(local_config, 15002);
             if (rc != ONEP_OK) {
                 fprintf(stderr, "\nFailed to set port: "
                         "errorcode = %d, errormsg = %s",
                         rc, onep_strerror(rc));
                 goto error_cleanup;
             }
             break;
         case ONEP_SESSION_LOCAL:    /* Ignored */
             break;
         default:
             fprintf(stderr, "\nUnknown transport mode: %d", mode);
             break;
     }

     /* Set the TLS attributes of the session. */
     if (mode == ONEP_SESSION_TLS) {
         rc = onep_session_config_set_tls(
             local_config,       /* Pointer to session_config_t   */
             client_cert_path,   /* Client certificate file path  */
             client_key_path,    /* Client private key file path  */
             key_passphrase,     /* Client private key passphrase */
             root_cert_path);    /* Root certificates file path   */
         if (rc != ONEP_OK) {
             fprintf(stderr, "\nFailed to set TLS: "
                     "errorcode = %d, errormsg = %s",
                     rc, onep_strerror(rc));
             goto error_cleanup;
         }
     }

     *config = local_config;
     return ONEP_OK;

 error_cleanup:
     destroy_rc = onep_session_config_destroy(&local_config);
     if (destroy_rc != ONEP_OK) {
         fprintf(stderr, "\nFailed to destroy session config: "
                 "errorcode = %d, errormsg = %s",
                 destroy_rc, onep_strerror(destroy_rc));
     }
     return rc;
 }

 /**
  * Connects the application to a network element.
  *
  * @param [in]  ipaddr    This is the ip address of the network element.
  * @param [in]  username  Username
  * @param [in]  password  Password
  * @param [in]  app_name  Application Name
  * @param [out] ne        Address to the network_element_t pointer
  *
  * @retval NULL if a connection could not be established. Otherwise, a
  *              session_handle_t pointer is returned.
  */
 session_handle_t *
 connect_network_element (char* ipaddr, char *username, char* password,
                          char* app_name, char *transport,
                          network_element_t **ne)
 {
     network_application_t* myapp = NULL;
     network_element_t*     local_ne = NULL;
     session_handle_t*      session_handle = NULL;
     onep_status_t          rc;
     struct sockaddr_in     v4addr;
     onep_transport_mode_e  mode;
     session_config_t*      config = NULL;

     /* Obtain a network_application_t instance. */
     rc = onep_application_get_instance(&myapp);
     if (rc != ONEP_OK) {
        fprintf(stderr, "\nFailed to get network instance:"
                         " errocode = %d, errormsg = %s",
                         rc, onep_strerror(rc));
        return NULL;
     }

     /* Set the name of the network application. */
     rc = onep_application_set_name(myapp, app_name);
     if (rc != ONEP_OK) {
        fprintf(stderr, "\nFailed to get network application name:"
                         " errocode = %d, errormsg = %s",
                         rc, onep_strerror(rc));
         disconnect_network_element(NULL, NULL);
         return NULL;
     }

     /* Get the network element at the IP address. */
     memset(&v4addr, 0, sizeof(struct sockaddr_in));
     v4addr.sin_family = AF_INET;
     inet_pton(AF_INET, ipaddr, &(v4addr.sin_addr));
     rc = onep_application_get_network_element(myapp,
             (struct sockaddr *)&v4addr,
             &local_ne);
     if (rc != ONEP_OK) {
         fprintf(stderr, "\nFailed to get network element:"
                         " errocode = %d, errormsg = %s",
                         rc, onep_strerror(rc));
         disconnect_network_element(NULL, NULL);
         return NULL;
     }

     /* Create a session configuration. */
     if (strcasecmp(transport, "tcp") == 0) {
         mode = ONEP_SESSION_SOCKET;
     } else {
         mode = ONEP_SESSION_TLS;
     }
     rc = create_session_config(mode, &config);
     if (rc != ONEP_OK) {
         fprintf(stderr,
             "\ncreate_session_config failed\n\n");
         disconnect_network_element(&local_ne, NULL);
         return NULL;
     }

     /* Connect to the network element. */
     rc = onep_element_connect(
             local_ne, username, password, config, &session_handle);
     if (rc != ONEP_OK) {
         /**
          * Failed to connect to network element.
          */
         fprintf(stderr, "\nFailed to connect to network element:"
                 " errocode = %d, errormsg = %s",
                 rc, onep_strerror(rc));
         disconnect_network_element(&local_ne, NULL);
         return NULL;
     }
     *ne = local_ne;
     return session_handle;
}

 char *
 get_pwd ()
 {
     return password;
 }

 char *
 get_user ()
 {
     return username;
 }


 char *
 get_interface ()
 {
     return interface;
 }

 char *
 get_protocol ()
 {
     return protocol;
 }

 /**
  * Get the transport type for the connection to the network element.
  *
  * @return char*  transport type
  */
 char *
 get_transport_type ()
 {
     return transport_type;
 }

 /**
  * Get the path to the client certificate file.
  *
  * @return char*  client certificate path
  */
 char *
 get_client_cert_path ()
 {
     return client_cert_path;
 }

 /**
  * Get the path to the client private key file.
  *
  * @return char*  client private key path
  */
 char *
 get_client_key_path ()
 {
     return client_key_path;
 }

 /**
  * Get the passphrase the client private key file.
  *
  * @return char*  client private key passphrase
  */
 char *
 get_key_passphrase ()
 {
     return key_passphrase;
 }

 /**
  * Get the path to the root certificates file.
  *
  * @return char*  root certificates path
  */
 char *
 get_root_cert_path ()
 {
     return root_cert_path;
 }

 /**
  * Get the network element's address.
  *
  * @return char*  network element address
  */
 char *
 get_element_address ()
 {
     return ne_address;
 }

