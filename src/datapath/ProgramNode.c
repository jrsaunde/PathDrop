#include <jni.h>
#include <stdio.h>
#include <limits.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>

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
#include "onep_types.h"
#include "filter.h"
#include "onep_dpss_packet_delivery.h"
#include "onep_dpss_pkt.h"
#include "onep_dpss_flow.h"
#include "onep_dpss_actions.h"
#include "onep_dpss_callback_framework.h"

#include "datapath_NodePuppet.h"
/*
#define TRY(_rc, _expr, _env, _jobj, _errFid, _fmt, _args...)                                 \
if (((_rc) = (_expr)) != ONEP_OK) {                                 \
char *_tmpErrBuf = g_strdup_printf("\n%s:%d: Error: %s(%d): " _fmt "\n",               \
__FILE__, __LINE__, onep_strerror((_rc)), (_rc) , ##_args); \
jstring _errBuf = (*_env)->NewStringUTF(_env, _tmpErrBuf); \
g_free(_tmpErrBuf); \
(*_env)->SetObjectField(_env, _jobj, _errFid, _errBuf); \
return ((_rc));                                                 \
}
*/
typedef struct list{
	struct list* next;
	struct list* previous;
	uint16_t data;//16 bit ip header identification field
	time_t timestamp;
} List;

//static List *root = NULL;
static int PACKET_TIMEOUT = 10; //how long we wait before declaring a packet loss
static char *DEST;
static int CHECK_TIME_INTERVAL = 2;
static JavaVM* g_jvm;
static JNIEnv* g_env;
static jobject g_obj;
static jmethodID g_meth;
static jmethodID g_meth2;


static jint lostPackets = 0;
static jlong totalPackets = 0;

static void add_to_java(int ID, char* name, char* c_message){

	JNIEnv* thisEnv;
	(*g_jvm)->AttachCurrentThread(g_jvm, (void**)&thisEnv, NULL);
	jstring message = (*thisEnv)->NewStringUTF(thisEnv, c_message);
	jstring router_name = (*thisEnv)->NewStringUTF(thisEnv, name);
	(*thisEnv)->CallVoidMethod(thisEnv, g_obj, g_meth, ID, router_name, message);
	//(*thisEnv)->ReleaseStringUTFChars(thisEnv, message, c_message);
	(*g_jvm)->DetachCurrentThread(g_jvm);
	return;
}
static void remove_from_java(int ID, char* name, char* c_message){
	JNIEnv* thisEnv;
	(*g_jvm)->AttachCurrentThread(g_jvm, (void**)&thisEnv, NULL);
	jstring message = (*thisEnv)->NewStringUTF(thisEnv, c_message);
	jstring router_name = (*thisEnv)->NewStringUTF(thisEnv, name);
	(*thisEnv)->CallVoidMethod(thisEnv, g_obj, g_meth2, ID, router_name, message);;
	(*g_jvm)->DetachCurrentThread(g_jvm);
	return;

}

static void add_to_end(List *list, uint16_t _data, time_t _timestamp, char* _name){
	//if(_timestamp - time() < 0) return;
	//fprintf(stderr, "add_to_end... \n");
	totalPackets++;
	List *last = list;
	List *newNode = (List *)malloc(sizeof(List));
	newNode->data = _data;
	newNode->next = NULL;
	newNode->timestamp = _timestamp;

	if(last == NULL){
		last->next = newNode;
		//printf("added packet at beginning with id %d to end at time %ld\n", _data, _timestamp);
		return;
	}
	while (last){
		if(last->next == NULL){
			last->next = newNode;
			//remove_from_java(_data, _name);
			//printf("added packet with id %d to end at time %ld\n", _data, _timestamp);
			return;
		}
		last = last->next;
	}
}

static void print_list(List *list){
	if(list == NULL){
		printf("List is empty\n");
		printf("---------------------------------------------------------------------\n");
		return;
	}
	printf("------------------------------List %p-Contents: --------------------------\n", list);
	list = list->next;
	while(list->next != NULL){
		printf("ID:%d   Time Lapse:%d\n", list->data, (int) (time(NULL) - list->timestamp));
		list = list->next;
	}
	printf("ID:%d   Time Lapse:%d\n", list->data, (int)(time(NULL) - list->timestamp));
	printf("---------------------------------------------------------------------\n");
}

