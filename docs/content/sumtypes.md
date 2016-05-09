title=Sum Types
date=2015-12-20
type=post
tags=tour, lense
status=published
~~~~~~

# Sum Types

In Lense is possible to define an abstract type that must the implemented by a limited list of other types.
The classic example is the Node type :

~~~~brush: lense 
public class Node <T> {

	case class Brunch<T> {
	  constructor (Node<T> left , Node<T> right) 
	}
	
	case class Leaf <T> { 
	  constructor (T value)
	}
}
~~~~ 

If we now need to visit a long and complex tree we need to normally do so validation of types. For example, in java would be:

~~~~brush: java

public <T> void colectLeafs(Node<T> node, List<T> leafs){

	if (node instanceof Branch){
	     Branch b = (Branch)node);
	     collectLeafs(b.left, leafs);
	     collectLeafs(b.right, leafs);
	} else if (node instanceof Leaf){
	    leafs.add(((Leaf)node).value);
	}
}

~~~~

We need to make a decision based on the type of the object, then cast it, then capture the properties and use them.
In Lense we could use a similar construction using the flow-cast mechanism:

~~~~brunch> lense 

public void colectLeafs<T>(Node<T> node, List<T> leafs){

	if (node is Branch<T>){
	     collectLeafs(node.left, leafs);
	     collectLeafs(node.right, leafs);
	} else if (node is Leaf<T>){
	    leafs.add(node.value);
	}
}

~~~~

However the compiler would be blind to the fact ``Node`` is a sum type. If we did not had a ``else if`` to test for leafs the compiler 
would not complain.  If we need the be sure all sub types are covered we can use the ``switch`` statement.

~~~~brush: lense
public <T> void colectLeafs(Node<T> node, List<T> leafs){

	switch(node) {
		case Branch (b){
			collectLeafs(b.left, leafs);
	      collectLeafs(b.right, leafs);
		} 
		case Leaf (leaf){
			leafs.add(leaf.value);
		} 
	}
}
~~~~

#Limited Sum Types

Some sum types can be limited to just some subclasses. The compiler will proibit to define new subclasses.

~~~~brush: lense 
public class Node<T> is Brunch<T> | Leaf<T> {

	case class Brunch<T> {
	  constructor (Node<T> left , Node<T> right) 
	}
	
	case class Leaf <T> { 
	  constructor (T value)
	}
}
~~~~ 