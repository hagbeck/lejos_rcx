/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class josx_rcxcomm_irTower */

#ifndef _Included_josx_rcxcomm_irTower
#define _Included_josx_rcxcomm_irTower
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     josx_rcxcomm_irTower
 * Method:    close
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_close
  (JNIEnv *, jobject);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    hexdump
 * Signature: (Ljava/lang/String;[BI)V
 */
JNIEXPORT void JNICALL Java_josx_rcxcomm_irTower_hexdump
  (JNIEnv *, jobject, jstring, jbyteArray, jint);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    isAlive
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_isAlive
  (JNIEnv *, jobject);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_open
  (JNIEnv *, jobject, jstring);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    read
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_read
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    receive
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_receive
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    send
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_send
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     josx_rcxcomm_irTower
 * Method:    write
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_josx_rcxcomm_irTower_write
  (JNIEnv *, jobject, jbyteArray, jint);

#ifdef __cplusplus
}
#endif
#endif