static void check_timeout(List **list){
	if(list == NULL) return;
	List *cur = *list;
	if(time(NULL) - cur->timestamp > PACKET_TIMEOUT){
		List *temp = *list;
		fprintf(stderr, "freeing 1 \n");
		*list = cur->next;
		free(temp);
		printf("1found and removed packet with id %d\n", cur->data);
		return;
	}
	while(cur->next){
		if(time(NULL) - cur->next->timestamp > PACKET_TIMEOUT){
			List *temp = cur->next;
			cur->next = cur->next->next;
			printf("found and removed packet with id %d\n", cur->data);
			fprintf(stderr, "freeing 3 \n");
			free(temp);
			return;
		}else{
			cur = cur -> next;
		}
	}
	return;
}


static int search_and_remove(List **list, uint16_t _data, char* sys_name, char* int_name){
	if(list == NULL) return 0;
	List *cur = *list;
	/*if(cur->data == _data ){
		List *temp = *list;
		fprintf(stderr, "freeing 1 \n");
		*list = cur->next;
		free(temp);
		printf("1found and removed packet with id %d\n", _data);
		return 1;
	}*/
	while(cur->next){
		if(cur->next->data == _data){
			//fprintf(stderr, "Outgoing: %s [%s] with id: %d\n", sys_name, int_name, _data);
			List *temp = cur->next;
			cur->next = cur->next->next;
			//printf("found and removed packet with id %d\n", _data);
			//add_to_java(_data, sys_name, int_name);
			fprintf(stderr, "freeing 3 \n");
			free(temp);
			return 1;
		}else if(time(NULL) - cur->next->timestamp > PACKET_TIMEOUT){
				lostPackets++;
				//printf("PACKETT LOSS: packet with id %d never seen\n", cur->data);
				List *temp = cur ->next;
				cur->next = cur->next->next;
				fprintf(stderr, "freeing 4 \n");
				free(temp);
		}else{
			cur = cur -> next;
		}
	}
	return 0;
}


static void dpss_tutorial_display_intf_list(onep_collection_t *intf_list, FILE *op)
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
 * Extract IP addressing and port information from the packet.
 */
