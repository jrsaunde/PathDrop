/*
 *------------------------------------------------------------------
 * onePK SDK Tutorials
 *
 * DatapathTutorial.c
 *
 * Copyright (c) 2012-2013 by Cisco Systems, Inc.
 *
 * THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY
 * BY CISCO SOLELY FOR THE PURPOSE of PROVIDING PROGRAMMING EXAMPLES.
 * CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY
 * APPLICATION.
 *
 * Redistribution and use in source or binary forms, with or without
 * modification, is subject to the terms and conditions of the Cisco onePK
 * Software Development Kit License Agreement (onePK SDK Internal User License).
 *------------------------------------------------------------------
 */


/*
 * This tutorial demonstrates onepk Datapath Service Set.
 * This tutorial will show you how to to hook in to the packet
 * flow through a Cisco switch or router and extract packets from
 * that flow of packets.
 *
 * There are some pre-requisites to running this tutorial which are mentioned in
 * the README file of this tutorial
 *

 */

#include <limits.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/un.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <netinet/in.h>
#include <netinet/udp.h>
#include <netinet/ip.h>
#include <unistd.h>
#include <inttypes.h>
#include <time.h>

#include "onep_core_services.h"
#include "policy.h"
#include "datapath_policy.h"
#include "class.h"
#include "filter.h"
#include "onep_dpss_packet_delivery.h"
#include "onep_types.h"
#include "onep_dpss_actions.h"
#include "onep_dpss_pkt.h"
#include "onep_dpss_flow.h"
#include "onep_dpss_callback_framework.h"
#include "include/programnode_util.h"
#define FAIL false
#define SUCCESS true
#define PROTOCOL_MAX_LEN (20)
#define INTERFACE_MAX_LEN (40)

// START SNIPPET: c_variables
static class_t* acl_class;
static filter_t* acl_filter;

static class_t* acl_class_in;
static filter_t* acl_filter_in;
static class_t* acl_class_out;
static filter_t* acl_filter_out;
static interface_filter_t* intf_filter = NULL;
static onep_collection_t*  intfs = NULL;
static unsigned int        count = 0;
static network_element_t*  ne = NULL;
static onep_status_t       rc;
static onep_username       user;
static onep_password       pwd;
static onep_if_name        intf_name;
static int proto;
char* transporttype;

typedef struct list{
	struct list* next;
	struct list* previous;
	uint16_t data;//16 bit ip header identification field
	time_t timestamp;
} List;

List *root;
int timeout = 10; //how long we wait before declaring a packet loss

void add_to_end(List *list, uint16_t _data, time_t _timestamp){
	List* last = list;
	while (1){
		if(last->next == NULL){
			break;
		}else{
			last = last->next;
		}
	}
	last->next = malloc(sizeof(List));
	last->next->previous = last;
	last->next->data = _data;
	last->next->timestamp = _timestamp;
	last->next->next = NULL;
	printf("added packet with id %d to end at time %ld\n", _data, _timestamp);
}

void print_list(List *list){
	if(list == NULL){
		printf("List is empty\n");
		return;
	}
	while(list->next != NULL){
		printf("%d\n", list->data);
		list = list->next;
	}
	printf("%d\n", list->data);
}

int search_and_remove(List *list, uint16_t _data){
	if(list == NULL)return 0;
	if(list->data == _data){
		root = list->next;
		free(list);
		return 1;
	}
	while(1){
		if(list->next == NULL){
			if(list->data == _data){
				return 1;
			}else{
				if(time(NULL) - list->timestamp > timeout) printf("PACKET LOSS\n");
				return 0;
			}
		}
		if(list->next->data == _data){
			List *temp = list->next;
			list->next = list->next->next;
			//time_t travel_time = time(NULL) - temp->next->timestamp;
			printf("found and removed packet with id %d\n", _data);
			free(temp);
			return 1;
		}else{
			if(time(NULL) - list->timestamp > timeout) printf("PACKET LOSS\n");
			list = list->next;
		}
	}
}

// END SNIPPET: c_variables

// START SNIPPET: callback_info
/*
 * Extract the IP version from a packet.
 */
