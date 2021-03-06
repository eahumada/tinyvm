
#include "classes.h"
#include "language.h"

#ifndef _THREADS_H
#define _THREADS_H

#define DEAD    0 /* Must be zero, due to rerun semantics */
#define RUNNING 1
#define WAITING 2
#define STARTED 3

#define SF_SIZE (sizeof(StackFrame))

extern Thread *currentThread;
extern Thread *bootThread;
extern byte gThreadCounter;

typedef struct S_StackFrame
{
  MethodRecord *methodRecord;
  STACKWORD *localsBase;
  STACKWORD *stackTop;
  byte *pc;
  Object *monitor;
} StackFrame;

extern boolean init_thread (Thread *thread);
extern void start_thread (Thread *thread);
extern void switch_thread();
extern StackFrame *current_stackframe();
extern void enter_monitor (Object* obj);
extern void exit_monitor (Object* obj);

/**
 * Must be written by user of threads.h.
 */
extern void switch_thread_hook();

#define stackframe_array_ptr() (word2ptr(currentThread->stackFrameArray))
#define stack_array_ptr()      (word2ptr(currentThread->stackArray))
#define stackframe_array()     ((StackFrame *) ((byte *) stackframe_array_ptr() + HEADER_SIZE))
#define stack_array()          ((STACKWORD *) ((byte *) stack_array_ptr() + HEADER_SIZE))

static inline void init_threads()
{
  gThreadCounter = 0;
  currentThread = null;	
}

#endif