static onep_status_t dpss_tutorial_get_ip_port_info(
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
static void dpss_tutorial_get_flow_state(struct onep_dpss_paktype_ *pakp,
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

static void
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

static onep_status_t create_ace (ace_t 	**our_ace,
								 int 	ace_number,
								 char*	source_ip,
								 int 	source_port,
								 int 	source_prefix,
								 char* 	dest_ip,
								 int 	dest_port,
								 int 	dest_prefix,
								 int 	protocol){

	onep_status_t ace_rc;
	ace_t *this_ace;
	int			src_compare = ONEP_COMPARE_EQ;
	int		 	dest_compare = ONEP_COMPARE_EQ;

	if(source_port == 0){
		src_compare = ONEP_COMPARE_ANY;
	}
	if(dest_port == 0){
		dest_compare = ONEP_COMPARE_ANY;
	}

	struct sockaddr_in ip4addr_source, ip4addr_dest;
	ip4addr_source.sin_family = AF_INET;
	ip4addr_dest.sin_family = AF_INET;

	inet_pton(AF_INET, source_ip, &ip4addr_source.sin_addr);
	inet_pton(AF_INET, dest_ip, &ip4addr_dest.sin_addr);

	struct sockaddr *sock_source = (struct sockaddr *)&ip4addr_source;
	struct sockaddr *sock_dest = (struct sockaddr *)&ip4addr_dest;

	ace_rc = onep_acl_create_l3_ace(ace_number, TRUE, &this_ace);
		if (ace_rc != ONEP_OK) {
			fprintf(stderr, "Unable to create l3 ace: %s\n", onep_strerror(ace_rc));
			return ONEP_FAIL;
		}
	ace_rc = onep_acl_set_l3_ace_src_prefix(this_ace, sock_source, source_prefix);
		if (ace_rc != ONEP_OK) {
			fprintf(stderr, "Unable to set source prefix on ace_1: %s\n", onep_strerror(ace_rc));
			return ONEP_FAIL;
		}
	ace_rc = onep_acl_set_l3_ace_dst_prefix(this_ace, sock_dest, dest_prefix);
		if (ace_rc != ONEP_OK) {
			fprintf(stderr, "Unable to set dest prefix: %s\n", onep_strerror(ace_rc));
			return ONEP_FAIL;
		}
	ace_rc = onep_acl_set_l3_ace_protocol(this_ace, protocol);
		if (ace_rc != ONEP_OK) {
			fprintf(stderr, "Unable to set protocol: %s\n", onep_strerror(ace_rc));
			return ONEP_FAIL;
		}
	ace_rc = onep_acl_set_l3_ace_src_port(this_ace, source_port, src_compare);
		if (ace_rc != ONEP_OK) {
			fprintf(stderr, "Unable to set source port: %s\n", onep_strerror(ace_rc));
			return ONEP_FAIL;
		}
	ace_rc = onep_acl_set_l3_ace_dst_port(this_ace, dest_port, dest_compare);
		if (ace_rc != ONEP_OK) {
			fprintf(stderr, "Unable to set dest port: %s\n", onep_strerror(ace_rc));
			return ONEP_FAIL;
		}

		//Return our ace
		*our_ace = this_ace;
		return ONEP_OK;
}
static onep_status_t create_acls( network_element_t *elm,
								  class_t			**in_class,
								  class_t			**out_class,
								  int				proto,
								  char*				source_address,
								  int				source_port,
								  int				source_len,
								  char*				dest_address,
								  int				dest_port,
								  int				dest_len){

	onep_status_t acl_rc;
	ace_t 		*ace_1, *ace_2, *ace_3, *ace_4;
	acl_t 		*in_acl, *out_acl;
	filter_t 	*acl_filter_in, *acl_filter_out;
	class_t		*acl_class_in, *acl_class_out;


	/* ACE_1 src -> dest */
	acl_rc = create_ace(&ace_1, 41, source_address, source_port, source_len, dest_address, dest_port, dest_len, proto);
		if(acl_rc != ONEP_OK){
			fprintf(stderr, "Problem creating ace1: %s\n", onep_strerror(acl_rc));
		}
	/* ACE_2 dest -> src */
	acl_rc = create_ace(&ace_2, 42, dest_address, dest_port, dest_len, source_address, source_port, source_len, proto);
		if(acl_rc != ONEP_OK){
			fprintf(stderr, "Problem creating ace2: %s\n", onep_strerror(acl_rc));
		}
	/* ACE_3 src -> dest */
	acl_rc = create_ace(&ace_3, 43, source_address, source_port, source_len, dest_address, dest_port, dest_len, proto);
		if(acl_rc != ONEP_OK){
			fprintf(stderr, "Problem creating ace3: %s\n", onep_strerror(acl_rc));
		}
	/* ACE_4 dest -> src */
	acl_rc = create_ace(&ace_4, 44, dest_address, dest_port, dest_len, source_address, source_port, source_len, proto);
		if(acl_rc != ONEP_OK){
			fprintf(stderr, "Problem creating ace4: %s\n", onep_strerror(acl_rc));
		}

	/* Create the ACL */
	acl_rc = onep_acl_create_l3_acl(AF_INET, elm, &in_acl);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to create acl: %s\n", onep_strerror(acl_rc));
		return ONEP_FAIL;
	}
	acl_rc = onep_acl_create_l3_acl(AF_INET, elm, &out_acl);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to create acl: %s\n", onep_strerror(acl_rc));
		return ONEP_FAIL;
	}

	/* Add ACEs to ACLs */
	acl_rc = onep_acl_add_ace(in_acl, ace_1);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to add ace1 to in_acl: %s\n", onep_strerror(acl_rc));
		return ONEP_FAIL;
	}
	//printf("\n Added ace1 to in_acl\n");
	acl_rc = onep_acl_add_ace(in_acl, ace_2);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to add ace2 to in_acl: %s\n", onep_strerror(acl_rc));
		return ONEP_FAIL;
	}

	acl_rc = onep_acl_add_ace(out_acl, ace_3);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to add ace3 to out_acl: %s\n", onep_strerror(acl_rc));
		return ONEP_FAIL;
	}
	//fprintf(stderr,"\n Added ace1 to acl2\n");
	acl_rc = onep_acl_add_ace(out_acl, ace_4);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to add ace4 to out_acl: %s\n", onep_strerror(acl_rc));
		return ONEP_FAIL;
	}

	/* Create Class maps */
	acl_rc = onep_policy_create_class(elm, ONEP_CLASS_OPER_OR, &acl_class_in);
	if (acl_rc != ONEP_OK) {
		 fprintf(stderr, "Unable to create class: %s\n", onep_strerror(acl_rc));
		 return ONEP_FAIL;
	}
	acl_rc = onep_policy_create_class(elm, ONEP_CLASS_OPER_OR, &acl_class_out);
	if (acl_rc != ONEP_OK) {
		 fprintf(stderr, "Unable to create class: %s\n", onep_strerror(acl_rc));
		 return ONEP_FAIL;
	}

	/* Create an acl filter containing the acl created above*/
	acl_rc = onep_policy_create_acl_filter(in_acl, &acl_filter_in);
	if (acl_rc != ONEP_OK) {
		   fprintf(stderr, "Unable to create acl filter: %s\n", onep_strerror(acl_rc));
		   return ONEP_FAIL;
	}
	acl_rc = onep_policy_create_acl_filter(out_acl, &acl_filter_out);
	if (acl_rc != ONEP_OK) {
		   fprintf(stderr, "Unable to create acl filter: %s\n", onep_strerror(acl_rc));
		   return ONEP_FAIL;
	}

	/* Now add the ACL filter to the created acl_class*/
	acl_rc = onep_policy_add_class_filter(acl_class_in, acl_filter_in);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to add filter to class: %s\n",
			onep_strerror(acl_rc));
		return ONEP_FAIL;
	}
	acl_rc = onep_policy_add_class_filter(acl_class_out, acl_filter_out);
	if (acl_rc != ONEP_OK) {
		fprintf(stderr, "Unable to add filter to class: %s\n",
			onep_strerror(acl_rc));
		return ONEP_FAIL;
	}

	/*Assuming we got this far, we want to return the class we created */
	*in_class = acl_class_in;
	*out_class = acl_class_out;

	return ONEP_OK;
}