onep_status_t dpss_tutorial_get_ip_version(struct onep_dpss_paktype_ *pakp,
    char *ip_version) {

    onep_status_t rc;
    uint16_t l3_protocol;
    char l3_prot_sym = 'U';

    /* Get packet L3 protocol. */
    rc = onep_dpss_pkt_get_l3_protocol(pakp, &l3_protocol);
    if( rc == ONEP_OK ) {
        if( l3_protocol == ONEP_DPSS_L3_IPV4 ) {
            l3_prot_sym = '4';
        } else if( l3_protocol == ONEP_DPSS_L3_IPV6 ) {
            l3_prot_sym = '6';
        } else if( l3_protocol == ONEP_DPSS_L3_OTHER ) {
            l3_prot_sym = 'N';
        } else {
            l3_prot_sym = 'U';
        }
    } else {
        fprintf(stderr, "Error getting L3 protocol. code[%d], text[%s]\n", rc, onep_strerror(rc));
        return (rc);
    }
    *ip_version = l3_prot_sym;
    return (ONEP_OK);
}


/*
 * Extract IP addressing and port information from the packet.
 */
onep_status_t dpss_tutorial_get_ip_port_info(
    struct onep_dpss_paktype_ *pakp, char **src_ip, char **dest_ip,
    uint16_t *src_port, uint16_t *dest_port, char *prot, char ip_version, u_int16_t *pkt_id) {

    onep_status_t   rc;
    uint8_t         l4_protocol;
    uint8_t         *l3_start;
    struct iphdr    *l3hdr;
    uint8_t         *l4_start;
    struct tcphdr   *l4tcp;
    struct udphdr   *l4udp;

    uint8_t 		*payload_start;

    onep_dpss_pkt_get_payload(pakp, &payload_start);

    if( ip_version == '4' ) {
    	/*get payload */
    	/* get IPv4 header */
        rc = onep_dpss_pkt_get_l3_start(pakp, &l3_start);
        if( rc==ONEP_OK ) {
            l3hdr = (struct iphdr *)l3_start; // convert to iphdr
            *src_ip = strdup(inet_ntoa( *(struct in_addr *)&(l3hdr->saddr) ));
            *dest_ip = strdup(inet_ntoa( *(struct in_addr *)&(l3hdr->daddr) ));
            *pkt_id = ntohs(l3hdr->id);
        } else {
            fprintf(stderr,"Error getting IPv4 header. code[%d], text[%s]\n", rc, onep_strerror(rc));
            return (ONEP_ERR_SYSTEM);
        }
    } else if( ip_version == '6' ) {
        fprintf(stderr, "Cannot get IPv6 traffic at this time.\n");
        return (ONEP_ERR_SYSTEM);
    } else if( ip_version == 'N' ) {
        fprintf(stderr, "IP address is neither IPv4 nor IPv6.\n");
        return (ONEP_ERR_SYSTEM);
    } else {
        fprintf(stderr, "Unknown IP version.\n");
        return (ONEP_ERR_SYSTEM);
    }

    /* get L4 header */
    rc = onep_dpss_pkt_get_l4_start(pakp, &l4_start);
    if( rc != ONEP_OK ) {
        fprintf(stderr, "Error getting L4 header. code[%d], text[%s]\n", rc, onep_strerror(rc));
        return (rc);
    }

    /* get packet L4 protocol */
    rc = onep_dpss_pkt_get_l4_protocol(pakp, &l4_protocol);
    if( rc == ONEP_OK ) {
        if( l4_protocol == ONEP_DPSS_TCP_PROT ) {
            /* TCP */
            strcpy(prot,"TCP");
            l4tcp = (struct tcphdr *)l4_start;
            *src_port = ntohs( l4tcp->source );
            *dest_port = ntohs( l4tcp->dest );
        }
        else if( l4_protocol == ONEP_DPSS_UDP_PROT ) {
            /* UDP */
            strcpy(prot,"UDP");
            l4udp = (struct udphdr *)l4_start;
            *src_port = ntohs( l4udp->source );
            *dest_port = ntohs( l4udp->dest );
        }
        else if( l4_protocol == ONEP_DPSS_ICMP_PROT ) {
            strcpy(prot,"ICMP");
        }
        else if( l4_protocol == ONEP_DPSS_IPV6_ENCAPSULATION_PROT ) {
            // sends IPV6 packet as payload of IPV4
            strcpy(prot,"ENCP"); // IPV6 encapsulated on IPV4
        }
        else {
            strcpy(prot,"UNK!"); // Unknown!
        }
    }
    else {
        fprintf(stderr, "Error getting L4 protocol. code[%d], text[%s]\n", rc, onep_strerror(rc));
    }
    return (ONEP_OK);
}


