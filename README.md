# Mobile-Phone-Factory
Factory where diligent workers practice work stealing in order to quickly assemble products ordered by their clients.

## Introduction
I will implement a work stealing scheduler, than i will test it by implementing a parallel 
merge sort algorithm, and afterwards i'll use it to build a smart phone factory.

## Work Stealing Scheduler
In a work stealing scheduler, each processor in the computer system has a queue of work tasks to perform.
while running, each task can spawn a new task or more that can feasibly be executed in parallel with its other work.
When a processor runs out of work, it looks at the queues of other processors and steals their work items
.Each processor is a thread which maintains local work queue. A processor can push and pop tasks from
its local queue. Also, a processor can pop tasks from other processor’s queue by the steal action.

## Implementation of the Work Stealing scheduler
Each Processor will run on its own thread. Each Processor has own id. 
The WorkStealingThreadPool holds all the local queues of each Processor in our system. 
The WorkStealingThreadPool initially schedules the tasks to the processors by in the submit method.
The queue of each processor holds objects of type Task, the processor executes a task by invoking the
handle methodof this task method of this task. In order for a task to complete its computation, it might need
results of anothertasks (e.g. the merge sort). These results are crucial for its completion. When a task needs a 
result of anothertask, it subscribes itself to the Deferred object of that task. After the task spawn new child-tasks,
it may wait fortheir results to be resolved via the Task::whenResolved method.The Deferred is an object which represents a 
deferred result of an operation, this object will hold eventually theresult of the operation when it completes its computation. 
Each task has it is own Deferred object, when the taskcompletes its execution its result will be held by its Deferred object.
A deferred object support subscription with a callback using the whenResolved 
method, this callback will be invoked when the Deferred 
object is eventually resolved.A task might need a 
result of several other tasks at once, then it needs to subscribe to all of the deferreds
of thesetasks, and should be continuedonlywhen all the tasks are completed. Resolving all the
Deferred objects the taskwaits on them should result in rescheduling the subscribed task on the same processor 
which executed it beforesuspending (the task can also be stolen after rescheduling). 
At some point, the processor executes the rescheduledtask again, it actually will execute the continuation of the task.

## Work Stealing Mobile Phone Factory
Products and their parts are identified by ID’s. 
Parts are assembled according to plans using special tools.

## Mobile Phone Factory input Format
The following is an example of json file.
```
{
"threads": 4,
"tools": [
{
"tool": "gs-driver",
"qty": 35
},
{
"tool": "np-hammer",
"qty": 17
},
{
"tool": "rs-pliers",
"qty": 23
}
],
"plans": [
{
"product": "yphone30",
"tools": ["gs-driver", "rs-pliers"],
"parts": ["5’-screen", "round-button"]
},
{
"product": "5’-screen",
"tools": ["np-hammer"],
"parts": ["glass", "touch-controller"]
},
{
"product": "glass",
"tools": ["np-hammer"],
"parts": []
},
{
"product": "touch-controller",
"tools": ["rs-pliers", "np-hammer"],
"parts": []
},
{
"product": "round-button",
"tools": ["gs-driver"],
"parts": []
}
],
"waves": [
[
{
"product": "yphone30",
"qty": 100,
"startId": 50123450
}
]
]
}
```
The above input file will spawn 100 manufacturing tasks for yphone 30 products. When a processor begins
pro-cessing a yphone30 manufacturing task, it will spawn a 5’-screen manufacturing task and around-button manufacturing task. 
The round-button manufacturing task requires no additional parts, and can be immediatly resolved by a processor, 
while the5’-screen manufacturing task will again spawn two manufacturing tasks fro glass and touch-controller. 
Once all the tasks and sub-tasks spawned by the yphone30 manufacturing task are resolved, the yphone30 manufacturing 
task may resolve and a new yphone30 can be added to the output ConcurrentLinkedQueue.The startId of the first yphone30 is 50123450, 
the startIds of the its5’-screenandround-button are both 50123451. 
The startId of the 5’-screen’s glass and touch-controlleris 50123452.
The startId of the i-th yphone30 is 50123450+i.
