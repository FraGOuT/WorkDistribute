# WorkDistribute

This is client server application designed to parallely execute computation tasks by subdividing them into smaller tasks and executing them on all the connected clients.

The subdivision and parallel execution can greatly improve the running time for any computation task.


Currently the application only finds prime numbers within a given range.
As we know finding prime number is a computationally expensive task if only one computer is performing it.
But if more than one computers find the prime numbers in a sub range then the task can be accomplished much faster.


### Command format accepted ###

Request from the client should be of the form - prime startNumber endNumber
eg.  prime 2000 75000

Any other type of request is not understood by the server.

Other types of messages that are present and understood by the server are
1 -		.q == Terminate the client