/*
 * Extract some flow state given a packet and a FID.
 */
void dpss_tutorial_get_flow_state(struct onep_dpss_paktype_ *pakp,
    onep_dpss_flow_ptr_t fid, char *l4_state_char ) {

    onep_status_t             rc;
    onep_dpss_l4_flow_state_e l4_state;

    rc = onep_dpss_flow_get_l4_flow_state(pakp,&l4_state);
    if( rc==ONEP_OK ) {
        if( l4_state == ONEP_DPSS_L4_CLOSED ) {
            strcpy(l4_state_char,"CLOSED");
        } else if( l4_state == ONEP_DPSS_L4_OPENING ) {
            strcpy(l4_state_char,"OPENING");
        } else if( l4_state == ONEP_DPSS_L4_UNI_ESTABLISHED ) {
            strcpy(l4_state_char,"UNI-ESTABLISHED");
        } else if( l4_state == ONEP_DPSS_L4_UNI_ESTABLISHED_INCORRECT ) {
            strcpy(l4_state_char,"UNI-ESTABLISHED INCORRECT");
        } else if( l4_state == ONEP_DPSS_L4_BI_ESTABLISHED ) {
            strcpy(l4_state_char,"BI-ESTABLISHED");
        } else if( l4_state == ONEP_DPSS_L4_BI_ESTABLISHED_INCORRECT ) {
            strcpy(l4_state_char,"BI-ESTABLISHED INCORRECT");
        } else if( l4_state == ONEP_DPSS_L4_CLOSING ) {
            strcpy(l4_state_char,"CLOSING");
        } else {
            strcpy(l4_state_char,"!UNKNOWN!");
        }
    } else {
        fprintf(stderr, "Error getting L4 state of flow. code[%d], text[%s]\n", rc, onep_strerror(rc));
    }
    return;
}


/* MY CALLBACK FUNCTION */
void out_packet_drop_callback( onep_dpss_traffic_reg_t *reg, struct onep_dpss_paktype_ *pak, void *client_context, bool *return_packet){
		onep_status_t        rc;
	    onep_dpss_fid_t      fid;
	    uint16_t             src_port = 0;
	    uint16_t             dest_port = 0;
	    char                 *src_ip = NULL;
	    char                 *dest_ip = NULL;
	    char                 l4_protocol[5];
	    char                 l4_state[30];

	    /* TODO: MY VARS */
	    uint16_t			pkt_id = 0;
	    network_interface_t* input_int;
	    network_interface_t* output_int;
	    onep_if_name 		 input;
	    onep_if_name 		 output;
	    bool				 is_initiator = false;
	    char				 direction[5];


	    /* END MY VARS */

	    strcpy(l4_protocol,"ERR");
	    strcpy(l4_state,"ERR");


	    rc = onep_dpss_pkt_get_flow(pak, &fid);
	    if( rc == ONEP_OK ) {
	    	dpss_tutorial_get_ip_port_info(pak, &src_ip, &dest_ip, &src_port, &dest_port, l4_protocol, '4', &pkt_id);
	    	dpss_tutorial_get_flow_state(pak, fid, l4_state);

	        /*TODO: MY CODE */

	        //Set direction
	        strcpy(direction, "<---");
	        //Get input and output interface of packet
	        onep_dpss_pkt_get_input_interface(pak, &input_int);
	        onep_dpss_pkt_get_output_interface(pak, &output_int);

	        //Get names of interfaces
	        onep_interface_get_name(input_int, input);
	        onep_interface_get_name(output_int, output);

	        //printf("%d", is_initiator);
	        //Which side of the flow did the packet come from?
	        onep_dpss_pkt_is_initiator(pak, &is_initiator);


	        if(is_initiator){
	        	strcpy(direction, "--->");
	        }
	        /* END MY CODE */


	    } else {
	        fprintf(stderr, "Error getting flow ID. code[%d], text[%s]\n", rc, onep_strerror(rc));
	    }
//	    printf(
//	        "\n"
//	        "O| FID | Source                 |  Port | Destination     |  Port | Prot | Pkt# | State                     | Input Int          | Output Int         \n");
//	    printf(
//	    	" | %-3lu | %-15s : %-5d | %-15s : %-5d | %-4s | %-4d | %-25s | %-18s | %-18s |\n\n",
//	      fid, src_ip, src_port, dest_ip, dest_port, l4_protocol, pkt_id, l4_state, input, output);

	    printf("\n"
	    		"Out - %-4d | %-18s | %-15s (%-5d) --> %-15s (%-5d)\n", pkt_id, output, src_ip, src_port, dest_ip, dest_port);
	    search_and_remove(root, pkt_id);
	    free(src_ip);
	    free(dest_ip);
	    return;
}