static void out_packet_drop_callback( onep_dpss_traffic_reg_t *reg, struct onep_dpss_paktype_ *pak, void *client_context, bool *return_packet){
	onep_status_t        rc;
    onep_dpss_fid_t      fid;
    uint16_t             src_port = 0;
    uint16_t             dest_port = 0;
    char                 *src_ip = NULL;
    char                 *dest_ip = NULL;
    char                 l4_protocol[5];
    char                 l4_state[30];
    uint16_t			pkt_id = 0;
    network_interface_t* output_int;
    onep_if_name 		 output;

    network_element_t* elem;
    element_property_t* prop;
    char* sys_name;

    strcpy(l4_protocol,"ERR");
    strcpy(l4_state,"ERR");

    rc = onep_dpss_pkt_get_flow(pak, &fid);
    if( rc == ONEP_OK ) {
    	dpss_tutorial_get_ip_port_info(pak, &src_ip, &dest_ip, &src_port, &dest_port, l4_protocol, '4', &pkt_id);
    	dpss_tutorial_get_flow_state(pak, fid, l4_state);

    	//Get output interface of packet
        onep_dpss_pkt_get_output_interface(pak, &output_int);
        onep_interface_get_name(output_int, output);

        //Get network element
        onep_dpss_pkt_get_network_element(pak, &elem);
        onep_element_get_property(elem, &prop);
        onep_element_property_get_sys_name(prop, &sys_name);
    } else {
        fprintf(stderr, "Error getting flow ID. code[%d], text[%s]\n", rc, onep_strerror(rc));
    }
    //fprintf(stderr, "\n"
    // 		"Out - %-4d | %-18s | %-18s | %-15s (%-5d) --> %-15s (%-5d)\n", pkt_id, sys_name, output, src_ip, src_port, dest_ip, dest_port);
    //search_and_remove((List **) client_context, pkt_id, sys_name, output);
    add_to_java(pkt_id, sys_name, output);

    //print_list((List *) client_context);
    //fflush(stdout);
    free(sys_name);
    free(src_ip);
    free(dest_ip);
    return;
}

static void out_packet_drop_callback2( onep_dpss_traffic_reg_t *reg, struct onep_dpss_paktype_ *pak, void *client_context, bool *return_packet){
	onep_status_t        rc;
    onep_dpss_fid_t      fid;
    uint16_t             src_port = 0;
    uint16_t             dest_port = 0;
    char                 *src_ip = NULL;
    char                 *dest_ip = NULL;
    char                 l4_protocol[5];
    char                 l4_state[30];
    uint16_t			pkt_id = 0;
    network_interface_t* output_int;
    onep_if_name 		 output;

    strcpy(l4_protocol,"ERR");
    strcpy(l4_state,"ERR");

    rc = onep_dpss_pkt_get_flow(pak, &fid);
    if( rc == ONEP_OK ) {
    	dpss_tutorial_get_ip_port_info(pak, &src_ip, &dest_ip, &src_port, &dest_port, l4_protocol, '4', &pkt_id);
    	dpss_tutorial_get_flow_state(pak, fid, l4_state);

    	//Get output interface of packet
        onep_dpss_pkt_get_output_interface(pak, &output_int);
        onep_interface_get_name(output_int, output);


    } else {
        fprintf(stderr, "Error getting flow ID. code[%d], text[%s]\n", rc, onep_strerror(rc));
    }
    printf("\n"
    		"Out2 - %-4d | %-18s | %-15s (%-5d) --> %-15s (%-5d)\n", pkt_id, output, src_ip, src_port, dest_ip, dest_port);
    search_and_remove((List **) client_context, pkt_id, "", output);
    //print_list((List *) client_context);
    //fflush(stdout);
    free(src_ip);
    free(dest_ip);
    return;
}

