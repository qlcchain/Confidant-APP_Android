/*  bootstrap.c
 *
 *
 *  Copyright (C) 2016 Toxic All Rights Reserved.
 *
 *  This file is part of Toxic.
 *
 *  Toxic is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Toxic is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Toxic.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

#include "bootstrap.h"

/*20180125,extern JNIEnv from createdp2pnetwork ,begin*/
extern  JNIEnv *Env;
/*20180125,extern JNIEnv from createdp2pnetwork ,end*/



/* URL that we get the JSON encoded nodes list from. */
#define NODES_LIST_URL "https://nodes.tox.chat/json"

#define DEFAULT_NODES_FILENAME "DHTnodes.json"

/* Time to wait between bootstrap attempts */
#define TRY_BOOTSTRAP_INTERVAL 5

/* Number of nodes to bootstrap to per try */
#define NUM_BOOTSTRAP_NODES 5

/* Number of seconds since last successful ping before we consider a node offline */
#define NODE_OFFLINE_TIMOUT (60*60*24*2)

#define IP_MAX_SIZE 45
#define IP_MIN_SIZE 7
#define PORT_MAX_SIZE 5

#define LAST_SCAN_JSON_KEY "\"last_scan\":"
#define LAST_SCAN_JSON_KEY_LEN (sizeof(LAST_SCAN_JSON_KEY) - 1)

#define IPV4_JSON_KEY "\"ipv4\":\""
#define IPV4_JSON_KEY_LEN (sizeof(IPV4_JSON_KEY) - 1)

#define IPV6_JSON_KEY "\"ipv6\":\""
#define IPV6_JSON_KEY_LEN (sizeof(IPV6_JSON_KEY) - 1)

#define PORT_JSON_KEY "\"port\":"
#define PORT_JSON_KEY_LEN (sizeof(PORT_JSON_KEY) - 1)

#define PK_JSON_KEY "\"public_key\":\""
#define PK_JSON_KEY_LEN (sizeof(PK_JSON_KEY) - 1)

#define LAST_PING_JSON_KEY "\"last_ping\":"
#define LAST_PING_JSON_KEY_LEN (sizeof(LAST_PING_JSON_KEY) - 1)

/* Maximum allowable size of the nodes list */
#define MAX_NODELIST_SIZE (MAX_RECV_CURL_DATA_SIZE)

#ifndef MAX_PORT_RANGE
#define MAX_PORT_RANGE 65535
#endif



static struct Thread_Data {
    pthread_t tid;
    pthread_attr_t attr;
    pthread_mutex_t lock;
    volatile bool active;
} thread_data;

#define MAX_NODES 50
struct Node {
    char ip4[IP_MAX_SIZE + 1];
    bool have_ip4;

    char ip6[IP_MAX_SIZE + 1];
    bool have_ip6;

    char key[TOX_PUBLIC_KEY_SIZE];
    uint16_t port;
};

static struct DHT_Nodes {
    struct Node list[MAX_NODES];
    size_t count;
    time_t last_updated;
} Nodes;


/* Call_Json_From_Tox_Java
** Get json from https://nodes.tox.chat/json by java func
** 0 success call java func
** -1 Env is NULL
** -2 can't find  class
** -3 can't find  mid_construct
** -4 can't find mid_instance ID
** -5 can't Create an instance 
*/
int Call_Json_From_Tox_Java(char *json)
{ 
	jclass clazz = NULL;  
    jobject jobj = NULL;  
    jmethodID mid_construct = NULL;  
    jmethodID mid_instance = NULL;  
    JNIEnv *env = Env;
	if(env==NULL)
	{
		return -1;
	}
    // 1、从classpath路径下搜索ClassMethod这个类，并返回该类的Class对象  
    clazz = (*env)->FindClass(env, "com/stratagile/qlink/qlinkcom");  
    if (clazz == NULL) {  
        printf("找不到'com/stratagile/qlink/qlinkcom'这个类");  
        return -2;  
    } 
    // 2、获取类的默认构造方法ID  
    mid_construct = (*env)->GetMethodID(env,clazz, "<init>","()V");  
    if (mid_construct == NULL) {  
        printf("找不到默认的构造方法");  
        return -3;  
    } 
    // 3、查找实例方法的ID  
    mid_instance = (*env)->GetMethodID(env, clazz, "CallJsonFromTox", "()Ljava/lang/String;");  
    if (mid_instance == NULL) {  
		printf("找不到默认的构造方法");
        return -4;  
    }  
    // 4、创建该类的实例  
    jobj = (*env)->NewObject(env,clazz,mid_construct);  
    if (jobj == NULL) {  
        printf("在com.stratagile.qlink.qlinkcom类中找不到CallJsonFromTox方法");  
        return -5;  
    }  
    // 5、调用对象的实例方法  
    jstring result=(*env)->CallObjectMethod(env,jobj,mid_instance);  
	if(result!=NULL)
	{
    	char *json_tox=Jstring2CStr(env,result);
		if(json_tox!=NULL){		
		strcpy(json,json_tox);
		free(json_tox);
		}
	}

    // 删除局部引用  
    (*env)->DeleteLocalRef(env,clazz);  
    (*env)->DeleteLocalRef(env,jobj);  
	(*env)->DeleteLocalRef(env,result);
	return 0;
}