void in_packet_drop_callback( onep_dpss_traffic_reg_t *reg, struct onep_dpss_paktype_ *pak, void *client_context, bool *return_packet){
		onep_status_t        rc;
	    onep_dpss_fid_t      fid;
	    uint16_t             src_port = 0;
	    uint16_t             dest_port = 0;
	    char                 *src_ip = NULL;
	    char                 *dest_ip = NULL;
	    char                 l4_protocol[5];
	    char                 l4_state[30];

	    /* TODO: MY VARS */
	    uint16_t			pkt_id = 0;
	    network_interface_t* input_int;
	    network_interface_t* output_int;
	    onep_if_name 		 input;
	    onep_if_name 		 output;
	    bool				 is_initiator = false;
	    char				 direction[5];


	    /* END MY VARS */

	    strcpy(l4_protocol,"ERR");
	    strcpy(l4_state,"ERR");

	    rc = onep_dpss_pkt_get_flow(pak, &fid);
	    if( rc == ONEP_OK ) {
	    	dpss_tutorial_get_ip_port_info(pak, &src_ip, &dest_ip, &src_port, &dest_port, l4_protocol, '4', &pkt_id);
	    	dpss_tutorial_get_flow_state(pak, fid, l4_state);

	        /*TODO: MY CODE */

	        //Set direction
	        strcpy(direction, "<---");
	        //Get input and output interface of packet
	        onep_dpss_pkt_get_input_interface(pak, &input_int);
	        onep_dpss_pkt_get_output_interface(pak, &output_int);

	        //Get names of interfaces
	        onep_interface_get_name(input_int, input);
	        onep_interface_get_name(output_int, output);

	        //Which side of the flow did the packet come from?
	        onep_dpss_pkt_is_initiator(pak, &is_initiator);

	        if(is_initiator){
	        	strcpy(direction, "--->");
	        }
	        /* END MY CODE */


	    } else {
	        fprintf(stderr, "Error getting flow ID. code[%d], text[%s]\n", rc, onep_strerror(rc));
	    }
//	    printf(
//	        "\n"
//	        "I| FID | Source                 |  Port | Destination     |  Port | Prot | Pkt# | State                     | Input Int          | Output Int         \n");
//	    printf(
//	    	" | %-3lu | %-15s : %-5d | %-15s : %-5d | %-4s | %-4d | %-25s | %-18s | %-18s |\n\n",
//	      fid, src_ip, src_port, dest_ip, dest_port, l4_protocol, pkt_id, l4_state, input, output);

	    printf("\n"
	    		"In  - %-4d | %-18s | %-15s (%-5d) --> %-15s (%-5d)\n", pkt_id, input, src_ip, src_port, dest_ip, dest_port);
	    add_to_end(root, pkt_id, time(NULL));
	    free(src_ip);
	    free(dest_ip);
	    return;
}
/*
 * Display a list of interfaces.
 */
void dpss_tutorial_display_intf_list(onep_collection_t *intf_list, FILE *op)
{
    onep_status_t rc;
    unsigned int count;
    network_interface_t* intf;
    onep_if_name name;

    onep_collection_get_size(intf_list, &count);
    if (count>0) {
        unsigned int i;
        for (i = 0; i < count; i++) {
            rc = onep_collection_get_by_index(intf_list, i, (void *)&intf);
            if (rc==ONEP_OK) {
                rc = onep_interface_get_name(intf,name);
                fprintf(op, "[%d] Interface [%s]\n", i, name);
            } else {
            	fprintf(stderr, "Error getting interface. code[%d], text[%s]\n", rc, onep_strerror(rc));
            }
        }
    }
}

/*
 * Our method of creating an acl
 */
