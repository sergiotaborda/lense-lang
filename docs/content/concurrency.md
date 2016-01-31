title=Concurrency
date=2015-12-26
type=post
tags=tour, lense
status=draft
~~~~~~

# Concurrency

Lense chooses not to force any concurrency model because there is no guarantee it would be available in all platforms.
So, concurrency is enabled by means of specific APIs that hide the details an not by language constructs.

## Promises and Deference

A ``Promise`` is a monad that can hold functions to be executed once a value is calculated. The promise does not define if the calculation is executed in another thread or not, but conceptually is better to work as if it where. The thread that executes the value also executes the passed functions. A promise holds a function for when the value is calculated sucessufly and a function for when the promise can not be calculated, i.e. the calculation ends in an exception.

~~~~brush: lense
public interface Promise<T> {

	Promise<T> then (Action<T> onSuccess);
	Promise<Nothing> catch (Action<Exception> onError)
	
	Promise<R> map (Function<R,T> transformation);
}   
~~~~

A ``Deferred`` is a ``Promise`` factory that holds a private relation to the created ``Promise``.``Deferred`` objects also do not define the concurrency model. They are simply a factory that holds control of the internal state of the promise. Is the ``Deferred`` object that receives the value after the calculation is executed and triggers the ``then`` function, or triggers the ``catch`` function after an exception is detected. The calculation itself is handled by a third object.  

A simple example that do not uses concurrency could be something like this:

~~~~brush: lense
public object Calculator{

    // calculates the n-th element in the fibonacci sequence
    // 0	1	2	3	4	5	6	7 ...
    // 0	1	1	2	3	5	8	13 ...
	public Promise<Natural> fibonacci(Natural n) {
	         Deferred<Natural> deferred = new Deferred.empty();
	         
	         try {
	         
	         		Natural fibonacci = calculateRecursively (n);
	         		deferred.sucess(fibonacci);
	         	} catch (Exception e){
	         		deferred.error(e);
	         	}
	         
	         return deferred.promisse(); 
	}
	
	private Natural calculateRecursively (Natural n){
		if (n <= 1){
			return n;
		} else if (n > 1000){
		 	// just for it to be possible to have an exception.
			throw new Exception("Cannot calculate Fibonacci for numbers greated than 1000");
		}
		
		return calculateRecursively (n-1) + calculateRecursively (n-2)
	}
	
}

~~~~

This implementation does not use concurrency but the client code calling the ``fibonacci`` function has no way of knowing this, so it has to assume the code is calculated concurrently. Later, the implementation can evolve to a concurrent of even distributed version without consequences to the calling code.

## Executors

An ``Executor`` is an object that can create another thread of execution to run a given function. 
The calling code will continue to run after the executor is called, enabling concurrency.

There are two principal executors in the standard API: the ``TimedExecutor`` and the ``DeferredExecutor``.
The ``TimedExecutor`` enables code to be run after some time as elapsed. ``DefferedExecutor`` allows to the code to run
immediately in another thread. 

~~~~brush: lense 
public object TimedExecutor {

	Promise<T> runAfter<T>(Duration duration, Function<T> calculation);
	Promise<T> runAt<T>(TimePoint timePoint, Function<T> calculation);
	Void runEvery(Duration duration, Runnable runnable); 
}

public object DeferredExecutor {

	Promise<T> run<T>(Function<T> calculation);
}
~~~~ 

``Executor``s return promises that can then be combined (they are monads) to produce a result. This is equivalent to a wait/notify/join mechanism without all the fuss and without having to be specific about a thread model.

The ``TimedExecutor`` is implemented in java using the Java Executor API and in javascript using ``window.time``.
``Deferred`` objects are used internally to create and control the promises.  
 
## Actors 

``Actor``s enable a message based communication between different threads that can be distributed in one or more machines. The Actor model is similar to Java Messaging Service in the sense messages are exchanged back and forward. However the technology the enables the actor to send and receive the messages depends on the ``ActorEnvironment`` used.  The environment determines how the messages are handled and several different implementations are possible. The actors exist in the scope of a specific environment. For example, in a javascript an environment based on *websockets* would be a natural choice. But is as not be a raw websocket implementation an other APIs like *Stomp* can be used.
A possible code would be:

~~~~brush: lense 
ActorEnvironment env = new StompActorEnvironment("http://server.side.address.com");

// register the actor with a name

env.register("myActor", new MyActor()); 
env.register("printActor", new PrintActor());

//sending values from outside the environment.
env.sendTo("myActor" , 4);
~~~~


The actor is self a class or object that extends ``Actor``.


~~~~brush: lense 
public class MyActor extends Actor {

       public Void onMesssage(Message msg){
       
             val n = msg.payload as Natural;
             
             if (n is Some<Natural>){
               Natural f =  calculateRecursively(n);
               
               this.Environment.sendTo("printActor", Deferred.withValue(f).promise);
             } else {
               this.Environment.sendTo("printActor", Deferred.withError("No value given.").promise);
             
             }
       }

}

public class PrintActor extends Actor {

       public Void onMesssage(Message msg){
       
             val n = msg.payload as Promise<Natural>;
             
             if (n is Some<Promise<Natural>>){
             	 n.then( value => Console.println("The result is {{ value }}"));
             	 n.error( e => Console.println("The result could not be calculated because {{ e.message }}"));
             }
       }

}
~~~~

 
This code will create and register two actors. One calculates the fibonacci sequence; the second, prints the calculated value.
Actors communicate thought messages like people communicating via e-mail; by messages. Each actor has access to the environment is registered with and can use it to send messages to other actors. 

The communication is normally asynconous whenever the ``sendTo`` method is called. Actors allow for more complex concurrent scenarios and patterns like Producer-Consumer.  