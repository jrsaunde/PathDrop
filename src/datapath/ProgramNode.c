#include <jni.h>
#include <stdio.h>
#include "datapath_NodePuppet.h"
 
JNIEXPORT void JNICALL Java_datapath_NodePuppet_ProgramNode(JNIEnv *env, jobject thisObj, jstring j_address, jstring j_user, jstring j_pass, jstring j_protocol) {
	session_handle_t* sh;




	//Convert all from jstrings to char*
	const char *address = (*env)->GetStringUTFChars(env, j_address, NULL);
	const char *user = (*env)->GetStringUTFChars(env, j_user, NULL);
	const char *pass = (*env)->GetStringUTFChars(env, j_pass, NULL);
	const char *protocol = (*env)->GetStringUTFChars(env, j_protocol, NULL);
   
	printf("Address: %s Username: %s Password: %s Protocol: %s\n", address, user, pass, protocol);


	//Get a reference to this object's class
	jclass thisClass = (*env)->GetObjectClass(env, thisObj);

	//the int
	//get the Field ID of number
	jfieldID fidNumber = (*env)->GetFieldID(env, thisClass, "number","I");
	if(NULL == fidNumber) return;

	//Get the int given the Field ID
	jint number = (*env)->GetIntField(env, thisObj, fidNumber);
	printf("In C, the int is %d\n", number);

	//Change the variable
	number = 99;
	(*env)->SetIntField(env, thisObj, fidNumber, number);


	//At the end release the resources
	(*env)->ReleaseStringUTFChars(env, j_address, address);  // release resources
	(*env)->ReleaseStringUTFChars(env, j_user, user);  // release resources
	(*env)->ReleaseStringUTFChars(env, j_pass, pass);  // release resources
	(*env)->ReleaseStringUTFChars(env, j_protocol, protocol);  // release resources
   return;
}