onep_status_t create_in_acl( network_element_t *elm, class_t **in_class){
	onep_status_t rc;
	ace_t *our_ace;
	acl_t *our_acl;
	uint16_t http_port = 80;

	/* Create the traffic class */

	// Create ACE
	rc = onep_acl_create_l3_ace(40, TRUE, &our_ace);
	    if (rc != ONEP_OK) {
	        fprintf(stderr, "Unable to create l3 ace: %s\n", onep_strerror(rc));
	        return ONEP_FAIL;
	    }
	// Set the source prefix
	rc = onep_acl_set_l3_ace_src_prefix(our_ace, NULL, 0);
		if (rc != ONEP_OK) {
			fprintf(stderr, "Unable to set source prefix: %s\n", onep_strerror(rc));
			return ONEP_FAIL;
		}
	// Set the destination prefix
	rc = onep_acl_set_l3_ace_dst_prefix(our_ace, NULL, 0);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set dest prefix: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	// Set the protocol
	rc = onep_acl_set_l3_ace_protocol(our_ace, proto);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set protocol: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	// Set the source port
	rc = onep_acl_set_l3_ace_src_port(our_ace, 0, ONEP_COMPARE_ANY);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set source port: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	// Set the destination port
	rc = onep_acl_set_l3_ace_dst_port(our_ace, http_port, ONEP_COMPARE_EQ);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set dest port: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	/* Now create the related ACL.  After creating the ACL we will add
	     * ace40 to it.
	     */
	    rc = onep_acl_create_l3_acl(AF_INET, ne, &our_acl);
	    if (rc != ONEP_OK) {
	        fprintf(stderr, "Unable to create acl: %s\n", onep_strerror(rc));
	        return ONEP_FAIL;
	    }


	    rc = onep_acl_add_ace(our_acl, our_ace);
	    if (rc != ONEP_OK) {
	        fprintf(stderr, "Unable to add ace to acl: %s\n", onep_strerror(rc));
	        return ONEP_FAIL;
	    }
	    printf("\n Added ace to acl\n");

	    /* Now that the ACL is created, we can create a class map with an
	     * ACL filter.
	     */
	    rc = onep_policy_create_class(ne, ONEP_CLASS_OPER_OR, &acl_class_in);
	    if (rc != ONEP_OK) {
	         fprintf(stderr, "Unable to create class: %s\n", onep_strerror(rc));
	         return ONEP_FAIL;
	    }

	    /* Create an acl filter containing the acl created above.
	     */
	    rc = onep_policy_create_acl_filter(our_acl, &acl_filter_in);
	    if (rc != ONEP_OK) {
	           fprintf(stderr, "Unable to create acl filter: %s\n", onep_strerror(rc));
	           return ONEP_FAIL;
	    }

	    /* Now add the ACL filter to the created acl_class.
	     */
	    rc = onep_policy_add_class_filter(acl_class_in, acl_filter_in);
	    if (rc != ONEP_OK) {
	            fprintf(stderr, "Unable to add filter to class: %s\n",
	                    onep_strerror(rc));
	            return ONEP_FAIL;
	    }

	    // END SNIPPET: get_class
	    /*
	     * Assuming we got this far, we want to return the class we created.
	     */
	    *in_class = acl_class_in;

	    return (ONEP_OK);
}

