title=Properties
date=2016-04-26
type=post
tags=tour, lense
status=published
~~~~~~

# Properties

In Lense a type can have fields, methods and properties. Properties are invoked like fields but behave like methods. 
Properties have two methods associated it them: the accessor and the modifier. The acessor (the "get method") is the method that is invoked when we need the propertie's value. The modififer (the "set method") is the method that is invoked when we need to set or change the propertie's value. 

~~~~brush: lense

public class Person {

	public String name {get; set;} = "" // self-backing property initialized with the empty string
	public String surname { get; set;} = "" // self-backing property initialized with the empty string
	
	public String fullname { 
		get { 
			// concatenate the names
			return name + " " + surname; 
		}
		set (value){
			// split the names by a empty space
			val split = value.split(" ");
			this.name = split[0];
			this.surname = split[1];
		}
	}
}

// used like a field

val person = new Person();

person.name = "John"; // call modifier to set the value
person.surname = "Doe"; // call modifier to set the value

val fullname  = person.fullname; // call accessor to get the value

Console.print(fullname); // prints: John Doe
~~~~

The ``value`` is always of the type of the property and represents the value assigned to the property.


## Self-backing Properties 

Properties are really only a pair of methods so its value must be hold somewhere. 
The most common use of a property is a self-backing property, i.e. a property that handles the storage of the value automatically in a private field.

~~~~brush: lense

public class Person {

	public String name { get; set;} = "";
}

~~~~

Because this a very common case you can ommit the accessor and modifier declarations, like so:

~~~~brush: lense

public class Person {

	public String name {} = "";
}

~~~~

Either syntax will instruct the compiler to define a private, inaccessible, field and to store the value of the property there.

## Non Inialized Properties

You may omit the properie's initialization, however, because Lense as no ``null``, you will need to use an optional type.

~~~~brush: lense

public class Person {

	public String? name {}
}


~~~~


## Read-Only and Write-Only Properties

By omitting just the modifier, you can make a property read-only. If the property is read only the compiler will not define a private inaccessible field and body is necessary.

~~~~brush: lense

public class Person {

	public String name {}
	public String surname {}
	
	public String fullname { // a read only property
		get { return name + " " + surname; }
	}
}

~~~~

Less common is the use of write-only properties, but you can also define a write-only property by omitting the accessor declaration.

## Different visibility

Sometimes you need to have a public accessor but a private modifier, or vice-versa. In that case you can use visibility modifiers to declare diferent visibilities

 ~~~~brush: lense

public class Person {

	public String name { public get; private set;}
	public String surname { get; private set;}
}

~~~~

If you omit the modifier near the acessor or modifier declarations the visibility of the property will be used.
The visibility of the acessor or modifier may not be less restrictive than that of the property. So public get for a private property makes no sense. 


# Indexer Properties

<a name="indexed">Indexer properties</a> are anonymous properties that allow you to associate a value of the property with one or more values of parameters called indexes.
The most common examples are arrays that use numeric indexes or maps that use key object for indexes.

The rules of normal properties apply to indexer properties , you may need to define an accessor, or a modifier, or both.
Indexer properties are never self-backing.

Indexers are not limited to one index. In the next example we use an array to implement a matrix using a mathematical trick of calculating the cell
position in the array. We use the indexer property in the array to read and write values in the array.

~~~~brush: lense

public class Matrix<T> {

	private Natural rowsCount;
    private Natural columnsCount;
    
	private Array<T> cells = new Array<T>()

	public T [Natural row, Natural column] { 
		get {  
			return items[calculateCell(row, column)]; 
		}
		set (value){
		   items[calculateCell(row, column] = value;
		}
	}
	
	private Natural calculateCell(Natural row, Natural column){
		return row * rowsCount + column;
	}	
}

~~~~

An indexer as no name, so it is not possible to define two indexers with the exact same number and types of indexes. 