/* Return true if json encoded string s contains a valid IP address and puts address in ip_buf.
 *
 * ip_type should be set to 1 for ipv4 address, or 0 for ipv6 addresses.
 * ip_buf must have room for at least IP_MAX_SIZE + 1 bytes.
 */
static bool extract_val_ip(const char *s, char *ip_buf, unsigned short int ip_type)
{
    int ip_len = char_find(0, s, '"');

    if (ip_len < IP_MIN_SIZE || ip_len > IP_MAX_SIZE) {
        return false;
    }

    memcpy(ip_buf, s, ip_len);
    ip_buf[ip_len] = 0;

    return (ip_type == 1) ? is_ip4_address(ip_buf) : is_ip6_address(ip_buf);
}

/* Extracts the port from json encoded string s.
 *
 * Return port number on success.
 * Return 0 on failure.
 */
static uint16_t extract_val_port(const char *s)
{
    long int port = strtol(s, NULL, 10);
    return (port > 0 && port <= MAX_PORT_RANGE) ? port : 0;
}

/* Extracts the last pinged value from json encoded string s.
 *
 * Return timestamp on success.
 * Return -1 on failure.
 */
static long long int extract_val_last_pinged(const char *s)
{
    long long int last_pinged = strtoll(s, NULL, 10);
    return (last_pinged <= 0) ? -1 : last_pinged;
}

/* Extracts DHT public key from json encoded string s and puts key in key_buf.
 * key_buf must have room for at least TOX_PUBLIC_KEY_SIZE * 2 + 1 bytes.
 *
 * Return number of bytes copied to key_buf on success.
 * Return -1 on failure.
 */
static int extract_val_pk(const char *s, char *key_buf)
{

    int key_len = char_find(0, s, '"');

    if (key_len != TOX_PUBLIC_KEY_SIZE * 2) {
        return -1;
    }

    memcpy(key_buf, s, key_len);
    key_buf[key_len] = 0;

    return key_len;
}

/* Extracts values from json formatted string, validats them, and puts them in node.
 *
 * Return 0 on success.
 * Return -1 if line is empty.
 * Return -2 if line does not appear to be a valid nodes list entry.
 * Return -3 if node appears to be offline.
 * Return -4 if entry does not contain either a valid ipv4 or ipv6 address.
 * Return -5 if port value is invalid.
 * Return -6 if public key is invalid.
 */
static int extract_node(const char *line, struct Node *node)
{
    if (!line) {
        return -1;
    }

    const char *ip4_start = strstr(line, IPV4_JSON_KEY);
    const char *ip6_start = strstr(line, IPV6_JSON_KEY);
    const char *port_start = strstr(line, PORT_JSON_KEY);
    const char *key_start = strstr(line, PK_JSON_KEY);
    const char *last_pinged_str = strstr(line, LAST_PING_JSON_KEY);

    if (!ip4_start || !ip6_start || !port_start || !key_start || !last_pinged_str) {
        return -2;
    }

    char ip4_string[IP_MAX_SIZE + 1];
    bool have_ip4 = extract_val_ip(ip4_start + IPV4_JSON_KEY_LEN, ip4_string, 1);

    char ip6_string[IP_MAX_SIZE + 1];
    bool have_ip6 = extract_val_ip(ip6_start + IPV6_JSON_KEY_LEN, ip6_string, 0);

    if (!have_ip6 && !have_ip4) {
        return -4;
    }

    uint16_t port = extract_val_port(port_start + PORT_JSON_KEY_LEN);

    if (port == 0) {
        return -5;
    }

    char key_string[TOX_PUBLIC_KEY_SIZE * 2 + 1];
    int key_len = extract_val_pk(key_start + PK_JSON_KEY_LEN, key_string);

    if (key_len == -1) {
        return -6;
    }

    if (hex_string_to_bin_s(key_string, key_len, node->key, TOX_PUBLIC_KEY_SIZE) == -1) {
        return -6;
    }

    if (have_ip4) {
        snprintf(node->ip4, sizeof(node->ip4), "%s", ip4_string);
        node->have_ip4 = true;
    }

    if (have_ip6) {
        snprintf(node->ip6, sizeof(node->ip6), "%s", ip6_string);
        node->have_ip6 = true;
    }

    node->port = port;

    return 0;
}