onep_status_t create_out_acl( network_element_t *elm, class_t **in_class){
	onep_status_t rc;
	ace_t *our_ace;
	acl_t *our_acl;
	uint16_t http_port = 80;

	/* Create the traffic class */

	// Create ACE
	rc = onep_acl_create_l3_ace(50, TRUE, &our_ace);
	    if (rc != ONEP_OK) {
	        fprintf(stderr, "Unable to create l3 ace: %s\n", onep_strerror(rc));
	        return ONEP_FAIL;
	    }
	// Set the source prefix
	rc = onep_acl_set_l3_ace_src_prefix(our_ace, NULL, 0);
		if (rc != ONEP_OK) {
			fprintf(stderr, "Unable to set source prefix: %s\n", onep_strerror(rc));
			return ONEP_FAIL;
		}
	// Set the destination prefix
	rc = onep_acl_set_l3_ace_dst_prefix(our_ace, NULL, 0);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set dest prefix: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	// Set the protocol
	rc = onep_acl_set_l3_ace_protocol(our_ace, proto);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set protocol: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	// Set the source port
	rc = onep_acl_set_l3_ace_src_port(our_ace, http_port, ONEP_COMPARE_EQ);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set source port: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	// Set the destination port
	rc = onep_acl_set_l3_ace_dst_port(our_ace, 0, ONEP_COMPARE_ANY);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Unable to set dest port: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
	/* Now create the related ACL.  After creating the ACL we will add
	     * ace40 to it.
	     */
	    rc = onep_acl_create_l3_acl(AF_INET, ne, &our_acl);
	    if (rc != ONEP_OK) {
	        fprintf(stderr, "Unable to create acl: %s\n", onep_strerror(rc));
	        return ONEP_FAIL;
	    }


	    rc = onep_acl_add_ace(our_acl, our_ace);
	    if (rc != ONEP_OK) {
	        fprintf(stderr, "Unable to add ace to acl: %s\n", onep_strerror(rc));
	        return ONEP_FAIL;
	    }
	    printf("\n Added ace to acl\n");

	    /* Now that the ACL is created, we can create a class map with an
	     * ACL filter.
	     */
	    rc = onep_policy_create_class(ne, ONEP_CLASS_OPER_OR, &acl_class_out);
	    if (rc != ONEP_OK) {
	         fprintf(stderr, "Unable to create class: %s\n", onep_strerror(rc));
	         return ONEP_FAIL;
	    }

	    /* Create an acl filter containing the acl created above.
	     */
	    rc = onep_policy_create_acl_filter(our_acl, &acl_filter_out);
	    if (rc != ONEP_OK) {
	           fprintf(stderr, "Unable to create acl filter: %s\n", onep_strerror(rc));
	           return ONEP_FAIL;
	    }

	    /* Now add the ACL filter to the created acl_class.
	     */
	    rc = onep_policy_add_class_filter(acl_class_out, acl_filter_out);
	    if (rc != ONEP_OK) {
	            fprintf(stderr, "Unable to add filter to class: %s\n",
	                    onep_strerror(rc));
	            return ONEP_FAIL;
	    }

	    // END SNIPPET: get_class
	    /*
	     * Assuming we got this far, we want to return the class we created.
	     */
	    *in_class = acl_class_out;

	    return (ONEP_OK);
}
/*
 * Create an interesting classmap that we want to use to define the
 * traffic we care about processing. This is not going to be
 * parameterized. It will just be a relatively random class map for
 * now.
 *
 * @note While there is error checking on ACE, ACL, filter and class
 * creation, there is no resource freeing, so this routine can easily
 * leak memory if, say, final class creation fails.
 */
onep_status_t dpss_tutorial_create_acl( network_element_t *elem, class_t **interesting_class) {
    onep_status_t rc;
    ace_t *ace40;
    acl_t *the_acl;
    uint16_t http_port = 80;

    /* Create a class which defines what traffic the application wishes to
     * receive from a specific interface.  The traffic class is specified
     * using the Datapath Policy Service Set.
     */

    /* The first step is to create an access list entry that defines the traffic
     * that we care about.  In this case it is the equivalent of the CLI
     * "access-list 40 permit ip any any".  This object is created locally,
     * so no network element scoping is required.
     */

    // START SNIPPET: get_class

    rc = onep_acl_create_l3_ace(40, TRUE, &ace40);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to create l3 ace: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }

    rc = onep_acl_set_l3_ace_src_prefix(ace40, NULL, 0);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to set source prefix: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }

    rc = onep_acl_set_l3_ace_dst_prefix(ace40, NULL, 0);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to set dest prefix: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }
    printf("%d", proto);
    rc = onep_acl_set_l3_ace_protocol(ace40, proto);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to set protocol: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }

    rc = onep_acl_set_l3_ace_src_port(ace40, 0, ONEP_COMPARE_ANY);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to set source port: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }

    rc = onep_acl_set_l3_ace_dst_port(ace40, 0, ONEP_COMPARE_ANY);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to set dest port: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }



    /* Now create the related ACL.  After creating the ACL we will add
     * ace40 to it.
     */
    rc = onep_acl_create_l3_acl(AF_INET, ne, &the_acl);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to create acl: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }


    rc = onep_acl_add_ace(the_acl, ace40);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Unable to add ace to acl: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }
    printf("\n Added ace to acl\n");

    /* Now that the ACL is created, we can create a class map with an
     * ACL filter.
     */
    rc = onep_policy_create_class(ne, ONEP_CLASS_OPER_OR, &acl_class);
    if (rc != ONEP_OK) {
         fprintf(stderr, "Unable to create class: %s\n", onep_strerror(rc));
         return ONEP_FAIL;
    }

    /* Create an acl filter containing the acl created above.
     */
    rc = onep_policy_create_acl_filter(the_acl, &acl_filter);
    if (rc != ONEP_OK) {
           fprintf(stderr, "Unable to create acl filter: %s\n", onep_strerror(rc));
           return ONEP_FAIL;
    }

    /* Now add the ACL filter to the created acl_class.
     */
    rc = onep_policy_add_class_filter(acl_class, acl_filter);
    if (rc != ONEP_OK) {
            fprintf(stderr, "Unable to add filter to class: %s\n",
                    onep_strerror(rc));
            return ONEP_FAIL;
    }

    // END SNIPPET: get_class
    /*
     * Assuming we got this far, we want to return the class we created.
     */
    *interesting_class = acl_class;

    return (ONEP_OK);
}