static void in_packet_drop_callback( onep_dpss_traffic_reg_t *reg,
							  struct onep_dpss_paktype_ *pak,
							  void *client_context,
							  bool *return_packet){

		onep_status_t        rc;
	    onep_dpss_fid_t      fid;
	    uint16_t             src_port = 0;
	    uint16_t             dest_port = 0;
	    char                 *src_ip = NULL;
	    char                 *dest_ip = NULL;
	    char                 l4_protocol[5];
	    char                 l4_state[30];
	    uint16_t			pkt_id = 0;
	    network_interface_t* input_int;
	    onep_if_name 		 input;


        network_element_t* elem;
        element_property_t* prop;
        char* sys_name;

	    strcpy(l4_protocol,"ERR");
	    strcpy(l4_state,"ERR");

	    rc = onep_dpss_pkt_get_flow(pak, &fid);
	    if( rc == ONEP_OK ) {
	    	dpss_tutorial_get_ip_port_info(pak, &src_ip, &dest_ip, &src_port, &dest_port, l4_protocol, '4', &pkt_id);
	    	dpss_tutorial_get_flow_state(pak, fid, l4_state);

	        //Get input interface of packet
	        onep_dpss_pkt_get_input_interface(pak, &input_int);
	        onep_interface_get_name(input_int, input);

	        //Get network element
	        onep_dpss_pkt_get_network_element(pak, &elem);
	        onep_element_get_property(elem, &prop);
	        onep_element_property_get_sys_name(prop, &sys_name);

	    } else {
	        fprintf(stderr, "Error getting flow ID. code[%d], text[%s]\n", rc, onep_strerror(rc));
	    }

	    //fprintf(stderr,"\n"
	    //		"In  - %-4d | %-18s | %-18s | %-15s (%-5d) --> %-15s (%-5d)\n", pkt_id, sys_name, input, src_ip, src_port, dest_ip, dest_port);
	    /*
	     * If it is destination, assume not lost and dont add to list
	     * strcmp - 0 if equal
	     */

	    //if(strcmp(DEST, dest_ip)){
	    //printf("client_context %p\n", client_context);
	    	//add_to_end((List *) client_context, pkt_id, time(NULL), sys_name);
		    remove_from_java(pkt_id, sys_name, input);
	    	//print_list((List *) client_context);
	    //}
		fflush(stdout);
	    free(src_ip);
	    free(dest_ip);
		free(sys_name);
	    return;
}

static void in_packet_drop_callback2( onep_dpss_traffic_reg_t *reg,
							  struct onep_dpss_paktype_ *pak,
							  void *client_context,
							  bool *return_packet){

		onep_status_t        rc;
	    onep_dpss_fid_t      fid;
	    uint16_t             src_port = 0;
	    uint16_t             dest_port = 0;
	    char                 *src_ip = NULL;
	    char                 *dest_ip = NULL;
	    char                 l4_protocol[5];
	    char                 l4_state[30];
	    uint16_t			pkt_id = 0;
	    network_interface_t* input_int;
	    onep_if_name 		 input;

	    strcpy(l4_protocol,"ERR");
	    strcpy(l4_state,"ERR");

	    rc = onep_dpss_pkt_get_flow(pak, &fid);
	    if( rc == ONEP_OK ) {
	    	dpss_tutorial_get_ip_port_info(pak, &src_ip, &dest_ip, &src_port, &dest_port, l4_protocol, '4', &pkt_id);
	    	dpss_tutorial_get_flow_state(pak, fid, l4_state);

	        //Get input interface of packet
	        onep_dpss_pkt_get_input_interface(pak, &input_int);
	        onep_interface_get_name(input_int, input);
	    } else {
	        fprintf(stderr, "Error getting flow ID. code[%d], text[%s]\n", rc, onep_strerror(rc));
	    }

	    printf("\n"
	    		"In2  - %-4d | %-18s | %-15s (%-5d) --> %-15s (%-5d)\n", pkt_id, input, src_ip, src_port, dest_ip, dest_port);
	    /*
	     * If it is destination, assume not lost and dont add to list
	     * strcmp - 0 if equal
	     */

	    //if(strcmp(DEST, dest_ip)){
	    printf("client_context %p\n", client_context);
	    	add_to_end((List *) client_context, pkt_id, time(NULL), "");
		    print_list((List *) client_context);
	    //}
		fflush(stdout);
	    free(src_ip);
	    free(dest_ip);
	    return;
}

