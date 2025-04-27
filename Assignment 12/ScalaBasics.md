# Scala Basics to understand the code
### 1. **Variables and Data Types**
   - **Declaring Variables**: 
     
     - **`val`** (Immutable): Once assigned, the value cannot be changed.
       ```scala
       val x = 10  // Immutable variable
       ```
       
     - **`var`** (Mutable): The value can be reassigned.
       ```scala
       var y = 20  // Mutable variable
       y = 30      // Reassigned
       ```

   - **Basic Data Types**:
     - `Int`, `Double`, `String`, `Boolean`, `Char`, etc.
       ```scala
       val age: Int = 25
       val price: Double = 99.99
       val name: String = "Alice"
       val isActive: Boolean = true
       ```

   - **Type Inference**: Scala often infers the type based on the assigned value.
     ```scala
     val age = 25  // Scala infers 'age' to be of type Int
     ```

### 2. **Functions**
   - **Defining Functions**: Functions in Scala are defined using the `def` keyword.
     ```scala
     def add(a: Int, b: Int): Int = {
       a + b
     }
     println(add(2, 3))  // Output: 5
     ```

### 3. **Collections**
   - **Lists**: Immutable sequences of elements.
     ```scala
     val numbers = List(1, 2, 3, 4)
     println(numbers.head)   // Output: 1
     println(numbers.tail)   // Output: List(2, 3, 4)
     ```

   - **Mutable List**:
     ```scala
     import scala.collection.mutable.ListBuffer
     val mutableList = ListBuffer(1, 2, 3)
     mutableList += 4
     println(mutableList)  // Output: ListBuffer(1, 2, 3, 4)
     ```
### 4. **Control Structures**
   - **If-Else**:
     ```scala
     val x = 10
     if (x > 5) println("Greater") else println("Smaller or equal")
     ```

   - **Match (Pattern Matching)**:
     ```scala
     val num = 2
     num match {
       case 1 => println("One")
       case 2 => println("Two")
       case _ => println("Unknown")
     }
     ```

   - **Loops**:
     - **For loop** (including ranges):
       ```scala
       for (i <- 1 to 5) println(i)
       ```
     - **While loop**:
       ```scala
       var i = 0
       while (i < 5) {
         println(i)
         i += 1
       }
       ```

### 5. **Object-Oriented Basics**
   - **Classes and Objects**:
     - Classes are blueprints for objects.
     - Objects are single instances of classes.
     
     **Class**:
     ```scala
     class Person(val name: String, val age: Int) {
       def greet(): Unit = {
         println(s"Hello, my name is $name and I am $age years old.")
       }
     }
     val person1 = new Person("Alice", 25)
     person1.greet()  // Output: Hello, my name is Alice and I am 25 years old.
     ```

     **Object** (Singleton):
     ```scala
     object Calculator {
       def add(x: Int, y: Int): Int = x + y
     }
     println(Calculator.add(2, 3))  // Output: 5
     ```

---