/* Main application  */
int main (int argc, char* argv[]) {
	session_handle_t* sh;
	uint64_t pak_count, last_pak_count;
	int timeout = 120;
	root = (List *)malloc(sizeof(List));


    memset(user, 0, ONEP_USERNAME_SIZE);
    memset(pwd,  0, ONEP_PASSWORD_SIZE);

	/* validate and parse the input. */
	process_cmd_line_arguments(argc, argv);

	strncpy(user, get_user(), ONEP_USERNAME_SIZE - 1);
	strncpy(pwd, get_pwd(), ONEP_PASSWORD_SIZE - 1);
	proto = atoi(get_protocol());
	strncpy(intf_name, get_interface(), ONEP_IF_NAME_SIZE);
	transporttype = get_transport_type();

	/* Connect to the Network Element */
	sh = connect_network_element(
	            get_element_address(),
	            user,
	            pwd,
	            "com.cisco.onepapp.datapath",
	            transporttype,
	            &ne);


	if (!sh) {
		fprintf(stderr, "\n*** create_network_connection fails ***\n");
		return ONEP_FAIL;
	}
    printf("\n Network Element CONNECT SUCCESS \n");

    /*
     *  Create an interesting ACL
     */
     rc = dpss_tutorial_create_acl(ne, &acl_class);
     if (rc != ONEP_OK) {
		fprintf(stderr, "\nCannot turn on interface"
				"code[%d], text[%s]\n", rc, onep_strerror(rc));
		goto cleanup;
	 }

     /*
      * Create incoming ACL
      */
     rc = create_in_acl(ne, &acl_class_in);
         if (rc != ONEP_OK) {
    		fprintf(stderr, "\nCannot turn on interface"
    				"code[%d], text[%s]\n", rc, onep_strerror(rc));
    		goto cleanup;
    	 }
	 /*
	   * Create outgoing ACL
	   */
	  rc = create_out_acl(ne, &acl_class_out);
		  if (rc != ONEP_OK) {
			fprintf(stderr, "\nCannot turn on interface"
					"code[%d], text[%s]\n", rc, onep_strerror(rc));
			goto cleanup;
		 }
     // START SNIPPET: get_interface
     /*
      * Get list of interfaces on device, then find the interface we want.
      */
     onep_interface_filter_new(&intf_filter);
     rc = onep_element_get_interface_list(ne, intf_filter, &intfs);
    if (rc != ONEP_OK) {
        fprintf(stderr, "\nError getting interface. code[%d], text[%s]\n", rc, onep_strerror(rc));
        goto cleanup;
    }
    rc = onep_collection_get_size(intfs, &count);
    if (count <= 0 ) {
        fprintf(stderr, "\nNo interfaces available");
    goto cleanup;
    }
    // END SNIPPET: get_interface
    /*
     * Display the interfaces we retrieved
     */
    dpss_tutorial_display_intf_list(intfs,stderr);


     // START SNIPPET: register_packets

    uint32_t intf_count;
     /*
      * Register some packet handlers.
      */

    onep_collection_get_size(intfs, &intf_count);

    if (intf_count > 0) {
        network_interface_t *intf;
        target_t *targIN;
        target_t *targOUT;

        target_t *targ1_IN;
        target_t *targ1_OUT;
        target_t *targ2_IN;
        target_t *targ2_OUT;

        onep_dpss_traffic_reg_t *reg_handle_1_in;
        onep_dpss_traffic_reg_t *reg_handle_1_out;
        onep_dpss_traffic_reg_t *reg_handle_2_in;
        onep_dpss_traffic_reg_t *reg_handle_2_out;
        printf("\n Name of interface expecting packets: %s\n", intf_name);

        //Interface Name Input
        network_interface_t *intf_1;
        onep_if_name in_name = "GigabitEthernet0/0";
        rc = onep_element_get_interface_by_name(ne, in_name, &intf_1);
        if (rc != ONEP_OK) {
            fprintf(stderr, "Error in getting interface: %s\n", onep_strerror(rc));
            goto cleanup;
        }

        //Interface Name Output
        onep_if_name out_name = "GigabitEthernet0/2";
        network_interface_t *intf_2;
		rc = onep_element_get_interface_by_name(ne, out_name, &intf_2);
		if (rc != ONEP_OK) {
			fprintf(stderr, "Error in getting interface: %s\n", onep_strerror(rc));
			goto cleanup;
		}

        //Interface targets for int1
        rc = onep_policy_create_interface_target(intf_1,
        		ONEP_TARGET_LOCATION_HARDWARE_DEFINED_INPUT, &targ1_IN);
        if (rc != ONEP_OK) {
            fprintf(stderr, "Error creating target interface: %s\n",
                    onep_strerror(rc));
            goto cleanup;
        }
        rc = onep_policy_create_interface_target(intf_1,
				ONEP_TARGET_LOCATION_HARDWARE_DEFINED_OUTPUT, &targ1_OUT);
		if (rc != ONEP_OK) {
			fprintf(stderr, "Error creating target interface: %s\n",
					onep_strerror(rc));
			goto cleanup;
		}
        //Interface target for int2
		rc = onep_policy_create_interface_target(intf_2,
				ONEP_TARGET_LOCATION_HARDWARE_DEFINED_INPUT, &targ2_IN);
		   if (rc != ONEP_OK) {
			   fprintf(stderr, "Error creating target interface: %s\n",
					   onep_strerror(rc));
			   goto cleanup;
		   }
	   rc = onep_policy_create_interface_target(intf_2,
					ONEP_TARGET_LOCATION_HARDWARE_DEFINED_OUTPUT, &targ2_OUT);
			   if (rc != ONEP_OK) {
				   fprintf(stderr, "Error creating target interface: %s\n",
						   onep_strerror(rc));
				   goto cleanup;
			   }

		//REGISTER FOR INTERFACE 1
        rc = onep_dpss_register_for_packets(targ1_IN, acl_class_in,
               ONEP_DPSS_ACTION_COPY, in_packet_drop_callback, 0,
               &reg_handle_1_in);
        if (rc != ONEP_OK) {
             fprintf(stderr, "Unable to register for packets: %s\n",
                     onep_strerror(rc));
             goto cleanup;
        }
        rc = onep_dpss_register_for_packets(targ1_OUT, acl_class_out,
               ONEP_DPSS_ACTION_COPY, out_packet_drop_callback, 0,
               &reg_handle_1_out);
        if (rc != ONEP_OK) {
             fprintf(stderr, "Unable to register for packets: %s\n",
                     onep_strerror(rc));
             goto cleanup;
        }

	//REGISTER FOR INTERFACE 2
    rc = onep_dpss_register_for_packets(targ2_IN, acl_class_out,
           ONEP_DPSS_ACTION_COPY, in_packet_drop_callback, 0,
           &reg_handle_2_in);
    if (rc != ONEP_OK) {
         fprintf(stderr, "Unable to register for packets: %s\n",
                 onep_strerror(rc));
         goto cleanup;
    }
    rc = onep_dpss_register_for_packets(targ2_OUT, acl_class_in,
           ONEP_DPSS_ACTION_COPY, out_packet_drop_callback, 0,
           &reg_handle_2_out);
    if (rc != ONEP_OK) {
         fprintf(stderr, "Unable to register for packets: %s\n",
                 onep_strerror(rc));
         goto cleanup;
    }
}
    last_pak_count = 0;
    /* wait to query the packet loop for the number
     * of packets received and processed. */
    while (1) {
        sleep(timeout);
        (void) onep_dpss_packet_callback_rx_count(&pak_count);
        fprintf(stderr, "Current Packet Count: %lu\n", pak_count);
        if (pak_count == last_pak_count) {
          break;
        } else {
          last_pak_count = pak_count;
        }
    }
     printf("done\n");

    // END SNIPPET: register_packets


    /*
     * Need to free dpss items.
     */
    // START SNIPPET: cleanup
    cleanup:
           disconnect_network_element(&ne, &sh);
    // END SNIPPET: cleanup
    return rc;
}