static onep_status_t register_traffic(network_element_t *ne,
									  network_interface_t *this_interface,
									  class_t *in_acl,
									  class_t *out_acl,
									  target_t **in_target,
									  target_t **out_target,
									  onep_dpss_traffic_reg_t **in_handle,
									  onep_dpss_traffic_reg_t **out_handle,
									  List *root,
									  List **root_addr){

	onep_status_t rc;

	onep_if_name name;
	element_property_t* prop;
	char * sys_name;
	onep_interface_get_name(this_interface, name);
    onep_element_get_property(ne, &prop);
    onep_element_property_get_sys_name(prop, &sys_name);

	//fprintf(stderr, "We are registering for %s\n", name);
    //Interface targets for int1
    rc = onep_policy_create_interface_target(this_interface, ONEP_TARGET_LOCATION_HARDWARE_DEFINED_INPUT, in_target);
    if (rc != ONEP_OK) {
        fprintf(stderr, "Error creating target interface: %s\n", onep_strerror(rc));
        return ONEP_FAIL;
    }
    rc = onep_policy_create_interface_target(this_interface, ONEP_TARGET_LOCATION_HARDWARE_DEFINED_OUTPUT, out_target);
	if (rc != ONEP_OK) {
		fprintf(stderr, "Error creating target interface: %s\n", onep_strerror(rc));
		return ONEP_FAIL;
	}
    //fprintf(stderr, "created targets\n");

	//Register for packets
		rc = onep_dpss_register_for_packets(*in_target, in_acl, ONEP_DPSS_ACTION_COPY, in_packet_drop_callback, root, in_handle);
		if (rc != ONEP_OK) {
			fprintf(stderr, "Unable to register for packets: %s\n", onep_strerror(rc));
			return ONEP_FAIL;
		}
		rc = onep_dpss_register_for_packets(*out_target, out_acl, ONEP_DPSS_ACTION_COPY, out_packet_drop_callback, root_addr, out_handle);
		if (rc != ONEP_OK) {
			fprintf(stderr, "Unable to register for packets: %s\n", onep_strerror(rc));
			return ONEP_FAIL;
		}


	/* If we made it here, we registered successfully */
	//fprintf(stderr, "Registered for %s on %s!\n", name, sys_name);
	free(sys_name);
	return ONEP_OK;

}



