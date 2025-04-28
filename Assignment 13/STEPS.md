# Assignment 13 Steps:

Step 1:    Open primary terminal and type spark-shell  

```bash
spark-shell
```

You should see something like this

```
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
25/04/27 17:37:43 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Spark context Web UI available at http://ip-172-31-12-196.ec2.internal:4040
Spark context available as 'sc' (master = local[*], app id = local-1745775465202).
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.5.5
      /_/
         
Using Scala version 2.12.18 (OpenJDK 64-Bit Server VM, Java 1.8.0_442)
Type in expressions to have them evaluated.
Type :help for more information.
scala > 
```
---

Step 2:    Inside of the scala repl (scala >) Paste this code    
```scala
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object WordCount {

    def main(args: Array[String]):Unit = {
        val spark = SparkSession.builder.appName("word count").master("local[*]").getOrCreate()
        import spark.implicits._
        val lines = spark.readStream.format("socket").option("host","localhost").option("port",9999).load()
        var words = lines.as[String].flatMap(line => line.split(" "))
        var count = words.groupBy("value").count()
        var query = count.writeStream.outputMode("complete").format("console").start()
        query.awaitTermination()
    }
}
```
---
Step 3:    Now open another terminal and type : nc -lk 9999    

```bash
nc -lk 9999
```

---
Step 4:    Comeback to primary terminal and type : WordCount.main(Array())    

```scala
WordCount.main(Array())
```
---

Step 5:    Once the program has started, go back to secondary terminal and type a sentence and hit enter    
```text
tirthraj mahajan
hello spark hello
tirthraj loves java
hi
hello
world
```
---
Step 6:    Back in primary terminal, you should see the list of words with their respective counts   

```bash
-------------------------------------------
Batch: 1
-------------------------------------------
+------+-----+
| value|count|
+------+-----+
|tirthraj| 1|
|mahajan| 1|
+------+-----+
.
.
.
-----------------------------------------
Batch: 6
-----------------------------------------
+------+-----+
| value|count|
+------+-----+
|tirthraj| 2|
|mahajan| 1|
|hello| 3|
|spark| 1|
|loves| 1|
|java| 1|
|hi| 1|
|world| 1|
+------+-----+
```