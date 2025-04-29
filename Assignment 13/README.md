## Scala Code Explaination


### Step 1: Imports
```scala
import spark.implicits._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
```

1. `import org.apache.spark.sql.SparkSession`:  
This imports `SparkSession` from the Spark library
It allows you to connect to Spark and do work like reading files, processing data, and writing results.

2. `import org.apache.spark.sql.functions._`:  
This imports all the functions you might need for working with **Spark DataFrames** — like `count()`, `groupBy()`, etc.
The underscore `_` means "import everything" inside that package.

3. `import spark.implicts._` :   
This makes your life easier by allowing automatic conversions between basic Scala types and Spark DataFrames.  
You can now treat DataFrames like Scala collections (lists, arrays, etc.).

### Step 2: Create Object

```scala
object WordCount {
    // your code goes here..
}
```
In Scala, an object is like a **single instance** (a singleton) that contains code you can run.
Here, we're creating an object called `WordCount` that will hold our whole Spark program.

### Step 3: Main Function

```scala
def main(args: Array[String]) : Unit {
    // your code goes here..
}
```

This is same as `public static void main(String[] args)` in Java    
`def main()` is a function that returns `Unit`  
`Unit` means this function returns nothing (equivalent to void in Java/C).  
`arg: Array[String]` means it can take an array of text arguments if needed.


### Step 4: Create a SparkSession

**SparkSession** is the main starting point of any Spark program.  
It is the way you connect to Spark so you can read data, process data, and save data.

```scala
val spark = SparkSession.builder
            .appName("wordCount")
            .master("local[*]")
            .getOrCreate()
```

You are:
- Building (`builder`) a SparkSession.
- Setting the app name to "wordCount" (for monitoring/logging).
- Running locally with all CPU cores (`local[*]`).
- Either getting an existing session or creating a new one (`getOrCreate()`).

### Step 5: Read a stream of text from a network socket

Create a live connection to listen to text data coming from your own computer (localhost) at port 9999, and capture whatever data comes into a variable called lines.

```scala
val lines = spark.readStream
                .format("socket")
                .option("host","localhost")
                .option("port",9999)
                .load()
```

- `spark.readStream`: Start reading streaming data (data that keeps coming continuously).
- `.format("socket")`: The source of the data is a network socket (not a file, not a database, not kafka).
- `.option("host", "localhost")`: Connect to the host named localhost.
- `.option("port", 9999)`: Look at port number 9999 to receive the data.
- `.load()`: Start the connection and begin listening for incoming data.

### Step 6: Mapper

Convert the incoming line of text into a single list of strings

```scala
val words = lines.as[String]
                .flatMap(line => line.split(" "))
```

Currently `lines` is a Dataset of text data, each row is a line of text (but Spark thinks it’s a **generic Row object**).
Example
```
Dataset[RowObject] (
    "this is line 1",
    "this is line 2",
    "this is line 3"
)
```

`.as[String]` tells Spark: "Hey, treat each line as a **String (plain text)** instead of a Row object."  

Example
```
Dataset[String] (
    "this is line 1",
    "this is line 2",
    "this is line 3"
)
```

`line => line.split(" ")` : For each line (which is now a String), split it into an array of words

Example
```
Array ("this","is","line","1")
```

`.flatMap` : Flatten all the arrays into one big list of words.

Example
```
Dataset[String]("this", "is", "line", "1","this", "is", "line", "2","this","is",...)
```

If we had used `.map` it would have saved as follows:
Example:
```
Array (
    Array("this", "is", "line", "1"),
    Array("this", "is", "line", "2"),
    Array("this", "is", "line", "3")
)
``

### Step 7: Reducer

```scala
val wordCount = words.groupBy("value").count()
```

Group all the rows (words) by their value — that is, by the word itself.  
For each group (i.e., for each unique word), count how many times it appears.  
Store the result (a table of word and count) into the variable `wordCount`.  

But... why `"value"`?
Because in Spark, when you have a `Dataset[String]`, the column name for the string is automatically called "value".  
**It's Spark's default behavior.**

### Step 8: Stream to console

```scala
val query = wordCount.writeStream
                    .outputMode("complete")
                    .format("console")
                    .start()
```

`.writeStream` : tells Spark that you want to write the data in streaming mode — meaning Spark will continually process and output data as it arrives, instead of doing a one-time batch process.

`.outputMode("complete")` : This defines how Spark should handle the output:

`.format("console")` : This defines where to output the result

`.start()` : Starts the streaming query.


### Step 9: Await termination

```scala
query.awaitTermination()
```
If we didn't use awaitTermination(), your program would exit immediately, stopping the streaming process. So, by calling awaitTermination(), you ensure that your Spark job runs until you manually stop it or it finishes processing.