JNIEXPORT int JNICALL Java_datapath_NodePuppet_ProgramNode(JNIEnv *env,
														   jobject thisObj,
														   jstring j_address,
														   jstring j_user,
														   jstring j_pass,
														   jint    j_protocol,
														   jstring j_source,
														   jint	   j_source_port,
														   jstring j_dest,
														   jint	   j_dest_port) {

	/*Application Vars */
	network_application_t* myapp = NULL;
	session_handle_t*      session_handle = NULL;
	//session_handle_t*      session_handle2 = NULL;
	onep_status_t          rc;
	session_config_t*      config = NULL;

	/* Node Vars */
	network_element_t *ne1;
	struct sockaddr_in     v4addr;
	char *c_address, *c_username, *c_password, *c_source, *c_dest;
	int c_source_port = (int) j_source_port;
	int c_dest_port = (int) j_dest_port;
    int acl_number = 40;
    int c_protocol = (int) j_protocol;

    /* Policy Vars */
    class_t* acl_class_in, *acl_class_out;
    class_t* acl_class_in2, *acl_class_out2;

    interface_filter_t* intf_filter = NULL;
    onep_collection_t*  intfs = NULL;
	network_interface_t* intf;

	onep_dpss_traffic_reg_t *in_handle, *out_handle, *in_handle2, *out_handle2;
    target_t *in_target = NULL;
    target_t *out_target = NULL;
    target_t *in_target2 = NULL;
     target_t *out_target2 = NULL;
    uint64_t pak_count, last_pak_count;

	/*Get a reference to this object's class */
	jclass thisClass = (*env)->GetObjectClass(env, thisObj);
	(*env)->GetJavaVM(env, &g_jvm);

	g_obj = (*env)->NewGlobalRef(env, thisObj);
	jclass g_class = (*env)->GetObjectClass(env, g_obj);
	g_meth = (*env)->GetMethodID(env, g_class, "sendOutgoing", "(ILjava/lang/String;Ljava/lang/String;)V");
	g_meth2 = (*env)->GetMethodID(env, g_class, "removeIncoming", "(ILjava/lang/String;Ljava/lang/String;)V");
	//add_to_java(10, "This is it");
//	callBackOut = (*env)->GetMethodID(env, thisClass, "sendOutgoing", "(ILjava/lang/String;)V");
//	masterEnv = env;
//	masterObj = thisObj;

	//char* c_message = "Router2";
	//jstring message = (*env)->NewStringUTF(env, c_message);
	//(*env)->CallVoidMethod(env, thisObj, callBackOut, 254, message);
	//(*env)->ReleaseStringUTFChars(env, message, c_message);

	//make lists for each address
//	int stringCount = (*env)->GetArrayLength(env, j_address);
//	char **rawString = (char **) malloc(stringCount*sizeof(char *));
//	List **masterList = (List **) malloc(stringCount*sizeof(List*));
//	int i, t;
//	for (i=0; i<stringCount; i++) {
//	        jstring string = (jstring) (*env)->GetObjectArrayElement(env, j_address, i);
//	        rawString[i] =  (*env)->GetStringUTFChars(env, string, 0);
//	        List *root = (List *)malloc(sizeof(List));
//	        root->data = 0;
//	        root->timestamp = time(NULL);
//	        masterList[i] = root;
//	}
//	for (i=0; i<stringCount; i++) {
//		       printf("address: %s\n", rawString[i]);
//	}


	/* Get arguments */
			c_username 	= (char *) (*env)->GetStringUTFChars(env, j_user, NULL);
			c_password 	= (char *) (*env)->GetStringUTFChars(env, j_pass, NULL);
			c_source	= (char *) (*env)->GetStringUTFChars(env, j_source, NULL);
			c_dest		= (char *) (*env)->GetStringUTFChars(env, j_dest, NULL);
			c_address 	= (char *) (*env)->GetStringUTFChars(env, j_address, NULL);

			List *root = (List *)malloc(sizeof(List));
			root->data = 0;
			root->timestamp = time(NULL);

			List *outgoing = (List *)malloc(sizeof(List));
			outgoing->data = 0;
			outgoing->timestamp = time(NULL);
	//for (t=0; t<stringCount; t++) {
	/* Create Application instance. */
		//TRY(rc, onep_application_get_instance(&myapp), env, thisObj, errFid,
		//"onep_application_get_instance");


		//c_address 	= rawString[t];
		//fprintf(stderr, c_address);
	    onep_application_get_instance(&myapp);
		onep_application_set_name(myapp, c_address);

	/* Set session parameters */
		//TRY(rc, onep_session_config_new(ONEP_SESSION_SOCKET, &config), env, thisObj, errFid,
		//"onep_session_config_new");
		onep_session_config_new(ONEP_SESSION_SOCKET, &config);
		onep_session_config_set_event_queue_size(config, 300);
		onep_session_config_set_event_thread_pool(config, 1);
		onep_session_config_set_event_drop_mode(config, ONEP_SESSION_EVENT_DROP_OLD);


		fprintf(stderr, "Address: %s Username: %s Password: %s Protocol: %d\n", c_address, c_username, c_password, c_protocol);

		DEST = c_dest;
		//set address and connect for each address in array
			/* Set address of Network Element */
				memset(&v4addr, 0, sizeof(struct sockaddr_in));
				v4addr.sin_family = AF_INET;
				inet_pton(AF_INET, c_address, &(v4addr.sin_addr));


				//TRY(rc, onep_application_get_network_element(
				//myapp, (struct sockaddr*)&v4addr, &ne1), env, thisObj, errFid,
				//"onep_application_get_network_element");
				onep_application_get_network_element(myapp, (struct sockaddr*)&v4addr, &ne1);


				fprintf(stderr, "%s\n", c_address);

			/* Connect to Network Element */
				//TRY(rc, onep_element_connect(ne1, c_username, c_password, config, &session_handle), env, thisObj, errFid,
				//"onep_element_connect");
				onep_element_connect(ne1, c_username, c_password, config, &session_handle);


				if (!session_handle) {
					fprintf(stderr, "\n*** create_network_connection fails ***\n");
					return ONEP_FAIL;
				}
				fprintf(stderr, "\n Network Element CONNECT SUCCESS \n");


			/* Create ACLs */
				rc = create_acls(ne1, &acl_class_in, &acl_class_out, c_protocol, c_source, c_source_port, 32, c_dest, c_dest_port, 32);


				if (rc != ONEP_OK) {
					fprintf(stderr, "\nCannot create ACLs"
							"code[%d], text[%s]\n", rc, onep_strerror(rc));
					goto cleanup;
				}

			/* Get list of interfaces on device, then find the interface we want */
				onep_interface_filter_new(&intf_filter);
				onep_interface_filter_set_type(intf_filter, ONEP_IF_TYPE_ETHERNET);
				rc = onep_element_get_interface_list(ne1, intf_filter, &intfs);


				if (rc != ONEP_OK) {
						fprintf(stderr, "\nError getting interface. code[%d], text[%s]\n", rc, onep_strerror(rc));
						goto cleanup;
					}
	/* Display the interfaces we retrieved */
		//dpss_tutorial_display_intf_list(intfs,stderr);
		uint32_t intf_count;


	/* Register some packet handlers */
			onep_if_name name;

			onep_collection_get_size(intfs, &intf_count);
			if (intf_count>0) {
									fprintf(stderr, "We have %d interfaces\n", intf_count);
				unsigned int i;
				/* for each interface, we will register for incoming and outgoing traffic */
				for (i = 0; i < intf_count; i++) {
					rc = onep_collection_get_by_index(intfs, i, (void **)&intf);
					if (rc==ONEP_OK) {
						rc = onep_interface_get_name(intf,name);
						//fprintf(stderr, "Registering for traffic on %s\n", name);
						rc = register_traffic(ne1, intf, acl_class_in, acl_class_out, &in_target, &out_target, &in_handle, &out_handle, root, &root);

						if(rc != ONEP_OK){
							fprintf(stderr, "Problem registering for interface %s\n", name);
						}
					} else {
						fprintf(stderr, "Error getting interface. code[%d], text[%s]\n", rc, onep_strerror(rc));
					}
				}
			} if (intf_count <= 0 ) {
				fprintf(stderr, "\nNo interfaces available\n");
				goto cleanup;
			}

	//}
			fprintf(stderr, "done registering..\n");
			//jfieldID fidNumber = (*env)->GetFieldID(env, thisClass, "runTime", "Z");
			//bool runTime = (*env)->GetBooleanField(env, thisObj, fidNumber);

			while (1) {	//TODO:Add here a check to TrafficWatch.run for true/false
				sleep(CHECK_TIME_INTERVAL);
				//check_timeout(&root);
				//print_list(root);
				//bool runTime2 = (*env)->GetBooleanField(env, thisObj, fidNumber);
				//fprintf(stderr, "Checking for runtime %d\n", runTime2);
			}
			 printf("done\n");

	/* END SNIPPET: register_packets */

	//the int
	//get the Field ID of number
//	jfieldID fidNumber = (*env)->GetFieldID(env, thisClass, "number","I");
//	if(NULL == fidNumber) return 1;
//
//	//Get the int given the Field ID
//	jint number = (*env)->GetIntField(env, thisObj, fidNumber);
//	printf("In C, the int is %d\n", number);
//
//	//Change the variable
//	number = 99;
//	(*env)->SetIntField(env, thisObj, fidNumber, number);


	cleanup:
			fprintf(stderr, "Cleaning up node\n");
			disconnect_network_element(&ne1, &session_handle);
	//At the end release the resources
//	for(i = 0; i < stringCount; i++){
//		(*env)->ReleaseStringUTFChars(env, j_address, rawString[i]);
//	}
//	free(rawString);
    (*env)->ReleaseStringUTFChars(env, j_address, c_address);
    (*env)->ReleaseStringUTFChars(env, j_user, c_username);
    (*env)->ReleaseStringUTFChars(env, j_pass, c_password);
    (*env)->ReleaseStringUTFChars(env, j_source, c_source);
    (*env)->ReleaseStringUTFChars(env, j_dest, c_dest);
   return 1;
}