/* Creates a new thread that will load the DHT nodeslist to memory
 * from json encoded nodes file obtained at NODES_LIST_URL. Only one
 * thread may run at a time.
 *
 * Return 0 on success.
 * Return -1 if a thread is already active.
 * Return -2 if mutex fails to init.
 * Return -3 if pthread attribute fails to init.
 * Return -4 if pthread fails to set detached state.
 * Return -5 if thread creation fails.
 * Return -6 env is null;
 * Return -7 obj is null;
 */
int load_DHT_nodeslist(void)
{
   	char Tox_Bootstrap[MAX_RECV_CURL_DATA_SIZE+1]={0};
	
	/*20180124,wenchao,let java get the json from tox,begin*/
	
	if(Call_Json_From_Tox_Java(Tox_Bootstrap)!=0)
	{
		printf("error get Tox_Bootstrap from java\n");
	}
//	printf("Tox_Bootstrap:%s\n",Tox_Bootstrap);

	/*20180124,wenchao,let java get the json from tox,end*/


    char line[MAX_NODELIST_SIZE + 1];

    size_t idx = 0;
    const char *line_start = Tox_Bootstrap;

    while ((line_start = strstr(line_start + 1, IPV4_JSON_KEY))) {
        idx = Nodes.count;

        if (idx >= MAX_NODES) {
            break;
        }

        if (extract_node(line_start, &Nodes.list[idx]) == 0) {
            ++Nodes.count;
        }
    }

    /* If nodeslist does not contain any valid entries we set the last_scan value
     * to 0 so that it will fetch a new list the next time this function is called.
     */
    if (Nodes.count == 0) 
	{
        printf("nodeslist load error: List did not contain any valid entries.\n");

		/*Zhijie add , use our own Bootstrap server, 20171129,Begin*/
//		printf("*******zhijie, Nodes from https://nodes.tox.chat/json is %d\n",Nodes.count);
		Nodes.count = 1;
		Nodes.list[0].have_ip4 = true;
		char ip4_string[IP_MAX_SIZE + 1] = "47.91.166.18";
		snprintf(Nodes.list[0].ip4, sizeof(Nodes.list[0].ip4), "%s", ip4_string);
		
		Nodes.list[0].have_ip6 = false;
		
		char key_string[TOX_PUBLIC_KEY_SIZE * 2 + 1] = "1EB6DE774FF1FC0306FC5766C74EAC0D0575A0B5D6B95FEEC1A4C672BE49B734";
		hex_string_to_bin_s(key_string, TOX_PUBLIC_KEY_SIZE*2, Nodes.list[0].key, TOX_PUBLIC_KEY_SIZE);
		   
		Nodes.list[0].port = 33556;
		   
		/*Zhijie add , use our own Bootstrap server, 20171129,End*/		
    }
    return 0;
}

/* Connects to NUM_BOOTSTRAP_NODES random DHT nodes listed in the DHTnodes file. */
static void DHT_bootstrap(Tox *m)
{
    pthread_mutex_lock(&thread_data.lock);
    size_t num_nodes = Nodes.count;
    pthread_mutex_unlock(&thread_data.lock);

    if (num_nodes == 0) {
        return;
    }

    size_t i;

    pthread_mutex_lock(&thread_data.lock);

    for (i = 0; i < NUM_BOOTSTRAP_NODES; ++i) {
        struct Node *node = &Nodes.list[rand() % Nodes.count];
        const char *addr = node->have_ip4 ? node->ip4 : node->ip6;

        if (!addr) {
            continue;
        }

        TOX_ERR_BOOTSTRAP err;
        tox_bootstrap(m, addr, node->port, (uint8_t *) node->key, &err);

        if (err != TOX_ERR_BOOTSTRAP_OK) {
            fprintf(stderr, "Failed to bootstrap %s:%d\n", addr, node->port);
        }

        tox_add_tcp_relay(m, addr, node->port, (uint8_t *) node->key, &err);

        if (err != TOX_ERR_BOOTSTRAP_OK) {
            fprintf(stderr, "Failed to add TCP relay %s:%d\n", addr, node->port);
        }
    }

    pthread_mutex_unlock(&thread_data.lock);
}

/* Manages connection to the Tox DHT network. */
void do_tox_connection(Tox *m)
{
    static time_t last_bootstrap_time = 0;
    bool connected = tox_self_get_connection_status(m) != TOX_CONNECTION_NONE;

    if (!connected && timed_out(last_bootstrap_time, TRY_BOOTSTRAP_INTERVAL)) {
        DHT_bootstrap(m);
        last_bootstrap_time = get_unix_time();
    }
}

